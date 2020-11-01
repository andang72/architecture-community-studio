package architecture.community.web.spring.controller.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import architecture.community.attachment.AttachmentService;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageService;
import architecture.community.query.CustomQueryService;
import architecture.community.share.SharedLinkService;
import architecture.community.streams.StreamsService;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;

@Controller("community-resources-images-upload-data-controller")
@RequestMapping("/data/")
public class UploadDataController  extends AbstractResourcesDataController {

	private Logger log = LoggerFactory.getLogger(UploadDataController.class); 
	

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
	
	
	/**
	 * 이미지를 생성 / 업데이트 하고 동시에 타 모듈에서 이미지를 보여주기 위한 링크키를 생성하여 리턴한다. 
	 * (링크키는 없는 경우에만 생성된다.)
	 * 
	 * @param objectType
	 * @param objectId
	 * @param imageId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws UnAuthorizedException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_USER"})
    @RequestMapping(value = "/images/0/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ImageLink> uploadAndRetureLink(
    		@RequestParam(value = "objectType", defaultValue = "-1", required = false) Integer objectType,
    		@RequestParam(value = "objectId", defaultValue = "-1", required = false) Long objectId,
    		@RequestParam(value = "imageId", defaultValue = "0", required = false) Long imageId,
    		@RequestHeader MultiValueMap<String, String> headers,
    		MultipartHttpServletRequest request) throws NotFoundException, IOException, UnAuthorizedException { 
		
		User user = SecurityHelper.getUser();
		if( user.isAnonymous() )
		    throw new UnAuthorizedException(); 
		
		if( log.isDebugEnabled() ) {
			headers.forEach((key, value) -> {
		        log.debug(String.format( "Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
		    });
		}
		
		List<ImageLink> list = new ArrayList<ImageLink>(); 		
		Iterator<String> names = request.getFileNames();
		while (names.hasNext()) {
		    String fileName = names.next();
		    log.debug("multipart name : {}", fileName );
		    MultipartFile mpf = request.getFile(fileName);
		    Image image = upload( user , objectType, objectId, imageId, mpf); 		    
		    ImageLink link = imageService.getImageLink(image, true);
		    link.setFilename(image.getName());
		    list.add( link ) ; 
		}
		return list ; 
    } 
	
	private static final Lock lock = new ReentrantLock();
	
	/**
	 * 
	 * 
	 * @param user
	 * @param objectType
	 * @param objectId
	 * @param imageId
	 * @param mpf
	 * @return
	 * @throws IOException
	 * @throws NotFoundException
	 */
	private Image upload ( User user , Integer objectType, Long objectId, Long imageId , MultipartFile mpf ) throws IOException, NotFoundException {		
		InputStream is = mpf.getInputStream();
	    log.debug("upload objectType: {}, objectId:{}, image : {}, file:{}, size:{}, type:{} ", objectType, objectId, imageId,  mpf.getOriginalFilename(), mpf.getSize() , mpf.getContentType() ); 
		Image image;
	    if (imageId > 0) {
		    	image = imageService.getImage(imageId);	
		    	image.setUser(user);
		    	((DefaultImage) image).setName(mpf.getOriginalFilename());
		    	((DefaultImage) image).setInputStream(is);
		    	((DefaultImage) image).setSize((int) mpf.getSize());
	    } else {
		    	image = imageService.createImage(objectType, objectId, mpf.getOriginalFilename(), mpf.getContentType(), is, (int) mpf.getSize());
		    	image.setUser(user);
	    }	
	    
	    lock.lock();
	    try {
			imageService.saveImage(image);
		} finally{
			 lock.unlock();
		}	   
	    return image;
	}
	
	/**
	 * FilePond 
	 */
	
}
