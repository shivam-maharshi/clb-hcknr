package snip.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;

import snip.adapter.YouTubeAdapter;

public class DataService {

	public Object getData(String videoId) {
		SearchResult sr = getDataFromYouTube("v=" + videoId);
		return null;
	}

	private SearchResult getDataFromYouTube(String videoId) {
		YouTubeAdapter yta = new YouTubeAdapter();
		return yta.getData(videoId);
	}

	private Object getDataFromTwitter(String keyword) {
		return null;
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
