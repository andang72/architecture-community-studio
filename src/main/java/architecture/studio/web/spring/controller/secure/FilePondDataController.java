package architecture.studio.web.spring.controller.secure;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.AttachmentService;
import architecture.community.attachment.DefaultAttachment;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.share.SharedLink;
import architecture.community.share.SharedLinkService;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.community.util.SecurityHelper;
import architecture.ee.util.StringUtils;

import architecture.community.web.spring.controller.data.Utils;

@Controller("studio-resources-filepond-secure-data-controller")
public class FilePondDataController {

	private Logger log = LoggerFactory.getLogger(FilePondDataController.class); 

	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;	
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;
 
	@Autowired( required = false) 
	@Qualifier("tagService")
	private TagService tagService;
	

	/**
	 * /data/images/0/filepond
	 * 
	 * @param request
	 * @param headers
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws UnAuthorizedException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_USER"})
    @RequestMapping(value = "/data/images/0/filepond", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadImage( 
    		@RequestHeader Map<String, String> headers,
			MultipartHttpServletRequest request) throws NotFoundException, IOException, UnAuthorizedException {  
		
		headers.forEach((key, value) -> {
	        log.info(String.format("Header '%s' = %s", key, value));
	    });
		
		log.debug( "request parameters : {}" , request.getParameterMap() ); 
		
		User user = new UserTemplate(1L);
		Principal principal = request.getUserPrincipal();
		log.debug("user from security : {}, principal : {} ", user , principal != null ? principal.getName() : "anonymous");
		
		if(user.isAnonymous())
			throw new UnAuthorizedException("No Authorized. Please signin first.");
		
		boolean shared = false;
		StringBuilder sb = new StringBuilder();
		List<Image> images = new ArrayList<Image>(); 
		Iterator<String> names = request.getFileNames();		
		while (names.hasNext()) {
		    String fileName = names.next();
		    MultipartFile mpf = request.getFile(fileName);
		    InputStream is = mpf.getInputStream(); 
		    log.debug("uploading name:{}, size:{}, type:{} ", mpf.getOriginalFilename(), mpf.getSize() , mpf.getContentType() );  
		    Image image = imageService.createImage(0, 0L, mpf.getOriginalFilename(), mpf.getContentType(), is, (int) mpf.getSize());
		    image.setUser(user);		    
		    imageService.saveImage(image);  
		    if( shared ) {
				try {
					imageService.getImageLink(image, true);
					ImageLink link = imageService.getImageLink(image);
					((DefaultImage)image).setImageLink( link );
				} catch (Exception ignore) { 
				} 
		    }
		    sb.append(image.getImageId());
		    images.add(image);
		} 
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN); 
		return new ResponseEntity<String>(sb.toString(), responseHeaders, HttpStatus.OK);
    }
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_USER"})
    @RequestMapping(value = "/data/images/0/filepond", method = RequestMethod.DELETE)
	@ResponseBody
    public ResponseEntity<String> deleteImage ( @RequestHeader Map<String, String> headers, @RequestBody String body, NativeWebRequest request) throws NotFoundException, IOException, UnAuthorizedException {  
		headers.forEach((key, value) -> {
	        log.info(String.format("Header '%s' = %s", key, value));
	    });
		log.debug("request {}", body);
		Long imageId = NumberUtils.toLong(StringUtils.defaultString(body, "0"), -1L);
		
		User user = SecurityHelper.getUser();
		Principal principal = request.getUserPrincipal();
		log.debug("user from security : {}, principal : {} ", user , principal != null ? principal.getName() : "anonymous");
		
		if(user.isAnonymous())
			throw new UnAuthorizedException("No Authorized. Please signin first."); 
		if( imageId > 0 ) { 
			Image image = imageService.getImage(imageId); 
			if( Utils.isAllowed(image.getUser(), user)) {
				imageService.deleteImage(image);
			}else {
				throw new UnAuthorizedException("No Authorized."); 
			}
		}  
		return ResponseEntity.ok("deleted");
    }

	/**
	 * 
	 * POST /data/files/filepond
	 * 
	 * @param objectType
	 * @param objectId
	 * @param attachmentId
	 * @param share
	 * @param request
	 * @param headers
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws UnAuthorizedException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_USER"})
    @RequestMapping(value = "/data/files/0/filepond", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadFiles (    	
    	@RequestParam(value = "objectType", defaultValue = "0", required = false) Integer objectType,
    	@RequestParam(value = "objectId", defaultValue = "0", required = false) Long objectId,
    	@RequestParam(value = "attachmentId", defaultValue = "0", required = false) Long attachmentId,
    	@RequestParam(value = "share", defaultValue = "false", required = false) Boolean share,
    	MultipartHttpServletRequest request ) throws NotFoundException, IOException, UnAuthorizedException {  
		
		User user = SecurityHelper.getUser();
		
		if(user.isAnonymous())
			throw new UnAuthorizedException("No Authorized. Please signin first.");
		
		List<Attachment> list = new ArrayList<Attachment>();
		
		StringBuilder sb = new StringBuilder();
		Iterator<String> names = request.getFileNames(); 
		
		while (names.hasNext()) {
		    String fileName = names.next();
		    MultipartFile mpf = request.getFile(fileName);
		    
		    InputStream is = mpf.getInputStream();		    
		    log.debug("upload - file:{}, size:{}, type:{} ", mpf.getOriginalFilename(), mpf.getSize() , mpf.getContentType() );
		    Attachment attachment ;
		    if(attachmentId > 0)
		    {
			    attachment = attachmentService.getAttachment(attachmentId);
			    attachment.setContentType(mpf.getContentType());
			    attachment.setInputStream(is);
			    attachment.setSize((int) mpf.getSize());
			    attachment.setName(mpf.getOriginalFilename());
		    }else {
		    	attachment = attachmentService.createAttachment(objectType, objectId, mpf.getOriginalFilename(), mpf.getContentType(), is, (int) mpf.getSize());
		    }
		    attachment.setUser(user);		
		    attachmentService.saveAttachment(attachment);		    
		    if( share ) {
		    	SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId(), share);
		    	((DefaultAttachment) attachment ).setSharedLink(link);
		    	//sb.append(link.getLinkId());
		    }
		    sb.append(attachment.getAttachmentId());
		    list.add(attachment);
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN); 
		return new ResponseEntity<String>(sb.toString(), responseHeaders, HttpStatus.OK);
		
    }
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_USER"})
    @RequestMapping(value = "/data/files/filepond", method = RequestMethod.DELETE)
	@ResponseBody
    public ResponseEntity<String> deleteFiles ( 
    		@RequestBody String body, 
    		NativeWebRequest request) throws NotFoundException, IOException, UnAuthorizedException {  
  
		Long attachmentId = NumberUtils.toLong(StringUtils.defaultString(body, "0"), -1L);
		
		User user = SecurityHelper.getUser();
		Principal principal = request.getUserPrincipal();

		log.debug("user from security : {}, principal : {} ", user , principal != null ? principal.getName() : "anonymous");
		
		if(user.isAnonymous())
			throw new UnAuthorizedException("No Authorized. Please signin first."); 
		
			if( attachmentId > 0 ) { 			
			Attachment attachment = attachmentService.getAttachment(attachmentId);
			attachmentService.removeAttachment(attachment);
			try {
				SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
				sharedLinkService.removeSharedLink(link.getLinkId());
			} catch (Exception e) { 
			}
		} 		
		return ResponseEntity.ok("deleted");
    }
	
}
