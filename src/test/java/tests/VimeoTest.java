package tests;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import architecture.community.services.vimeo.VimeoApi;


public class VimeoTest {

	private static Logger log = LoggerFactory.getLogger(VimeoTest.class);
	
	private static final String VIMEO_SERVER = "https://api.vimeo.com";
	
	@Test
	public void testVimeo() {
		
		try {
			String videoId = "331388915";
			
				//6e7f8638d0aa255f19bc06f56fcc6934
			
			final String secretState = "6e7f8638d0aa255f19bc06f56fcc6934";
	        final String clientId = "3ec21f839dbfbd883394bec3328dcaa3550f4c49";
	        final String clientSecret = "V2vifC+R6gEvZ1rpjrPSXxOhanZH5RBpGH+P1ZYf8KaT+OtWNKLhEc0khcl+Pxprj6czpDZkvzcIFYgPHFZvuEICjDwSd4MqIv1fzhKru93xTK/1vXsFqP8VEkyJzikH";
	        //final String secretState = "secret" + new Random().nextInt(999_999);
	        
			// API 키를 사용하여 서비스 생성 > API 비밀키 지정하여 서비스 객체를 생성.
			OAuth20Service service = new ServiceBuilder(clientId)  
	                .apiSecret(clientSecret) //Client secrets
			        .build(VimeoApi.instance()); 
			
			// 호울 서비스 API 
			String url = String.format("https://api.vimeo.com/videos/%s", videoId);
			System.out.println("Now we're going to access a protected resource..."); 
			OAuthRequest request = new OAuthRequest(Verb.GET, url);
			
			//OAuth 인증을 위하여 매 서비스 호출마다 인증 토큰을 세팅하여 API 를 호출. 
			OAuth2AccessToken accessToken = new OAuth2AccessToken(secretState);
	        service.signRequest(accessToken, request);
	        Response response = service.execute(request);

	        log.debug(String.valueOf(response.getBody().toString())); 
	         
	        JsonObject jsonObject = (JsonObject) JsonParser.parseString(String.valueOf(response.getBody().toString()));
	        if(!jsonObject.get("transcode").isJsonNull()) {
	        	JsonObject transcode_obj = (JsonObject) jsonObject.get("transcode");
				String transcode = transcode_obj.get("status").getAsString();
				log.debug(transcode);
	        }
	        if(!jsonObject.get("pictures").isJsonNull()) { 
	        	JsonObject thumbnail_obj = (JsonObject) jsonObject.get("pictures");
				String thumbnail_uri = thumbnail_obj.get("uri").getAsString();
				thumbnail_uri = thumbnail_uri.substring(thumbnail_uri.lastIndexOf("/"));
				String thumbnail_url = "https://i.vimeocdn.com/video"+thumbnail_uri;
				log.debug(thumbnail_url);
	        } 
	        
			int duration = jsonObject.get("duration").getAsInt(); 
			
			log.debug("영상길이 : "+duration);
			
			} catch(Throwable e) {
				e.printStackTrace();
			}
		
	}
	
	protected void call (OAuth2AccessToken accessToken, String endpoint, String method, Map<String, String> params ) {
		
		String url = null;
		if (endpoint.startsWith("http")) {
			url = endpoint;
		} else {
			url = new StringBuffer(VIMEO_SERVER).append(endpoint).toString();
		} 
		OAuthRequest request = new OAuthRequest(Verb.GET, endpoint);
	}
	
}
