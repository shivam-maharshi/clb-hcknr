package snip.adapter;

import java.util.List;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.PoliteHttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;

public class RedditAdapter {

	public List<Submission> getData(String keyword) {
		RestClient restClient = new PoliteHttpRestClient();
		restClient.setUserAgent("bot/1.0 by name");
		User user = new User(restClient, "shivam-maharshi", "shivam@123");
		try {
			user.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Submissions subms = new Submissions(restClient, user);
		return subms.ofSubreddit(keyword, SubmissionSort.TOP, -1, 10, null, null, true);
	}

	public static void main(String[] args) {
		RedditAdapter ra = new RedditAdapter();
		ra.getData("programming");
	}

}
