package snip.service;

import com.google.api.services.youtube.model.SearchResult;

import snip.adapter.TwitterAdapter;
import snip.adapter.YouTubeAdapter;
import snip.pojo.YTVideoInfo;
import twitter4j.QueryResult;
import twitter4j.Status;

/**
 * Provides singular service for fetching data from multiple social network end
 * points.
 * 
 * @author shivam.maharshi
 */
public class DataService {
	YouTubeAdapter yta = new YouTubeAdapter();
	TwitterAdapter ta = new TwitterAdapter();

	public Object getData(String videoId) {
		// Fetch data from YouTube.
		YTVideoInfo vInfo = map(videoId, getDataFromYouTube("v=" + videoId));
		// Fetch data from Twitter.
		QueryResult twitterData = getDataFromTwitter(vInfo);
		for (Status status : twitterData.getTweets()) {
			System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText() + " : "
					+ status.getRetweetCount() + " : " + status.getCreatedAt());
		}
		// Fetch data from Facebook.
		// Fetch data from LinkedIn.
		// Fetch data from Quora.
		// Fetch data from StackOverFlow.
		// Fetch data from Wikipedia.
		return null;
	}

	private YTVideoInfo map(String videoId, SearchResult sr) {
		String channelName = sr.getSnippet().getChannelTitle();
		if (channelName.endsWith("VEVO") || channelName.endsWith("vevo"))
			// Fetch artist name. Improves search results.
			channelName = channelName.substring(0, channelName.length() - 4);
		YTVideoInfo vInfo = new YTVideoInfo(videoId, channelName, sr.getSnippet().getTitle());
		return vInfo;
	}

	private SearchResult getDataFromYouTube(String videoId) {
		return yta.getData(videoId);
	}

	private QueryResult getDataFromTwitter(YTVideoInfo vInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append(vInfo.getTitle()).append(" ").append(vInfo.getChannel()).append(" ").append(vInfo.getVideoId());
		QueryResult result = ta.getData(sb.toString());
		return result;
	}

	private Object getDataFromFacebook(String keyword) {
		return null;
	}

	private Object getDataFromLinkedIn(String keyword) {
		return null;
	}

	private Object getDataFromStackOverFlow(String keyword) {
		return null;
	}

	private Object getDataFromQuora(String keyword) {
		return null;
	}

}
