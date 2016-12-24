package snip.pojo;

/**
 * Twitter data holder class.
 * 
 * @author shivam.maharshi
 */
public class TwitterData {

	private String name;
	private String tweet;
	private String date;

	public TwitterData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TwitterData(String name, String tweet, String date) {
		super();
		this.name = name;
		this.tweet = tweet;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
