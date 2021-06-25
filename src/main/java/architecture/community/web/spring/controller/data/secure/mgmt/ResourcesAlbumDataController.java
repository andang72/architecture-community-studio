package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
import architecture.community.album.AlbumImage;
import architecture.community.album.AlbumNotFoundException;
import architecture.community.album.AlbumService;
import architecture.community.album.DefaultAlbum;
import architecture.community.attachment.Attachment;
import architecture.community.attachment.AttachmentService;
import architecture.community.attachment.DefaultAttachment;
import architecture.community.exception.NotFoundException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.share.SharedLink;
import architecture.community.share.SharedLinkService;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.util.StringUtils;

@Controller("community-mgmt-resources-album-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class ResourcesAlbumDataController {

private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;
	
	
	@Autowired
	@Qualifier("albumService") 
	private AlbumService albumService;
	
	/**
	 * ALBUM API 
	******************************************/
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/albums", "/albums/list.json"}, method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getAlbums(
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) {   
		
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_ALBUM_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_ALBUM_IDS_BY_REQUEST");
		
		List<Long> items = customQueryService.list(dataSourceRequest, Long.class); 
		List<Album> albums = new ArrayList<Album>(totalCount);
		for( Long albumId : items ) {
			try {
				Album album = albumService.getAlbum(albumId);
				if(  org.apache.commons.lang3.StringUtils.contains(fields, "contents") )
					setCoverImageFromContents(album);
				else
					setCoverImage(album);
				albums.add(album); 
			} catch (NotFoundException e) {
			}
		} 
		return new ItemList(albums, totalCount ); 
	}

	private void setCoverImageFromContents(Album album) {  
		List<AlbumContents> list = albumService.getAlbumContents(album);
		for( AlbumContents contents : list ) {
			if( contents.isImage() ) {
				try {
					Image coverImage = imageService.getImage(contents.getContentId());
					((DefaultAlbum)album).setCoverImage(coverImage);
					break;
				} catch (NotFoundException e) { 
				}
			}
		}
	}
	
	private void setCoverImage(Album album) { 
		
		List<AlbumImage> list = albumService.getAlbumImages(album);
		if( list.size() > 0 ) { 
			AlbumImage img = list.get(0);
			try {
				Image coverImage = imageService.getImage(img.getImageId());
				((DefaultAlbum)album).setCoverImage(coverImage);
			} catch (NotFoundException e) { 
			}
		}
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/albums/{albumId:[\\p{Digit}]+}"}, method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Album getAlbum(@PathVariable Long albumId, NativeWebRequest request) throws AlbumNotFoundException {  
		return albumService.getAlbum(albumId);
	} 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/albums/{albumId:[\\p{Digit}]+}/images", "/albums/{albumId:[\\p{Digit}]+}/images/list.json"}, 
	method = { RequestMethod.POST, RequestMethod.GET }, 
	produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ItemList getAlbumImages(
			@PathVariable Long albumId, 
			@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
			@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) throws NotFoundException {  
		
		boolean includeImageLink = org.apache.commons.lang3.StringUtils.contains(fields, "imageLink");   
		Album album = albumService.getAlbum(albumId); 
		
		List<AlbumImage> items = albumService.getAlbumImages(album);
		List<Image> images = new ArrayList<Image>(items.size());
		for( AlbumImage img : items ) {
			try { 
				Image image = imageService.getImage(img.getImageId());
				setOrder( image, img.getOrder() );
				if( includeImageLink ) {
					setLink(image);
				}
				images.add(image); 
			} catch (NotFoundException e) {
			}
		}
		return new ItemList(images, images.size() ); 
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/albums/{albumId:[\\p{Digit}]+}/contents", "/albums/{albumId:[\\p{Digit}]+}/contents/list.json"}, 
	method = { RequestMethod.POST, RequestMethod.GET }, 
	produces = MediaType.APPLICATION_JSON_VALUE )
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
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/albums/{albumId:[\\p{Digit}]+}/images", "/albums/{albumId:[\\p{Digit}]+}/images/save-or-update.json"}, 
		method = { RequestMethod.PUT },
		produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Result saveOrUpdateAlbumImages (@RequestBody List<DefaultImage> images, @PathVariable Long albumId,  NativeWebRequest request) throws NotFoundException {
		log.debug("save or update album ({}) images {}.", albumId, images.size());
		Album album = albumService.getAlbum(albumId);
		List<Image> imagesToUse = new ArrayList<Image>(images.size());
		for( Image img : images )
			imagesToUse.add(img);
		albumService.saveOrUpdate(album, imagesToUse);
		return Result.newResult();
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/albums/{albumId:[\\p{Digit}]+}/contents", "/albums/{albumId:[\\p{Digit}]+}/contents/save-or-update.json"}, 
		method = { RequestMethod.PUT },
		produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public Result saveOrUpdateAlbumContents (@RequestBody List<AlbumContents> contents, @PathVariable Long albumId,  NativeWebRequest request) throws NotFoundException {
		log.debug("save or update album ({}) images {}.", albumId, contents.size());
		Album album = albumService.getAlbum(albumId);
		
		for(AlbumContents item :  contents ) {
			item.setAlbumId(albumId);
		}

		albumService.saveOrUpdate(album, contents, true);
		return Result.newResult();
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/albums/{albumId:[\\p{Digit}]+}", "/albums/{albumId:[\\p{Digit}]+}/save-or-update.json"}, method = { RequestMethod.POST })
	@ResponseBody
	public Album saveOrUpdateAlbum(@RequestBody DefaultAlbum album, @PathVariable Long albumId,  NativeWebRequest request) throws NotFoundException {  
		DefaultAlbum albumToUse = album;
		if(albumToUse.getAlbumId() > 0  ) {
			albumToUse = 	(DefaultAlbum)albumService.getAlbum(album.getAlbumId());
			if( !StringUtils.isNullOrEmpty(album.getName()) )
			{
				albumToUse.setName(album.getName());
			} 
			if( !StringUtils.isNullOrEmpty(album.getDescription()) )
			{
				albumToUse.setDescription(album.getDescription());
			} 
		}else {
			albumToUse.setUser( SecurityHelper.getUser() );
		} 
		albumService.saveOrUpdate(albumToUse); 
		return albumToUse;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/albumId/{albumId:[\\p{Digit}]+}/delete.json"}, method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result deleteAlbum(
		@PathVariable Long albumId, 
		NativeWebRequest request) throws NotFoundException {
		Result result = Result.newResult();
		Album album = 	albumService.getAlbum(albumId);
		albumService.delete(album);
		return result;
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
