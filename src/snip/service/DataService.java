package snip.service;

import java.util.List;

import com.github.jreddit.entity.Submission;
import com.google.api.services.youtube.model.SearchResult;

import snip.adapter.RedditAdapter;
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
	RedditAdapter ra = new RedditAdapter();

	public Object getData(String videoId) {
		// Fetch data from YouTube.
		YTVideoInfo vInfo = map(videoId, getDataFromYouTube("v=" + videoId));
		// Fetch data from Twitter.
		QueryResult twitterData = getDataFromTwitter(vInfo);
		for (Status status : twitterData.getTweets()) {
			System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText() + " : "
					+ status.getRetweetCount() + " : " + status.getCreatedAt());
		}
		// Fetch data from Reddit.
		List<Submission> submissions = getDataFromReddit(vInfo);
		for (Submission s : submissions) {
			System.out.println(s.getAuthor() + ":" + s.getPermalink() + " : " + s.getSelftext() + " : " + s.getURL()
					+ " : " + s.getTitle() + " : " + s.getCreatedUTC() + " : " + s.getUrl());
		}
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

	private List<Submission> getDataFromReddit(YTVideoInfo vInfo) {
		StringBuilder sb = new StringBuilder();
		// VideoId inclusion search precision.
		sb.append(vInfo.getTitle()).append(" ").append(vInfo.getChannel());
		return ra.getData(sb.toString());
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
