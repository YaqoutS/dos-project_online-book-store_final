package frontend;

public class Book {

	private int id;
	private String topic;
	private String title;
	private int price;
	private int quantity;
		
	public Book() {
	}
	
	public Book(int id, String topic, String title, int price, int quantity) {
		super();
		this.id = id;
		this.topic = topic;
		this.title = title;
		this.price = price;
		this.quantity = quantity;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
