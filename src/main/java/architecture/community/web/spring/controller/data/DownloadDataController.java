package architecture.community.web.spring.controller.data;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.AttachmentService;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageNotFoundException;
import architecture.community.image.ImageService;
import architecture.community.image.ThumbnailImage;
import architecture.community.model.Models;
import architecture.community.security.spring.acls.CommunityAclService;
import architecture.community.security.spring.acls.PermissionsBundle;
import architecture.community.share.SharedLink;
import architecture.community.share.SharedLinkService;
import architecture.community.user.User;
import architecture.community.util.CommunityConstants;
import architecture.community.util.SecurityHelper;
import architecture.community.viewcount.ViewCountService;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;
import architecture.ee.util.StringUtils;

@Controller("community-download-data-controller")
@RequestMapping("/download")
public class DownloadDataController {

	private Logger log = LoggerFactory.getLogger(DownloadDataController.class);

	@Inject
	@Qualifier("imageService")
	private ImageService imageService;

	@Inject
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired(required = false)
	@Qualifier("aclService")
	private CommunityAclService aclService;

	@Autowired(required = false)
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;

	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;

	@Autowired(required = false)
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;

	private boolean isAllowed(Image image) throws NotFoundException {
		
		User currentUser = SecurityHelper.getUser();
		if( image.getUser() != null && image.getUser().getUserId() > 0 && currentUser.getUserId() == image.getUser().getUserId() )
		{
			return true;
		}
		if (SecurityHelper.isUserInRole("ROLE_DEVELOPER, ROLE_ADMINISTRATOR, ROLE_SYSTEM")) {
			return true;
		}
		
		ImageLink link = imageService.getImageLink(image);
		if (link.isPublicShared()) {
			return true;
		}
		PermissionsBundle bundle = aclService.getPermissionBundle(SecurityHelper.getAuthentication(), Models.IMAGE.getObjectClass(), image.getImageId());
		if (bundle.isRead())
			return true;
		return false;
	}

