package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
import architecture.community.model.Models;
import architecture.community.model.Property;
import architecture.community.query.CustomQueryService;
import architecture.community.share.SharedLink;
import architecture.community.share.SharedLinkService;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.AbstractResourcesDataController;
import architecture.community.web.spring.controller.data.Utils;
import architecture.ee.util.StringUtils; 

@Controller("community-mgmt-resources-attachment-secure-data-controller")
@RequestMapping("/data/secure/mgmt/attachments")
public class ResourcesAttachmentDataController extends AbstractResourcesDataController {

	

	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;
	
	@Autowired(required=false)
	@Qualifier("tagService")
	private TagService tagService; 	
	
	private Logger log = LoggerFactory.getLogger(ResourcesAttachmentDataController.class);
	
	private boolean isSetSharedLinkService() {
		return sharedLinkService == null ? false : true;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
    @RequestMapping(value = "/{attachmentId:[\\p{Digit}]+}/delete.json", method = RequestMethod.POST)
    @ResponseBody
    public Result remove (
    		@PathVariable Long attachmentId,	
    		NativeWebRequest request ) throws NotFoundException, IOException, UnAuthorizedException {
		
		User user = SecurityHelper.getUser();
		Attachment attachment = attachmentService.getAttachment(attachmentId);
		deleteAttachment(attachment);
		return Result.newResult();
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
    @RequestMapping(value = "/upload.json", method = RequestMethod.POST)
    @ResponseBody
    public List<Attachment> upload (
    		@RequestParam(value = "objectType", defaultValue = "-1", required = false) Integer objectType,
    		@RequestParam(value = "objectId", defaultValue = "-1", required = false) Long objectId,
    		@RequestParam(value = "attachmentId", defaultValue = "-1", required = false) Long attachmentId,
    	    MultipartHttpServletRequest request ) throws NotFoundException, IOException, UnAuthorizedException {
		
		User user = SecurityHelper.getUser();
		List<Attachment> list = new ArrayList<Attachment>();
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
		    list.add(attachment);
		}			
		return list;
	}

	
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/save-or-update.json", method = { RequestMethod.POST })
	@ResponseBody
	public Attachment saveOrUpdate(@RequestBody DefaultAttachment attachment, NativeWebRequest request) throws NotFoundException { 
		
		DefaultAttachment attachmentToUse = 	(DefaultAttachment) attachmentService.getAttachment(attachment.getAttachmentId());
		if( !StringUtils.isNullOrEmpty(attachment.getName()) )
		{
			attachmentToUse.setName(attachment.getName());
		}
		if( attachmentToUse.getObjectType() != attachment.getObjectType())
		{
			attachmentToUse.setObjectType(attachment.getObjectType());
		}
		if( attachmentToUse.getObjectId() != attachment.getObjectId())
		{
			attachmentToUse.setObjectId(attachment.getObjectId());
		}
		
		attachmentService.saveAttachment(attachmentToUse);
		
		return attachmentToUse;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/{attachmentId:[\\p{Digit}]+}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Attachment getAttachment (
		@PathVariable Long attachmentId, 
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		NativeWebRequest request) throws NotFoundException {
		
		boolean includeLink = org.apache.commons.lang3.StringUtils.contains(fields, "link");  
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");  
		
		Attachment attachment = 	attachmentService.getAttachment(attachmentId);
		if( includeLink && isSetSharedLinkService()) {
			try {
				SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
				((DefaultAttachment) attachment ).setSharedLink(link);
			} catch (Exception ignore) {
				
			}
		}
		
		if( includeTags && tagService!= null ) {
			String tags = tagService.getTagsAsString(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
			((DefaultAttachment)attachment).setTags( tags );
		}
		
		return attachment;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getAttachments(
		@RequestBody DataSourceRequest dataSourceRequest, 
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		NativeWebRequest request) { 
		
		
		boolean includeLink = org.apache.commons.lang3.StringUtils.contains(fields, "link");
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");  
		
		if( !dataSourceRequest.getData().containsKey("objectType")) {
			dataSourceRequest.getData().put("objectType", -1);
		}	
		if( !dataSourceRequest.getData().containsKey("objectId") ) {
			dataSourceRequest.getData().put("objectId", -1);
		} 
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_ATTACHMENT_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_ATTACHMENT_IDS_BY_REQUEST");
		
		
		List<Long> items = customQueryService.list(dataSourceRequest, Long.class);
		List<Attachment> attachments = new ArrayList<Attachment>();		
		for( Long id : items ) {
			try {
				Attachment attachment = attachmentService.getAttachment(id);
				
				if( includeLink && sharedLinkService != null) {
					try {
						SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
						((DefaultAttachment) attachment ).setSharedLink(link);
					} catch (Exception ignore) {}	
				}
				
				if( includeTags && tagService!= null ) {
					String tags = tagService.getTagsAsString(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
					((DefaultAttachment)attachment).setTags( tags );
				}
				
				attachments.add(attachment);
				
			} catch (NotFoundException e) {
			}
		}
		return new ItemList(attachments, totalCount );
	}	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/{attachmentId:[\\p{Digit}]+}/refresh.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result refreshAttachment (
		@PathVariable Long attachmentId,
		NativeWebRequest request) throws NotFoundException {
		
		Attachment attachment = attachmentService.getAttachment(attachmentId);
		attachmentService.refresh(attachment);
		
		return Result.newResult();
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/{attachmentId:[\\p{Digit}]+}/link.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public SharedLink getAttachmentLinkAndCreateIfNotExist (
		@PathVariable Long attachmentId,
		@RequestParam(value = "create", defaultValue = "false", required = false) Boolean createIfNotExist,
		NativeWebRequest request) throws NotFoundException {
		
		Attachment attachment = attachmentService.getAttachment(attachmentId);
		
		SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId(), createIfNotExist);
 
		return link; 
	
	}	
	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/{attachmentId:[\\p{Digit}]+}/delete-link.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result removeLink (
		@PathVariable Long attachmentId,
		@RequestBody DataSourceRequest dataSourceRequest, 
		NativeWebRequest request) throws NotFoundException { 
		Attachment attachment = 	attachmentService.getAttachment(attachmentId);
		SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
		sharedLinkService.removeSharedLink(link.getLinkId());
		attachmentService.refresh(attachment);
		
		return Result.newResult();
	}	
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/{attachmentId:[\\p{Digit}]+}/properties/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> getAttachmentProperties (
		@PathVariable Long attachmentId, 
		NativeWebRequest request) throws NotFoundException {
		Attachment attachment = 	attachmentService.getAttachment(attachmentId);
		Map<String, String> properties = attachment.getProperties(); 
		return Utils.toList(properties);
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/{attachmentId:[\\p{Digit}]+}/properties/update.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> updateAttachmentProperties (
		@PathVariable Long attachmentId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		Attachment attachment = 	attachmentService.getAttachment(attachmentId);
		Map<String, String> properties = attachment.getProperties();   
		// update or create
		for (Property property : newProperties) {
		    properties.put(property.getName(), property.getValue().toString());
		} 
		attachmentService.saveAttachment(attachment) ; 
		return Utils.toList(attachment.getProperties());
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/{attachmentId:[\\p{Digit}]+}/properties/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> deleteAttachmentProperties (
		@PathVariable Long attachmentId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		Attachment attachment = 	attachmentService.getAttachment(attachmentId);
		Map<String, String> properties = attachment.getProperties();  
		for (Property property : newProperties) {
		    properties.remove(property.getName());
		}
		attachmentService.saveAttachment(attachment);
		return Utils.toList(attachment.getProperties());
	} 
	
}