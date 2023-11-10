
public class BookDTO {
	private int id;
	private String title;
	
	public BookDTO(int id, String title) {
		super();
		this.id = id;
		this.title = title;
	}
	public BookDTO(Book book) {
		this.id = book.getId();
		this.title = book.getTitle();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
