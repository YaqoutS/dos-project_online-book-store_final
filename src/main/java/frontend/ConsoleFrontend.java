package frontend;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Response;
import spark.Route;

public class ConsoleFrontend {
	
	private static Gson gson = new Gson();

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
                System.out.println("Enter the book id (1 to 4):");
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
	            
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask the user for the service choice
        int serviceChoice;
        boolean exit = false;

        while(!exit) {
        	
        	serviceChoice = getServiceChoice(scanner);
        	
        	// perform functionality based on the user's service choice
            switch (serviceChoice) {
                case 1:
                    // search
                    String searchTopic = getSearchTopic(scanner);
                    System.out.println("Performing search for topic: " + searchTopic);
                    
                    // calling the search API
                    
                    
                    break;
                case 2:
                    // info
                    int bookID = getBookId(scanner);                 
                 
                    Book book = new Book();
                    System.out.println("Displaying info for book with id: " + bookID);
                    
                    // calling the info API
                    
                    
                    break;
                case 3:
                    // purchase
                    int bookIdPurchase = getBookId(scanner);
                    System.out.println("Processing purchase for book with id: " + bookIdPurchase);
                    
                    // calling the purchase API


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


