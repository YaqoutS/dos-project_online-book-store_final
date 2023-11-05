
import java.util.ArrayList;

public class Main {

	private static ArrayList<Book> catalog = new ArrayList<Book>();
	
	public static void main(String[] args) {
		catalog.add(new Book(1, "distributed systems", "How to get a good grade in DOS in 40 minutes a day", 22, 11));
		catalog.add(new Book(2, "distributed systems", "RPCs for Noobs", 22, 11));
		catalog.add(new Book(3, "undergraduate school", "Xen and the Art of Surviving Undergraduate School", 22, 11));
		catalog.add(new Book(4, "undergraduate school", "Cooking for the Impatient Undergrad", 22, 11));
		
	}
}
