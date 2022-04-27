package architecture.studio.web.spring.controller.data;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.album.Album;
import architecture.community.album.AlbumContents;
import architecture.community.album.AlbumImage;
import architecture.community.album.AlbumService;
import architecture.community.album.DefaultAlbum;
import architecture.community.attachment.AttachmentService;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.security.spring.userdetails.CommuintyUserDetails;
import architecture.community.security.spring.userdetails.SystemUser;
import architecture.community.share.SharedLinkService;
import architecture.community.streams.StreamsService;
import architecture.community.tag.TagService;
import architecture.community.user.CommunityUser;
import architecture.community.user.User;
import architecture.community.user.UserAlreadyExistsException;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.DataSourceRequest.FilterDescriptor;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.AbstractResourcesDataController;

@Controller("studio-me-secure-data-controller")
public class MeDataController extends AbstractResourcesDataController {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	@Qualifier("userManager")
	private UserManager userManager;


	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;	
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;
	
	@Autowired(required = false) 
	@Qualifier("streamsService")
	private StreamsService streamsService;
	
	@Autowired(required = false) 
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	@Autowired
	@Qualifier("albumService") 
	private AlbumService albumService;
		
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired( required = false) 
	@Qualifier("tagService")
	private TagService tagService;

	public MeDataController() {
	}

	/**
	 * ALBUM API 
	******************************************/
 
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_USER"})
	@RequestMapping(value = {"/data/users/me/albums", "/data/users/me/albums/list.json"}, method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getAlbums(
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		@RequestBody DataSourceRequest dataSourceRequest, 
		NativeWebRequest request) {   
		
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

	/**
	 * IMAGE API 
	******************************************/
	@Secured({ "ROLE_USER" })
	@RequestMapping(value = "/data/users/me/images/list.json", method = { RequestMethod.POST, RequestMethod.GET })
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
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_IMAGE_BY_REQUEST");

		FilterDescriptor userIdFilter = new FilterDescriptor();
		userIdFilter.setField("USER_ID");
		userIdFilter.setOperator("eq");
		userIdFilter.setValue(user.getUserId());
		dataSourceRequest.getFilter().getFilters().add( userIdFilter );

		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_IMAGE_IDS_BY_REQUEST");
		
		List<Long> imageIDs = customQueryService.list(dataSourceRequest, Long.class);
		List<Image> images = getImages( imageIDs, includeImageLink, includeTags);
		return new ItemList(images, totalCount );
	}

	protected List<Image> getImages (List<Long> imageIDs, boolean includeImageLink, boolean includeTags ){
		List<Image> images = new ArrayList<Image>(imageIDs.size());
		for( Long id : imageIDs ) {
			try {
				Image image = imageService.getImage(id);
				if( includeImageLink ) {
					setImageLink(image, false);
				}
				if( includeTags && tagService!= null ) {
					String tags = tagService.getTagsAsString(Models.IMAGE.getObjectType(), image.getImageId());
					((DefaultImage)image).setTags( tags );
				}
				images.add(image);
				
			} catch (NotFoundException e) {
			}
		}
		return images;
	}
	

	/**
	 * PROFILE API 
	******************************************/	
	@Secured({ "ROLE_USER" })
	@RequestMapping(value = { "/data/users/me", "/data/users/me/save-or-update.json" }, method = { RequestMethod.POST })
	@ResponseBody
	public Result saveOrUpdateUser(@RequestBody CommunityUser user, NativeWebRequest request)
			throws UnAuthorizedException, UserNotFoundException, UserAlreadyExistsException {
		log.debug("Save or update user {} ", user.toString());
		User userToUse = user;
		if (userToUse.getUserId() > 0 && userToUse.getUserId() == SecurityHelper.getUser().getUserId()) {
			userManager.updateUser(userToUse);
		} else {
			throw new UnAuthorizedException();
		}
		return Result.newResult("item", userToUse);
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(value = { "/data/users/me.json" }, method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public UserDetails getUserDetails(Authentication authentication, NativeWebRequest request) {
		// Authentication authentication = SecurityHelper.getAuthentication();
		UserDetails userDetails = null;
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof CommuintyUserDetails) {
				CommuintyUserDetails details = (CommuintyUserDetails) principal;
				userDetails = new UserDetails(details.getUser(), getRoles(details.getAuthorities()));
			} else if (principal instanceof SystemUser) {
				SystemUser details = (SystemUser) principal;
				userDetails = new UserDetails(details, getRoles(details.getAuthorities()));
			}
		}

		if (userDetails == null) {
			userDetails = new UserDetails(SecurityHelper.ANONYMOUS, Collections.EMPTY_LIST);
		}
		return userDetails;
	}

	protected List<String> getRoles(Collection<GrantedAuthority> authorities) {
		List<String> list = new ArrayList<String>();
		for (GrantedAuthority auth : authorities) {
			list.add(auth.getAuthority());
		}
		return list;
	}

	public static class UserDetails {
		private User user;
		private List<String> roles;

		public UserDetails() {
		}

		public UserDetails(User user, List<String> roles) {
			this.user = user;
			this.roles = roles;
		}

		/**
		 * @return user
		 */
		public User getUser() {
			return user;
		}

		/**
		 * @param user 설정할 user
		 */
		public void setUser(User user) {
			this.user = user;
		}

		/**
		 * @return roles
		 */
		public List<String> getRoles() {
			return roles;
		}

		/**
		 * @param roles 설정할 roles
		 */
		public void setRoles(List<String> roles) {
			this.roles = roles;
		}
	}
}
