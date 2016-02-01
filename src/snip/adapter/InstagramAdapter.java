package snip.adapter;

import java.util.List;

//import org.jinstagram.Instagram;
//import org.jinstagram.auth.InstagramAuthService;
//import org.jinstagram.auth.model.Token;
//import org.jinstagram.auth.model.Verifier;
//import org.jinstagram.auth.oauth.InstagramService;
//import org.jinstagram.entity.tags.TagInfoData;
//import org.jinstagram.entity.tags.TagSearchFeed;
//import org.jinstagram.exceptions.InstagramException;

/**
 * Provides functionality to access Instagram APIs using jInstagram library.
 * 
 * @author shivam.maharshi
 */
public class InstagramAdapter {

//	private static final Token EMPTY_TOKEN = null;
//
//	public Object getData(String keyword) {
//		InstagramService service = new InstagramAuthService().apiKey("922892f48cc345acb42622426d849b35")
//				.apiSecret("	51f6944211df4b9fb5f8faeac1c58344").callback("https://github.com/shivam-maharshi")
//				.scope("comments").build();
//		String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
//		System.out.println(authorizationUrl);
//		Verifier verifier = new Verifier("c7acf080aad2482b9feb2c74932b26df");
//		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
//		Instagram instagram = new Instagram(accessToken);
//		String query = "snow";
//		TagSearchFeed searchFeed = null;
//		List<TagInfoData> tags = null;
//		try {
//			searchFeed = instagram.searchTags(query);
//		} catch (InstagramException e) {
//			e.printStackTrace();
//		}
//		tags = searchFeed.getTagList();
//		for (TagInfoData tagData : tags) {
//			System.out.println("name : " + tagData.getTagName());
//			System.out.println("media_count : " + tagData.getMediaCount());
//		}
//		return null;
//	}
//
//	public static void main(String[] args) {
//		InstagramAdapter ia = new InstagramAdapter();
//		ia.getData("CalvinHarris");
//	}

}
