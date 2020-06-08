package architecture.community.web.spring.controller.data;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.user.AvatarImage;
import architecture.community.user.UserAvatarService;
import architecture.community.user.UserManager;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;

@Controller("community-user-data-controller")
public class UserDataController {

	private Logger log = LoggerFactory.getLogger(UserDataController.class);
	
	@Autowired(required=false)
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired(required = false) 
	@Qualifier("userAvatarService")
	private UserAvatarService userAvatarService;
	
	@Autowired(required = false) 
	@Qualifier("userManager")
	private UserManager userManager;
	
	private boolean isSetUserAvatarService() {
		return userAvatarService != null ? true : false;
	}
	
	private com.google.common.cache.LoadingCache<String, AvatarImage> avatars = null;
	 
	@PostConstruct
	public void initialize(){		
		log.debug("creating cache ...");		
		avatars = CacheBuilder.newBuilder().maximumSize(5000).expireAfterAccess(1, TimeUnit.MINUTES).build(		
				new CacheLoader<String, AvatarImage>(){
 					public AvatarImage load(String username) throws Exception {
						AvatarImage image = null;
						if( StringUtils.isNotEmpty(username))
						try {
							image = userAvatarService.getAvatareImageByUsername(username);
						} catch (Exception e) { 
							image = new AvatarImage();
						} 
						return image ;
					}			
				}
			);
	}
	
	public UserDataController() {

	}	
	
	private boolean hasAvatarImageInCache ( String username ) {
		if( !isSetUserAvatarService() )
			return false;  
		if( avatars != null) {
			AvatarImage avatar = avatars.getIfPresent(username);
			if( avatar == null) {
				try {
					avatar = avatars.get(username); 
				} catch (ExecutionException e) {
				}
			}
			return avatar.getAvatarImageId() > 0 ? true : false;
		}
		return false;
	}
	
	@RequestMapping(value = "/download/avatar/{username:.+}", method = RequestMethod.GET)
	@ResponseBody
	public void downloadUserAvatarImage (
			@PathVariable("username") String username, 
			@RequestParam(value = "width", defaultValue = "0", required = false) Integer width,
		    @RequestParam(value = "height", defaultValue = "0", required = false) Integer height,
		    HttpServletRequest request,
		    HttpServletResponse response) { 
		try {
			
			if(!hasAvatarImageInCache( username ))
			{	
				log.debug("not found avata for {}", username);
				ResourceUtils.noAvatars(request, response);
			} 
			//AvatarImage image = userAvatarService.getAvatareImageByUsername(username);  
			AvatarImage image = avatars.getIfPresent(username);
			if (image != null && image.getAvatarImageId() > 0) {
				InputStream input;
				String contentType;
				int contentLength;				
				if (width > 0 && width > 0) {
					input = userAvatarService.getImageThumbnailInputStream(image, width, height);
					contentType = image.getThumbnailContentType();
					contentLength = image.getThumbnailSize();
				} else {
					input = userAvatarService.getImageInputStream(image);
					contentType = image.getImageContentType();
					contentLength = image.getImageSize();
				}				
				response.setContentType(contentType);
				response.setContentLength(contentLength);
				IOUtils.copy(input, response.getOutputStream());
				response.flushBuffer();
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.setStatus(301);
			String url = ServletUtils.getContextPath(request) + configService.getApplicationProperty("components.download.images.no-avatar-url", "/images/no-avatar.png");
			response.addHeader("Location", url);
		}
	}	
}
