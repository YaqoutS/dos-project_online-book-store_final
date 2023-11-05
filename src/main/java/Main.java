import static spark.Spark.*;

import java.util.ArrayList;

import com.google.gson.Gson;

public class Main {

	private static Gson gson = new Gson();
	
	private static ArrayList<Book> catalog = new ArrayList<Book>();
	public static void main(String[] args) {
		catalog.add(new Book(1, "distributed systems", "How to get a good grade in DOS in 40 minutes a day", 22, 11));
		catalog.add(new Book(2, "distributed systems", "RPCs for Noobs", 22, 11));
		catalog.add(new Book(3, "undergraduate school", "Xen and the Art of Surviving Undergraduate School", 22, 11));
		catalog.add(new Book(4, "undergraduate school", "Cooking for the Impatient Undergrad", 22, 11));
		
		get("/search/:topic", (req, res) -> {
			String topic = req.params(":topic");
			ArrayList<BookDTO> books = new ArrayList<BookDTO>();
			for(Book book: catalog) {
				if(book.getTopic().equals(topic)) books.add(new BookDTO(book));
			}
			return gson.toJson(books);
		});
		
		get("/info/:bookID", (req, res) -> {
				int id = Integer.parseInt(req.params(":bookID"));
				for(Book book: catalog) {
					if(book.getId() == id) return gson.toJson(book);
				}
				return "There is no book with id = " + id;
		});
	}
}
