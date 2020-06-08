package architecture.community.web.spring.controller.data;

import java.io.File;
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
import org.springframework.security.core.Authentication;
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
import architecture.community.streams.Streams;
import architecture.community.streams.StreamsService;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.spring.controller.data.secure.mgmt.ResourcesImagesDataController.UrlImageUploader;
import architecture.ee.util.StringUtils;

@Controller("community-resources-filepond-data-controller")
public class StreamsResourcesDataController extends AbstractResourcesDataController{

	private Logger log = LoggerFactory.getLogger(StreamsResourcesDataController.class); 

	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;	
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;
	
	@Autowired(required = false) 
	@Qualifier("streamsService")
	private StreamsService streamsService;
	
	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired( required = false) 
	@Qualifier("tagService")
	private TagService tagService;
	
	public StreamsResourcesDataController() {
		
	}
	
	@RequestMapping(value = "/data/streams/me/images/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getImages(
		@RequestBody DataSourceRequest dataSourceRequest, 
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		Authentication authentication,
		NativeWebRequest request) { 
		
		User user = SecurityHelper.getUser();
		Principal principal = request.getUserPrincipal();
		
		log.debug("user from security : {}, principal : {} , authentication : {}", user , principal != null ? principal.getName() : "anonymous", authentication);
		
		boolean includeImageLink = org.apache.commons.lang3.StringUtils.contains(fields, "link");  
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");  
		
		log.debug("fields link : {} , tags : {}", includeImageLink, includeTags);
		Streams streams = Utils.getStreamsByNameCreateIfNotExist(streamsService , Utils.ME_STREAM_NAME); 
		dataSourceRequest.getData().put("objectType", Models.STREAMS.getObjectType());
		dataSourceRequest.getData().put("objectId", streams.getStreamId());
		
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_IMAGE_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_IMAGE_IDS_BY_REQUEST");
		List<Long> imageIDs = customQueryService.list(dataSourceRequest, Long.class);
		List<Image> images = getImages( imageIDs, includeImageLink, includeTags);
		return new ItemList(images, totalCount );
	}
	
	/**
     * URL 로 이미지를 업로드 한다.
     * 
     * @param uploader
     * @param request
     * @return
     * @throws NotFoundException
     * @throws IOException
     */
	
