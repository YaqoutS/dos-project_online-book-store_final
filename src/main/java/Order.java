
public class Order {
	private int orderID;
	private int bookID;

	public Order(int orderId, int bookId) {
		orderID = orderId;
		bookID = bookId;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public int getBookID() {
		return bookID;
	}

	public void setBookID(int bookID) {
		this.bookID = bookID;
	}
	
}