	private boolean isAllowed(Attachment attachment) throws NotFoundException {
		User currentUser = SecurityHelper.getUser();
		if( attachment.getUser() != null && attachment.getUser().getUserId() > 0 && currentUser.getUserId() == attachment.getUser().getUserId() )
		{
			return true;
		}
		if (SecurityHelper.isUserInRole("ROLE_DEVELOPER, ROLE_ADMINISTRATOR, ROLE_SYSTEM")) {
			return true;
		}
		SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId(), false);
		if (link.isPublicShared()) {
			return true;
		}
		PermissionsBundle bundle = aclService.getPermissionBundle(SecurityHelper.getAuthentication(), Models.ATTACHMENT.getObjectClass(), attachment.getAttachmentId());
		if (bundle.isRead())
			return true;
		return false;
	}

	/**
	 * 인자로 전달된 BASE64 데이터를 파일 형태로 변환하여 응답한다.
	 * 
	 * @param fileName
	 * @param base64
	 * @param contentType
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/proxy", method = RequestMethod.POST)
	@ResponseBody
	public void save(String fileName, String base64, String contentType, HttpServletResponse response)
			throws IOException {
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		response.setContentType(contentType);
		byte[] data = DatatypeConverter.parseBase64Binary(base64);
		response.setContentLength(data.length);
		response.getOutputStream().write(data);
		response.flushBuffer();
	}

	public static class EncodeContent {
		int size ;
		String contentType;
		String base64;
		public EncodeContent(String base64, String contentType, int size) { 
			this.base64 = base64;
			this.contentType = contentType;
			this.size = size;
		}
		
	}
	
	@RequestMapping(value = "/images/{linkId}", method = RequestMethod.GET)
	@ResponseBody
	public void downloadImageByLink(@PathVariable("linkId") String linkId,
			@RequestParam(value = "thumbnail", defaultValue = "false", required = false) boolean thumbnail,
			@RequestParam(value = "width", defaultValue = "150", required = false) Integer width,
			@RequestParam(value = "height", defaultValue = "150", required = false) Integer height,
			@RequestParam(value = "encode", defaultValue = "false", required = false) boolean encode,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {

			Image image = imageService.getImageByImageLink(linkId);
			// checking security ..
			if (image != null) {
				
				if ( !thumbnail && !isAllowed(image))
					throw new UnAuthorizedException(); 
				
				InputStream input;
				String contentType;
				int contentLength;
				
				if (thumbnail) {
					input = imageService.getImageThumbnailInputStream(image, width, height);
					contentType = image.getThumbnailContentType();
					contentLength = image.getThumbnailSize();
				} else {
					input = imageService.getImageInputStream(image);
					contentType = image.getContentType();
					contentLength = image.getSize();
				} 
				response.setContentType(contentType);
				response.setContentLength(contentLength);
				IOUtils.copy(input, response.getOutputStream());
				response.flushBuffer();
				
			} 
		} catch (UnAuthorizedException uae ) {
			response.setStatus( HttpStatus.UNAUTHORIZED.value() );
			ResourceUtils.notAccessWithPermission(request, response);	
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.setStatus(HttpStatus.NOT_FOUND.value());
			if(thumbnail )
				ResourceUtils.noThumbnails(request, response);
			else
				ResourceUtils.notAavaliable(request, response);
		}
	}

	@RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/{filename:.+}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void downloadImage(@PathVariable("imageId") Long imageId, @PathVariable("filename") String filename,
			@RequestParam(value = "thumbnail", defaultValue = "false", required = false) boolean thumbnail,
			@RequestParam(value = "width", defaultValue = "150", required = false) Integer width,
			@RequestParam(value = "height", defaultValue = "150", required = false) Integer height,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			if (imageId <= 0 && StringUtils.isNullOrEmpty(filename)) {
				throw new IllegalArgumentException();
			}
			log.debug("name {} decoded {}.", filename, ServletUtils.getEncodedFileName(filename));
			Image image = imageService.getImage(imageId);
			log.debug("checking equals plain : {} , decoded : {} ",
					org.apache.commons.lang3.StringUtils.equals(filename, image.getName()),
					org.apache.commons.lang3.StringUtils.equals(ServletUtils.getEncodedFileName(filename), 
					image.getName()));
			
			if (!isAllowed(image))
				throw new UnAuthorizedException();
			
			InputStream input;
			String contentType;
			int contentLength;
			if (width > 0 && width > 0 && thumbnail) {
				input = imageService.getImageThumbnailInputStream(image, width, height);
				if( input == null)
					throw new ImageNotFoundException();  
				contentType = image.getThumbnailContentType();
				contentLength = image.getThumbnailSize();
			} else {
				input = imageService.getImageInputStream(image);
				if( input == null)
					throw new ImageNotFoundException(); 
				contentType = image.getContentType();
				contentLength = image.getSize();
			}
			response.setContentType(contentType);
			response.setContentLength(contentLength);
			IOUtils.copy(input, response.getOutputStream());
			response.flushBuffer();
			
		} catch (UnAuthorizedException uae ) {
			response.setStatus( HttpStatus.UNAUTHORIZED.value() );
			ResourceUtils.notAccessWithPermission(request, response);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.setStatus(HttpStatus.NOT_FOUND.value());
			if(thumbnail )
				ResourceUtils.noThumbnails(request, response);
			else
				ResourceUtils.notAavaliable(request, response);
		}
	}

	@RequestMapping(value = "/files/{attachmentId:[\\p{Digit}]+}/{filename:.+}", method = RequestMethod.GET)
	@ResponseBody
	public void downloadFile(
			@PathVariable("attachmentId") Long attachmentId,
			@PathVariable("filename") String filename,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Attachment attachment = attachmentService.getAttachment(attachmentId);
			if (attachment != null) {
				if (!isAllowed(attachment))
					throw new UnAuthorizedException(); 
				InputStream input;
				String contentType;
				int contentLength; 
				input = attachmentService.getAttachmentInputStream(attachment);
				if (viewCountService != null && configService.getApplicationBooleanProperty( CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, false)) {
					log.debug("add view count attachment '{}'", attachment.getAttachmentId());
					viewCountService.addViewCount(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
				} 
				contentType = attachment.getContentType();
				contentLength = attachment.getSize();
				response.setContentType(contentType);
				response.setContentLength(contentLength);
				response.setHeader("contentDisposition", "attachment;filename=" + ServletUtils.getEncodedFileName(attachment.getName()));
				IOUtils.copy(input, response.getOutputStream());
				response.flushBuffer();
			}
		} catch (UnAuthorizedException uae ) {
			response.setStatus( HttpStatus.UNAUTHORIZED.value() );
			ResourceUtils.notAccessWithPermission(request, response);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.setStatus(HttpStatus.NOT_FOUND.value());
			ResourceUtils.notAavaliable(request, response);
		}
	}	
	
	@RequestMapping(value = "/files/{linkId}/{filename:.+}", method = RequestMethod.GET)
	@ResponseBody
	public void downloadFileByLink(
			@PathVariable("linkId") String linkId,
			@PathVariable("filename") String filename,
			@RequestParam(value = "thumbnail", defaultValue = "false", required = false) boolean thumbnail,
			@RequestParam(value = "width", defaultValue = "150", required = false) Integer width,
			@RequestParam(value = "height", defaultValue = "150", required = false) Integer height,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		try { 
			Long attachmentId = 0L;
			if( NumberUtils.isDigits(linkId) ) {
				attachmentId = NumberUtils.toLong(linkId); 
			}else {
				SharedLink link = sharedLinkService.getSharedLink(linkId);
				attachmentId = link.getObjectId();
			}
			Attachment attachment = attachmentService.getAttachment(attachmentId);
			if (attachment != null) {
				if (!thumbnail && !isAllowed(attachment))
					throw new UnAuthorizedException(); 
				InputStream input;
				String contentType;
				int contentLength;
				if(thumbnail) {
					boolean noThumbnail = false;
					if (attachmentService.hasThumbnail(attachment)) {
						ThumbnailImage thumbnailImage = new ThumbnailImage();
						thumbnailImage.setWidth(width);
						thumbnailImage.setHeight(height);
						try {
							input = attachmentService.getAttachmentThumbnailInputStream(attachment, thumbnailImage);
							if(input == null )
								throw new NotFoundException();
							
							response.setContentType(thumbnailImage.getContentType());
							response.setContentLength((int) thumbnailImage.getSize());
							IOUtils.copy(input, response.getOutputStream());
							response.flushBuffer();
							
						} catch (Exception e) {
							log.warn(e.getMessage(), e);
							noThumbnail = true;
						}
					}
					if (noThumbnail) {
						ResourceUtils.noThumbnails(request, response);
					}
				}else {
					input = attachmentService.getAttachmentInputStream(attachment); 
					if( input == null )
						throw new NotFoundException();
					
					if (viewCountService != null && configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, false)) {
						log.debug("add view count attachment '{}'", attachment.getAttachmentId());
						viewCountService.addViewCount(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
					} 
					contentType = attachment.getContentType();
					contentLength = attachment.getSize();
					response.setContentType(contentType);
					response.setContentLength(contentLength);
					response.setHeader("contentDisposition", "attachment;filename=" + ServletUtils.getEncodedFileName(attachment.getName()));
					IOUtils.copy(input, response.getOutputStream());
					response.flushBuffer();
				}
			}
		} catch (UnAuthorizedException uae ) {
			response.setStatus( HttpStatus.UNAUTHORIZED.value() );
			ResourceUtils.notAccessWithPermission(request, response);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.setStatus(HttpStatus.NOT_FOUND.value());
			ResourceUtils.notAavaliable(request, response);
		}
	}	
	
	@RequestMapping(value = "/files/{linkId}", method = RequestMethod.GET)
	@ResponseBody
	public void downloadFileByLink(@PathVariable("linkId") String linkId,
			@RequestParam(value = "thumbnail", defaultValue = "false", required = false) boolean thumbnail,
			@RequestParam(value = "width", defaultValue = "150", required = false) Integer width,
			@RequestParam(value = "height", defaultValue = "150", required = false) Integer height,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		try { 
			SharedLink link = sharedLinkService.getSharedLink(linkId);
			Attachment attachment = attachmentService.getAttachment(link.getObjectId());
			
			// checking security ..
			if (attachment != null) {
				if (!thumbnail && !isAllowed(attachment))
					throw new UnAuthorizedException();
				InputStream input;
				String contentType;
				int contentLength;
				if (thumbnail) {
					boolean noThumbnail = false;
					if (attachmentService.hasThumbnail(attachment)) {
						ThumbnailImage thumbnailImage = new ThumbnailImage();
						thumbnailImage.setWidth(width);
						thumbnailImage.setHeight(height);
						try {
							input = attachmentService.getAttachmentThumbnailInputStream(attachment, thumbnailImage);
							response.setContentType(thumbnailImage.getContentType());
							response.setContentLength((int) thumbnailImage.getSize());
							IOUtils.copy(input, response.getOutputStream());
							response.flushBuffer();
						} catch ( Throwable e ) {
							log.warn(e.getMessage(), e);
							noThumbnail = true;
						}
					}
					if (noThumbnail) {
						ResourceUtils.noThumbnails(request, response);
					}
				} else {
					input = attachmentService.getAttachmentInputStream(attachment);
					if (viewCountService != null && configService.getApplicationBooleanProperty( CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, false)) {
						log.debug("add view count attachment '{}'", attachment.getAttachmentId());
						viewCountService.addViewCount(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
					}
					
					contentType = attachment.getContentType();
					contentLength = attachment.getSize();
					response.setContentType(contentType);
					response.setContentLength(contentLength);
					
					IOUtils.copy(input, response.getOutputStream());
					response.setHeader("contentDisposition", "attachment;filename=" + ServletUtils.getEncodedFileName(attachment.getName()));
					response.flushBuffer();
				}
			}
		} catch (UnAuthorizedException uae ) {
			response.setStatus( HttpStatus.UNAUTHORIZED.value() );
			ResourceUtils.notAccessWithPermission(request, response);
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
			response.setStatus(HttpStatus.NOT_FOUND.value());
			ResourceUtils.notAavaliable(request, response);
		}
	}
}