    @RequestMapping(value = "/data/streams/me/images/0/upload_by_url.json", method = RequestMethod.POST)
    @ResponseBody
    public Image uploadImageByUrl(@RequestBody UrlImageUploader upload, NativeWebRequest request)
	    throws NotFoundException, Exception {
		User user = SecurityHelper.getUser();
		log.debug("downloading {}", upload.getFileName() );
		
		Streams streams = Utils.getStreamsByNameCreateIfNotExist(streamsService , Utils.ME_STREAM_NAME); 
		
		File file = readFileFromUrl(upload.getImageUrl());
		String contentType = detectContentType(file);
		Image imageToUse = imageService.createImage( Models.STREAMS.getObjectType(), streams.getStreamId(), upload.getFileName(), contentType, file );
		imageToUse.setUser(user);
		imageToUse.getProperties().put("url", upload.getImageUrl().getPath());
		Image uploadedImage = imageService.saveImage(imageToUse); 
		if( upload.isShare() ) {
		    ImageLink link = imageService.getImageLink(uploadedImage, true);
		    link.setFilename(uploadedImage.getName());
		    ((DefaultImage)uploadedImage).setImageLink(link);
		}
		return uploadedImage ; 
    }
	
	
	@RequestMapping(value = "/data/streams/me/files/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<ItemList> getAttachments(
		@RequestBody DataSourceRequest dataSourceRequest, 
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		NativeWebRequest request) throws UnAuthorizedException { 
		
		
		User user = SecurityHelper.getUser();
		Principal principal = request.getUserPrincipal();
		log.debug("user from security : {}, principal : {} ", user , principal != null ? principal.getName() : "anonymous");
		
		boolean includeLink = org.apache.commons.lang3.StringUtils.contains(fields, "link");
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");  
		
		Streams streams = Utils.getStreamsByNameCreateIfNotExist(streamsService , Utils.ME_STREAM_NAME); 
		dataSourceRequest.getData().put("objectType", Models.STREAMS.getObjectType());
		dataSourceRequest.getData().put("objectId", streams.getStreamId());
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_ATTACHMENT_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_ATTACHMENT_IDS_BY_REQUEST"); 
		
		List<Long> attachmentIDs = customQueryService.list(dataSourceRequest, Long.class);
		List<Attachment> attachments = getAttachments(attachmentIDs, includeLink, includeTags );
		return ResponseEntity.ok( new ItemList(attachments, totalCount) );
	}	
	
	
    @RequestMapping(value = "/data/streams/me/files/0/upload.json", method = RequestMethod.POST)
    @ResponseBody
    public List<Attachment> uploadFiles (
    		@RequestParam(value = "attachmentId", defaultValue = "-1", required = false) Long attachmentId,
    		@RequestParam(value = "shared", defaultValue = "true", required = false) Boolean shared,
    	    MultipartHttpServletRequest request ) throws NotFoundException, IOException, UnAuthorizedException {
		
		User user = SecurityHelper.getUser();
		Principal principal = request.getUserPrincipal();
		log.debug("user from security : {}, principal : {} ", user , principal != null ? principal.getName() : "anonymous");
		
		if(user.isAnonymous())
			throw new UnAuthorizedException("No Authorized. Please signin first."); 
		
		Streams streams = Utils.getStreamsByNameCreateIfNotExist(streamsService , Utils.ME_STREAM_NAME); 
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
		    	attachment = attachmentService.createAttachment(Models.STREAMS.getObjectType(), streams.getStreamId(), mpf.getOriginalFilename(), mpf.getContentType(), is, (int) mpf.getSize());
		    }
		    attachment.setUser(user);		
		    attachmentService.saveAttachment(attachment); 
		    if( shared ) {
		    	SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId(), shared);
		    	((DefaultAttachment) attachment ).setSharedLink(link);
		    } 
		    list.add(attachment);
		}
		
		return list;
	}
    
	/**
	 * 
	 * 
	 * https://pqina.nl/filepond/docs/patterns/api/server/
	 * 
	 * @param request
	 * @param headers
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws UnAuthorizedException
	 */
	@RequestMapping(value = "/data/streams/me/photos/filepond", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> uploadImage(
    		MultipartHttpServletRequest request, 
    		@RequestHeader Map<String, String> headers) throws NotFoundException, IOException, UnAuthorizedException {  
		
		headers.forEach((key, value) -> {
	        log.info(String.format("Header '%s' = %s", key, value));
	    });
		
		log.debug( "request parameters : {}" , request.getParameterMap() ); 
		
		User user = SecurityHelper.getUser();
		Principal principal = request.getUserPrincipal();
		log.debug("user from security : {}, principal : {} ", user , principal != null ? principal.getName() : "anonymous");
		
		if(user.isAnonymous())
			throw new UnAuthorizedException("No Authorized. Please signin first.");
		
		boolean shared = false;
		Streams streams = Utils.getStreamsByNameCreateIfNotExist(streamsService , Utils.ME_STREAM_NAME); 
		StringBuilder sb = new StringBuilder();
		
		List<Image> images = new ArrayList<Image>(); 
		Iterator<String> names = request.getFileNames();		
		while (names.hasNext()) {
		    String fileName = names.next();
		    MultipartFile mpf = request.getFile(fileName);
		    InputStream is = mpf.getInputStream(); 
		    log.debug("uploading name:{}, size:{}, type:{} ", mpf.getOriginalFilename(), mpf.getSize() , mpf.getContentType() );  
		    Image image = imageService.createImage(Models.STREAMS.getObjectType(), streams.getStreamId(), mpf.getOriginalFilename(), mpf.getContentType(), is, (int) mpf.getSize());
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
	
	@RequestMapping(value = "/data/streams/me/photos/filepond", method = RequestMethod.DELETE)
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
}
