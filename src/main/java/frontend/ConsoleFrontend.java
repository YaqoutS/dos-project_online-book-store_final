package frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class ConsoleFrontend {

	private static Gson gson = new Gson();
	private static Cache<String, Object> cache = Caffeine.newBuilder().maximumSize(1000).build();

	// To decide whether the upcoming request should be directed to the primary or secondary catalog server...
	private static boolean usePrimaryCatalogServer = false;
	
	// To decide whether the upcoming request should be directed to the primary or secondary order server...
	private static boolean usePrimaryOrderServer = false;    
	

	private static int getServiceChoice(Scanner scanner) {
		int choice = 0;
		boolean isValidChoice = false;

		while (!isValidChoice) {
			try {
				System.out.println("Choose a service: 1-Search, 2-Info, 3-Purchase, 4-Exit");
				choice = scanner.nextInt();
				isValidChoice = choice >= 1 && choice <= 4;
			} catch (InputMismatchException e) {
				// user entered a non-integer value
				scanner.nextLine(); // Consume the invalid input
				System.out.println("Invalid input. Please enter a number.");
			}
		}

		return choice;
	}

	private static String getSearchTopic(Scanner scanner) {
		String topic;
		System.out.println("Enter the topic you want to search for:");
		scanner.nextLine().toLowerCase();
		topic = scanner.nextLine().toLowerCase();
		return topic;
	}

	private static int getBookId(Scanner scanner) {
		int id = -1;
		boolean isValidId = false;

		while (!isValidId) {
			try {
				System.out.println("Enter the book id (1 to 7):");
				id = scanner.nextInt();
				isValidId = true;
			} catch (InputMismatchException e) {
				// user entered a non-integer value
				scanner.nextLine(); // Consume the invalid input
				System.out.println("Invalid input. Please enter a number.");
			}
		}

		return id;
	}

	public static void cachePut(String key, Object value) {
		cache.put(key, value);
	}

	public static Object cacheGet(String key) {
		return cache.getIfPresent(key);
	}

	public static void cacheEvict(String key) {
		cache.invalidate(key);
	}

	private static List<BookDTO> searchAPICall(String topic) {

		List<BookDTO> books;
		books = (List<BookDTO>) cacheGet(topic);
		if ( books != null ) {
			System.out.println("Cache hit");
			return books;
		}
		System.out.println("Cache miss");
		
		try {
			String encodedTopic = URLEncoder.encode(topic, StandardCharsets.UTF_8.toString());
			String apiUrl = "http://localhost:4568/search/" + encodedTopic;
			if (!usePrimaryCatalogServer) {
				// forward request to the secondary catalog server
				apiUrl = "http://localhost:4569/search/" + encodedTopic;
			}
			usePrimaryCatalogServer = !usePrimaryCatalogServer;

			String apiResponse = "";

			// try to open a connection to the info API
			try {
				URL url = new URL(apiUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				// extract the response code
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					// read the info API response
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder responseStringBuilder = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						responseStringBuilder.append(line);
					}
					reader.close();

					// get the response as a string
					apiResponse = responseStringBuilder.toString();

					// converting the response to a book object
					TypeToken<List<BookDTO>> typeToken = new TypeToken<List<BookDTO>>() {
					};
					books = gson.fromJson(apiResponse, typeToken.getType());
					cachePut(topic, books);
				} else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
					System.out.println("No results found for the search topic: " + topic);
				} else {
					System.out.println("Unexpected response code: " + responseCode);
				}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error calling the API");
			}

			return books;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("Error encoding the search topic");
			return Collections.emptyList(); // Return an empty list on error
		}
	}

	private static int infoAPICall(int bookID, Book book) {
		Book returnedBook = (Book) cacheGet(String.valueOf(bookID));
		
		if ( returnedBook != null ) {
			System.out.println("Cache hit");
			book.setId(returnedBook.getId());
			book.setPrice(returnedBook.getPrice());
			book.setQuantity(returnedBook.getQuantity());
			book.setTitle(returnedBook.getTitle());
			book.setTopic(returnedBook.getTopic());
			return 200;
		}
		System.out.println("Cache miss");
		
		try {
			// URL of the info API we want to call
			String apiUrl = "http://localhost:4568/info/" + bookID;
			if (!usePrimaryCatalogServer) {
				// forward request to the secondary catalog server
				apiUrl = "http://localhost:4569/info/" + bookID;
			}
			usePrimaryCatalogServer = !usePrimaryCatalogServer;

			// open a connection to the info API
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// extract the response code
			int responseCode = connection.getResponseCode();

			// check if the response code indicates not found (HTTP 404 OK)
			if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
				return 404;
			}

			// read the info API response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder responseStringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				responseStringBuilder.append(line);
			}
			reader.close();

			// get the response as a string
			String apiResponse = responseStringBuilder.toString();

			// convert the response to a book object and updating the original book object
			returnedBook = gson.fromJson(apiResponse, Book.class);
			book.setId(returnedBook.getId());
			book.setPrice(returnedBook.getPrice());
			book.setQuantity(returnedBook.getQuantity());
			book.setTitle(returnedBook.getTitle());
			book.setTopic(returnedBook.getTopic());

		} catch (IOException e) {
			e.printStackTrace();
			return 500;
		}
		cachePut(String.valueOf(bookID), book);
		return 200;
	}

	private static String purchaseAPICall(int bookID) {
		// int orderID = -1;

		StringBuilder response = new StringBuilder();
		try {
			String apiUrl = "http://localhost:4567/purchase/" + bookID;
			if (!usePrimaryOrderServer) {
				// forward request to the secondary order server
				apiUrl = "http://localhost:4566/purchase/" + bookID;
			}
			usePrimaryOrderServer = !usePrimaryOrderServer;

			// open a connection to the info API
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");

			// read the response content
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			response = new StringBuilder();
			String line2;
			while ((line2 = reader2.readLine()) != null) {
				response.append(line2);
			}
			reader2.close();

		} catch (IOException e) {
			e.printStackTrace();
			return response.toString();
		}

		return response.toString();
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Ask the user for the service choice
		int serviceChoice;
		boolean exit = false;

		while (!exit) {

			serviceChoice = getServiceChoice(scanner);

			// perform functionality based on the user's service choice
			switch (serviceChoice) {
			case 1:
				// search

				long startTime = System.currentTimeMillis();
				String searchTopic = getSearchTopic(scanner);
				long endTime = System.currentTimeMillis();
				long elapsedTime = endTime - startTime;
				System.out.println("Response time: " + elapsedTime + " milliseconds");

				System.out.println("Performing search for topic: " + searchTopic);

				// calling the search API
				List<BookDTO> searchResults = searchAPICall(searchTopic);

				// process the search results
				if (searchResults != null && !searchResults.isEmpty()) {
					System.out.println("Search results:");
					for (BookDTO book : searchResults) {
						System.out.println(book); // Assuming BookDTO has a meaningful toString() method
					}
				}

				break;
			case 2:
				// info
				int bookID = getBookId(scanner);

				Book book = new Book();
				System.out.println("Displaying info for book with id: " + bookID);

				// calling the info API

				startTime = System.currentTimeMillis();
				int infoStatusCode = infoAPICall(bookID, book);
				endTime = System.currentTimeMillis();
				elapsedTime = endTime - startTime;
				System.out.println("Response time: " + elapsedTime + " milliseconds");

				if (infoStatusCode == 404) {
					System.out.println("There is no book with ID = " + bookID);
				} else if (infoStatusCode == 500) {
					System.out.println("Request Faild.");
				} else {
					System.out.println(book);
				}

				break;
			case 3:
				// purchase
				int bookIdPurchase = getBookId(scanner);
				System.out.println("Processing purchase for book with id: " + bookIdPurchase);

				// calling the purchase API
				startTime = System.currentTimeMillis();
				String resutl = purchaseAPICall(bookIdPurchase);
				endTime = System.currentTimeMillis();
				elapsedTime = endTime - startTime;
				System.out.println("Response time: " + elapsedTime + " milliseconds");

				System.out.println(resutl);

				break;
			case 4:
				exit = true;
				System.out.println("Exit");
				break;
			default:
				System.out.println("Invalid choice. Please choose a number between 1 and 4.");
			}
		}
	}
}
