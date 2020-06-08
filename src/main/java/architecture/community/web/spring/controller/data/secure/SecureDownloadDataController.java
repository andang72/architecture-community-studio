package architecture.community.web.spring.controller.data.secure;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.AttachmentService;
import architecture.community.exception.NotFoundException;
import architecture.community.image.ThumbnailImage;
import architecture.community.query.CustomQueryService;
import architecture.community.web.spring.controller.data.ResourceUtils;
import architecture.community.web.spring.controller.data.secure.mgmt.ResourcesAttachmentDataController;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;
import architecture.ee.util.StringUtils;


@Controller("community-secure-download-data-controller")
@RequestMapping("/secure/download")
public class SecureDownloadDataController {

	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;
	
	private Logger log = LoggerFactory.getLogger(ResourcesAttachmentDataController.class);
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/files/{fileId:[\\p{Digit}]+}/{filename:.+}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void getAttachmentFile(
			@PathVariable("fileId") Long fileId, 
			@PathVariable("filename") String filename,
		    @RequestParam(value = "thumbnail", defaultValue = "false", required = false) boolean thumbnail,
		    @RequestParam(value = "width", defaultValue = "150", required = false) Integer width,
		    @RequestParam(value = "height", defaultValue = "150", required = false) Integer height,
		    HttpServletRequest request,
		    HttpServletResponse response) throws IOException { 
		try {
			
		    if (fileId > 0 && !StringUtils.isNullOrEmpty(filename)) { 
		    	log.debug("name {} decoded {}.", filename, ServletUtils.getEncodedFileName(filename)); 
		    	Attachment attachment = 	attachmentService.getAttachment(fileId); 
		    	int objectType = attachment.getObjectType() ;
		    	long objectId = attachment.getObjectId(); 
		    	log.debug("checking equals plain : {} , decoded : {} ", 
		    			org.apache.commons.lang3.StringUtils.equals(filename, attachment.getName()) , 
		    			org.apache.commons.lang3.StringUtils.equals(ServletUtils.getEncodedFileName(filename), attachment.getName())); 
		    	if (org.apache.commons.lang3.StringUtils.equals(filename, attachment.getName())) {
		    		if ( thumbnail ) {		    	
		    				boolean noThumbnail = false;		    				
		    				if(attachmentService.hasThumbnail(attachment)) {
		    					ThumbnailImage thumbnailImage = new ThumbnailImage();			
			    		    	thumbnailImage.setWidth(width);
			    		    	thumbnailImage.setHeight(height);		    		    		
		    				    try {
									InputStream input = attachmentService.getAttachmentThumbnailInputStream( attachment, thumbnailImage );
									if(input != null) {
										response.setContentType(thumbnailImage.getContentType());
										response.setContentLength( (int) thumbnailImage.getSize() );
										IOUtils.copy(input, response.getOutputStream());
										response.flushBuffer(); 
									}else {
										noThumbnail = true;
									}
								} catch (Throwable e) {
									// mlog.warn(e.getMessage(), e);
									noThumbnail = true;
								}
			    			}
		    			if(noThumbnail) {
		    				ResourceUtils.noThumbnails(request, response);
		    				//response.setStatus(301);
			    			//String url = configService.getApplicationProperty("components.download.attachments.no-attachment-url", "/images/no-image.jpg");
			    			//response.addHeader("Location", url);
		    			}		    				
		    		}else {
						InputStream input = attachmentService.getAttachmentInputStream(attachment);
						response.setContentType(attachment.getContentType());
						response.setContentLength(attachment.getSize());
						IOUtils.copy(input, response.getOutputStream());
						response.setHeader("contentDisposition", "attachment;filename=" + ServletUtils.getEncodedFileName(attachment.getName()));
						response.flushBuffer();
		    		}
				} else {
					throw new NotFoundException();
				}
		    } else {
				throw new NotFoundException();
			}
		} catch (NotFoundException e) {
			response.sendError(404);
		}
	}
}
