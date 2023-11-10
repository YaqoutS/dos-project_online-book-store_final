import static spark.Spark.*;

import java.util.ArrayList;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {

	private static Gson gson = new Gson();

	private static ArrayList<Book> catalog = new ArrayList<Book>();

	public static void connect() {
		Connection conn = null;
		try {
			// db path
			String url = "jdbc:sqlite:catalog.db";

			// SQL statement for creating a new table
			String sql = "CREATE TABLE IF NOT EXISTS book (\n" + "	id INT NOT NULL,\n"
					+ "	topic VARCHAR(500) NOT NULL,\n" + "	title VARCHAR(500) NOT NULL,\n" + "	price INT NOT NULL,\n"
					+ "	quantity INT NOT NULL,\n" + " PRIMARY KEY (id)" + ");";

			// create a connection to the database
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to SQLite has been established.");

			Statement stmt = conn.createStatement();

			// create a new table
			stmt.execute(sql);

			// insert books into the database
//          String SQLquery = "insert into book values('1', 'distributed systems', 'How to get a good grade in DOS in 40 minutes a day', '30', '10');";
//          stmt.executeUpdate(SQLquery);
//			SQLquery = "insert into book values ('2', 'distributed systems', 'RPCs for Noobs', '30', '10');";
//			stmt.executeUpdate(SQLquery);
//			SQLquery = "insert into book values ('3', 'undergraduate school', 'Xen and the Art of Surviving Undergraduate School.', '30', '10');";
//			stmt.executeUpdate(SQLquery);
//			SQLquery = "insert into book values('4', 'undergraduate school', 'Cooking for the Impatient Undergrad', '30', '10');";
//			stmt.executeUpdate(SQLquery);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {

		connect();

		get("/search/:topic", (req, res) -> {
			// extracting the 'topic' value from the url
			String requestTopic = req.params(":topic");
			ArrayList<BookDTO> books = new ArrayList<BookDTO>();
			String sql = "SELECT * From Book";
			try {
				String url = "jdbc:sqlite:catalog.db";
				Connection conn = DriverManager.getConnection(url);
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(sql);

				// looping through the result to find the books with the requested topic
				while (result.next()) {
					String id = result.getString("id");
					String topic = result.getString("topic");
					if (topic.equals(requestTopic)) {
						books.add(new BookDTO(Integer.parseInt(id), topic));
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

			return gson.toJson(books);
		});

		get("/info/:bookID", (req, res) -> {

			// extracting the 'bookID' value from the url
			int requestId = Integer.parseInt(req.params(":bookID"));

			String sql = "SELECT * From Book";
			try {
				String url = "jdbc:sqlite:catalog.db";
				Connection conn = DriverManager.getConnection(url);
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(sql);

				// looping through the result to find the book with the requested id
				while (result.next()) {
					int id = result.getInt("id");
					String topic = result.getString("topic");
					String title = result.getString("title");
					int quantity = result.getInt("quantity");
					int price = result.getInt("price");
					if (id == requestId) {
						Book book = new Book(id, topic, title, price, quantity);
						return gson.toJson(book);
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

			return "There is no book with id = " + requestId;
		});
	}
}
