package snip.pojo;

/**
 * Data holder class for the final output.
 * 
 * @author shivam.maharshi
 */
public class Data {

	TwitterData[] twitter;
	RedditData[] reddit;

	public Data() {
		super();
	}

	public Data(TwitterData[] twitter, RedditData[] reddit) {
		super();
		this.twitter = twitter;
		this.reddit = reddit;
	}

	public TwitterData[] getTwitter() {
		return twitter;
	}

	public void setTwitter(TwitterData[] twitter) {
		this.twitter = twitter;
	}

	public RedditData[] getReddit() {
		return reddit;
	}

	public void setReddit(RedditData[] reddit) {
		this.reddit = reddit;
	}

}
