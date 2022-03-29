package architecture.studio.web.spring.controller.secure;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.album.Album;
import architecture.community.album.AlbumContents;
import architecture.community.album.AlbumNotFoundException;
import architecture.community.album.AlbumService;
import architecture.community.attachment.Attachment;
import architecture.community.attachment.AttachmentService;
import architecture.community.attachment.DefaultAttachment;
import architecture.community.exception.NotFoundException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.share.SharedLink;
import architecture.community.share.SharedLinkService;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;

/**
 * Album API for USER ROLE
 */
@Controller("studio-album-secure-data-controller")
public class AlbumDataController {
    
    private Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
	@Qualifier("albumService") 
	private AlbumService albumService;
	
    @Autowired
	@Qualifier("imageService") 
	private ImageService imageService;

    @Autowired(required = false) 
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
    
	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;	

	@Secured({ "ROLE_USER" })
	@RequestMapping(value = {"/data/albums/{albumId:[\\p{Digit}]+}"}, method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Album getAlbum(@PathVariable Long albumId, NativeWebRequest request) throws AlbumNotFoundException {  
		return albumService.getAlbum(albumId);
	} 

    @Secured({ "ROLE_USER" })
	@RequestMapping(value = {"/data/albums/{albumId:[\\p{Digit}]+}/contents", "/data/albums/{albumId:[\\p{Digit}]+}/contents/list.json"}, 
	method = { RequestMethod.POST, RequestMethod.GET },  produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ItemList getAlbumContents(
			@PathVariable Long albumId, 
			@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
			@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) throws NotFoundException {  
		
		boolean includeLink = org.apache.commons.lang3.StringUtils.contains(fields, "link");   
		Album album = albumService.getAlbum(albumId); 
		
		List<AlbumContents> items = albumService.getAlbumContents(album);
		for( AlbumContents content : items ) {
			try { 
				if( content.isImage()) {
					Image image = imageService.getImage(content.getContentId());
					setOrder( image, content.getOrder() );
					if( includeLink ) {
						setLink(image);
					} 
					content.setImage(image);
				}else if (content.isAttachment()) {
					Attachment attachment = attachmentService.getAttachment(content.getContentId());
					setOrder( attachment, content.getOrder() );
					if( includeLink ) {
						setLink(attachment);
					} 
					content.setAttachment(attachment);
				} 
			} catch (NotFoundException e) {
			}
		}
		
		return new ItemList(items, items.size() ); 
	}

    private void setOrder(Image image, Integer order ) {
		((DefaultImage)image).setOrder( order  );
	}
	
	private void setOrder(Attachment attachment, Integer order  ) {
		((DefaultAttachment)attachment).setOrder( order  );
	}
	
	private void setLink(Image image ) {
		try {
			ImageLink link = imageService.getImageLink(image);
			((DefaultImage)image).setImageLink( link );
		} catch (Exception ignore) { }
	}
	
	private void setLink(Attachment attachment ) {
		try {
			SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
			((DefaultAttachment) attachment ).setSharedLink(link);
		} catch (Exception ignore) {}	
	}
}
