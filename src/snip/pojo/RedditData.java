package snip.pojo;

/**
 * Data holder for Reddit data.
 * 
 * @author shivam.maharshi
 */
public class RedditData {

	private String name;
	private String title;
	private String date;
	private String link;

	public RedditData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RedditData(String name, String title, String date, String link) {
		super();
		this.name = name;
		this.title = title;
		this.date = date;
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
