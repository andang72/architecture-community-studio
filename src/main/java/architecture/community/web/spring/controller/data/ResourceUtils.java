package architecture.community.web.spring.controller.data;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ResourceUtils {


	private static final String JPEG_CONTENT_TYPE = "image/jpeg"; 
	private static final String PNG_CONTENT_TYPE = "png/jpeg"; 
	private static final String IMAGES_NO_THUMBNAIL = "assets/images/no-thumbnail.jpg";
	private static final String IMAGES_NO_AVATAR = "assets/images/no-avatar.png";
	private static final String IMAGES_NOT_AVAILABLE = "assets/images/no-image-available.png";
	private static final String IMAGES_NO_ACCESS_WITH_PERMISSION_500_500 = "assets/images/personal_use_only.png";
	
	public static void notAccessWithPermission(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ClassPathResource resource = new ClassPathResource(IMAGES_NO_ACCESS_WITH_PERMISSION_500_500);
		image (resource, JPEG_CONTENT_TYPE, request, response);
	}
	
	public static void notAavaliable(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ClassPathResource resource = new ClassPathResource(IMAGES_NOT_AVAILABLE);
		image (resource, PNG_CONTENT_TYPE, request, response);
	}
	
	public static void noThumbnails(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ClassPathResource resource = new ClassPathResource(IMAGES_NO_THUMBNAIL);
		image (resource, JPEG_CONTENT_TYPE, request, response);
	} 

	public static void noAvatars(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ClassPathResource resource = new ClassPathResource(IMAGES_NO_AVATAR);
		image (resource, PNG_CONTENT_TYPE, request, response);
	} 
	
	public static void image (Resource resource, String contentType,  HttpServletRequest request, HttpServletResponse response) throws IOException {
		if( resource.exists() ) {
			InputStream input = resource.getInputStream();
			int length = input.available();
			response.setContentType(contentType);
			response.setContentLength(length);
			IOUtils.copy(input, response.getOutputStream());
			response.flushBuffer(); 
		}
	} 	
}
