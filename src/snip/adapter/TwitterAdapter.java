package snip.adapter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterAdapter {

	public QueryResult getData(String keyword) {
		Twitter twitter = TwitterFactory.getSingleton();
		Query query = new Query(keyword);
		query.setLang("en");
		QueryResult result = null;
		try {
			result = twitter.search(query);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		TwitterAdapter ta = new TwitterAdapter();
		ta.getData("CalvinHarris How Deep Is Your Love EgqUJOudrcM");
	}

}
