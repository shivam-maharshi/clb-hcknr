package snip.pojo;

/**
 * Data holder class for Instagram data.
 * 
 * @author shivam.maharshi
 */
public class InstagramData {

	private String name;
	private String status;
	private String date;
	private String link;

	public InstagramData() {
		super();
	}

	public InstagramData(String name, String status, String date, String link) {
		super();
		this.name = name;
		this.status = status;
		this.date = date;
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
