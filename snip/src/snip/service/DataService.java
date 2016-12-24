package snip.service;

import java.util.Date;
import java.util.List;

import com.github.jreddit.entity.Submission;

import snip.adapter.RedditAdapter;
import snip.adapter.TwitterAdapter;
import snip.pojo.Data;
import snip.pojo.RedditData;
import snip.pojo.TwitterData;
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
	// YouTubeAdapter yta = new YouTubeAdapter();
	TwitterAdapter ta = new TwitterAdapter();
	RedditAdapter ra = new RedditAdapter();

	public Data getData(String videoId) {
		// Fetch data from YouTube.
		// YTVideoInfo vInfo = map(videoId, getDataFromYouTube("v=" + videoId));
		// Fetch data from Twitter.
		YTVideoInfo vInfo = new YTVideoInfo(videoId, "CalvinHarrisVevo", "How Deep Is Your Love");
		QueryResult twitterData = getDataFromTwitter(vInfo);
		TwitterData[] td = map(twitterData);
		// Fetch data from Reddit.
		List<Submission> submissions = getDataFromReddit(vInfo);
		RedditData[] rd = map(submissions);
		return new Data(td, rd);
	}

	private TwitterData[] map(QueryResult twitterData) {
		TwitterData[] td = new TwitterData[10];
		for (int i = 0; i < twitterData.getTweets().size() && i < 10; i++) {
			Status status = twitterData.getTweets().get(i);
			td[i] = new TwitterData("@" + status.getUser().getScreenName(), status.getText(),
					status.getCreatedAt().toString());
		}
		return td;
	}

	private RedditData[] map(List<Submission> submissions) {
		RedditData[] rd = new RedditData[10];
		for (int i = 0; i < 10 && i < submissions.size(); i++) {
			Submission s = submissions.get(i);
			rd[i] = new RedditData(s.getAuthor(), s.getTitle(), new Date(s.getCreatedUTC().longValue()).toString(),
					s.getPermalink());
		}
		return rd;
	}

	// private YTVideoInfo map(String videoId, SearchResult sr) {
	// String channelName = sr.getSnippet().getChannelTitle();
	// if (channelName.endsWith("VEVO") || channelName.endsWith("vevo"))
	// // Fetch artist name. Improves search results.
	// channelName = channelName.substring(0, channelName.length() - 4);
	// YTVideoInfo vInfo = new YTVideoInfo(videoId, channelName,
	// sr.getSnippet().getTitle());
	// return vInfo;
	// }

	// private SearchResult getDataFromYouTube(String videoId) {
	// return yta.getData(videoId);
	// }

	private QueryResult getDataFromTwitter(YTVideoInfo vInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append(vInfo.getTitle()).append(" ").append(vInfo.getVideoId());
		QueryResult result = ta.getData(sb.toString());
		return result;
	}

	private List<Submission> getDataFromReddit(YTVideoInfo vInfo) {
		StringBuilder sb = new StringBuilder();
		// VideoId inclusion search precision.
		sb.append(vInfo.getTitle()).append(" ").append(vInfo.getChannel());
		return ra.getData(sb.toString());
	}

}
