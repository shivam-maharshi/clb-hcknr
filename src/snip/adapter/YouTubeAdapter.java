package snip.adapter;

//import java.io.IOException;
//import java.util.Iterator;
//import java.util.List;
//
//import com.google.api.client.googleapis.json.GoogleJsonResponseException;
//import com.google.api.client.http.HttpRequest;
//import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.SearchListResponse;
//import com.google.api.services.youtube.model.SearchResult;

/**
 * Provides functionality to access YouTube APIs using Google Libraries.
 * 
 * @author shivam.maharshi
 */
public class YouTubeAdapter {

//	private static final long NUMBER_OF_VIDEOS_RETURNED = 1;
//	private static YouTube youtube;
//
//	public SearchResult getData(String query) {
//		SearchResult singleVideo = null;
//		try {
//			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
//				public void initialize(HttpRequest request) throws IOException {
//				}
//			}).setApplicationName("youtube-cmdline-search-sample").build();
//
//			YouTube.Search.List search = youtube.search().list("id,snippet");
//			String apiKey = "AIzaSyDhyoRlMtPQ-iycAWorrZ16J32s_srdByI";
//			search.setKey(apiKey);
//			search.setQ(query);
//			search.setType("video");
//			search.setFields(
//					"items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url, snippet/channelTitle)");
//			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
//			SearchListResponse searchResponse = search.execute();
//			List<SearchResult> searchResultList = searchResponse.getItems();
//			Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
//			while (iteratorSearchResults.hasNext()) {
//				singleVideo = iteratorSearchResults.next();
//			}
//		} catch (GoogleJsonResponseException e) {
//			System.err.println(
//					"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
//		} catch (IOException e) {
//			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
//		return singleVideo;
//	}
//
//	public static void main(String[] args) {
//		YouTubeAdapter yta = new YouTubeAdapter();
//		SearchResult sr = yta.getData("v=EgqUJOudrcM");
//		System.out.println();
//	}

}
