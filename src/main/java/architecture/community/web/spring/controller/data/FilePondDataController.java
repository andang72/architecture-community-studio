package architecture.community.web.spring.controller.data;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.share.SharedLink;
import architecture.community.share.SharedLinkService;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.ee.util.StringUtils;

@Controller("community-resources-filepond-data-controller")
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
	 * 
	 * POST data/files/filepond
	 * 
	 * @param objectType
	 * @param objectId
	 * @param attachmentId
	 * @param shared
	 * @param request
	 * @param headers
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws UnAuthorizedException
	 */
	@RequestMapping(value = "/data/files/filepond", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadFiles (    	
    	@RequestParam(value = "objectType", defaultValue = "0", required = false) Integer objectType,
    	@RequestParam(value = "objectId", defaultValue = "0", required = false) Long objectId,
    	@RequestParam(value = "attachmentId", defaultValue = "0", required = false) Long attachmentId,
    	@RequestParam(value = "shared", defaultValue = "false", required = false) Boolean shared,
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
		    if( shared ) {
		    	SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId(), shared);
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
