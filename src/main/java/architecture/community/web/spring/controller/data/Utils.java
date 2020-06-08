package architecture.community.web.spring.controller.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import architecture.community.model.Property;
import architecture.community.streams.Streams;
import architecture.community.streams.StreamsNotFoundException;
import architecture.community.streams.StreamsService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.ee.util.StringUtils;

public class Utils {

	protected final static String ME_STREAM_NAME = "ME";
	
	public static List<Property> toList(Map<String, String> properties) {
		
		List<Property> list = new ArrayList<Property>();
		for (String key : properties.keySet()) {
		    String value = properties.get(key);
		    list.add(new Property(key, value));
		} 
		return list;
	} 
	
	/**
	 * extract data-image-link attr  value as List in img tag.
	 * @param content
	 * @return
	 */
	public static List<String> getImageLinksFromHtml(String content){ 
		Document doc = Jsoup.parse(content);
		Elements elements = doc.select("img");
		List<String> list = new ArrayList<String>();
		for (Element element : elements ) {
			Map<String, String> data = element.dataset();
			if( element.hasAttr("data-image-link") ) {					
				list.add(element.attr("data-image-link"));
			} 
		}
		return list;
	}

	public static Streams getStreamsByNameCreateIfNotExist(StreamsService streamsService, String name) {
		Streams streams = null;
		try {
			streams = streamsService.getStreamsByName(name);
		} catch (StreamsNotFoundException e) {
			streams = streamsService.createStreams(name, name, name);
		}
		return streams;
	}
	
	
	public static final String ALLOWED_EVERYTHING_ROLES = "ROLE_ADMINISTRATOR,ROLE_SYSTEM,ROLE_DEVELOPER";
	
	/**
	 * Check permissions ..
	 * @param owner
	 * @param me
	 * @param roles
	 * @return
	 */
	public static boolean isAllowed(User owner, User me, String roles) {
		boolean isAllowed = false;
		if( !StringUtils.isNullOrEmpty(roles)) {
			if( SecurityHelper.isUserInRole(roles))
				isAllowed = true;
		}
		if( !isAllowed &&  owner.getUserId() > 0 && me.getUserId() > 0 &&  owner.getUserId() == me.getUserId() ) {
			isAllowed = true;
		}
		return isAllowed ;
	}
	
	public static boolean isAllowed(User owner, User me) {
		return isAllowed(owner, me, "ROLE_ADMINISTRATOR,ROLE_SYSTEM,ROLE_DEVELOPER,ROLE_OPERATOR");
		
	}
	
}
