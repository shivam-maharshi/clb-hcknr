package snip.adapter;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import facebook4j.auth.OAuthAuthorization;
import facebook4j.auth.OAuthSupport;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;

/**
 * Provides functionality to access Twitter APIs using Facebook4j library.
 * 
 * @author shivam.maharshi
 */
public class FacebookAdapter {

	public static Configuration createConfiguration() {
		ConfigurationBuilder confBuilder = new ConfigurationBuilder();
		confBuilder.setDebugEnabled(true);
		confBuilder.setOAuthAppId("1006336622766112");
		confBuilder.setOAuthAppSecret("2237fbece5d20d2057e0b64f6ebff66e");
		confBuilder.setUseSSL(true);
		confBuilder.setJSONStoreEnabled(true);
		Configuration configuration = confBuilder.build();
		return configuration;
	}
	
	public static void main(String[] argv) throws FacebookException {
		Configuration configuration = createConfiguration();
		FacebookFactory facebookFactory = new FacebookFactory(configuration);
		Facebook facebookClient = facebookFactory.getInstance();
		AccessToken accessToken = null;
		try {
			OAuthSupport oAuthSupport = new OAuthAuthorization(configuration);
			accessToken = oAuthSupport.getOAuthAppAccessToken();

		} catch (FacebookException e) {
			System.err.println("Error while creating access token " + e.getLocalizedMessage());
		}
		facebookClient.setOAuthAccessToken(accessToken);
		ResponseList<Post> results = facebookClient.searchPosts("watermelon");
		for (Post p : results) {
			System.out.println(p.getStory());
			System.out.println(p.getCaption());
			System.out.println(p.getMessage());
			System.out.println(p.getCreatedTime());
			System.out.println(p.getLink());
			System.out.println(p.getStory());
			System.out.println(p.getType());
			System.out.println(p.getSharesCount());
		}
	}

}
