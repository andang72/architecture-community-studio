package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.model.Property;
import architecture.community.query.CustomQueryService;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.Utils;
import architecture.ee.util.StringUtils;

@Controller("community-mgmt-resources-images-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class ResourcesImagesDataController {
 
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;

	@Autowired(required=false)
	@Qualifier("tagService")
	private TagService tagService; 
	
	/**
	 * IMAGES API 
	******************************************/
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getImages(
		@RequestBody DataSourceRequest dataSourceRequest, 
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		NativeWebRequest request) { 
		
		boolean includeImageLink = org.apache.commons.lang3.StringUtils.contains(fields, "imageLink");  
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");  
		
		log.debug("fields link : {} , tags : {}", includeImageLink, includeTags);
		
		if( !dataSourceRequest.getData().containsKey("objectType")) {
			dataSourceRequest.getData().put("objectType", -1);
		}	
		if( !dataSourceRequest.getData().containsKey("objectId") ) {
			dataSourceRequest.getData().put("objectId", -1);
		} 
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_IMAGE_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_IMAGE_IDS_BY_REQUEST");
		List<Long> items = customQueryService.list(dataSourceRequest, Long.class);
		List<Image> images = new ArrayList<Image>(totalCount);
		for( Long id : items ) {
			try {
				Image image = imageService.getImage(id);
				if( includeImageLink ) {
					try {
						ImageLink link = imageService.getImageLink(image);
						((DefaultImage)image).setImageLink( link );
					} catch (Exception ignore) {
						
					}
				}
				if( includeTags && tagService!= null ) {
					String tags = tagService.getTagsAsString(Models.IMAGE.getObjectType(), image.getImageId());
					((DefaultImage)image).setTags( tags );
				}
				images.add(image);
				
			} catch (NotFoundException e) {
			}
		}
		return new ItemList(images, totalCount );
	}	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/save-or-update.json", method = { RequestMethod.POST })
	@ResponseBody
	public Image saveOrUpdate(@RequestBody DefaultImage image, NativeWebRequest request) throws NotFoundException { 
		DefaultImage imageToUse = 	(DefaultImage)imageService.getImage(image.getImageId());
		if( !StringUtils.isNullOrEmpty(image.getName()) )
		{
			imageToUse.setName(image.getName());
		}
		if( imageToUse.getObjectType() != image.getObjectType())
		{
			imageToUse.setObjectType(image.getObjectType());
		}
		if( imageToUse.getObjectId() != image.getObjectId())
		{
			imageToUse.setObjectId(image.getObjectId());
		}
		imageService.saveOrUpdate(imageToUse); 
		return imageToUse;
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
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
    @RequestMapping(value = "/images/upload_by_url.json", method = RequestMethod.POST)
    @ResponseBody
    public Image uploadImageByUrl(@RequestBody UrlImageUploader upload, NativeWebRequest request)
	    throws NotFoundException, Exception {
		User user = SecurityHelper.getUser();
		log.debug("downloading {}", upload.getFileName() );
		
		File file = readFileFromUrl(upload.getImageUrl());
		String contentType = getContentType(file);
		Image imageToUse ;
		if( upload.getImageId() > 0 ) {
			FileInputStream inputStream = new FileInputStream(file);
			imageToUse = imageService.getImage(upload.getImageId());
			((DefaultImage)imageToUse).setContentType(contentType);
	    	((DefaultImage)imageToUse).setInputStream(inputStream); 
	    	((DefaultImage)imageToUse).setName(upload.getFileName());
	    	imageToUse.setSize(IOUtils.toByteArray(inputStream).length);
		}else {
			imageToUse = imageService.createImage( upload.getObjectType(), upload.getObjectId(), upload.getFileName(), contentType, file );
		}
		imageToUse.setUser(user);
		imageToUse.getProperties().put("url", upload.getImageUrl().getPath());
		return imageService.saveImage(imageToUse);
		
    }

    public static class UrlImageUploader {
    	
    	private long imageId = 0L;
		private int objectType = 0 ;
		private URL imageUrl;
		private long objectId = 0;
		private boolean share = false;

		@JsonIgnore
		private String contentType;
	
		public long getImageId() {
			return imageId;
		}

		public void setImageId(long imageId) {
			this.imageId = imageId;
		}

		public void setObjectId(long objectId) {
		    this.objectId = objectId;
		}
	
		public long getObjectId() {
		    return this.objectId;
		}

		public int getObjectType() {
		    return objectType;
		}
 
		public void setObjectType(int objectType) {
			this.objectType = objectType;
		}
	
		public boolean isShare() {
			return share;
		}

		public void setShare(boolean share) {
			this.share = share;
		}

		/**
		 * @return imageUrl
		 */
		public URL getImageUrl() {
		    return imageUrl;
		}
		
		public void setImageUrl(URL imageUrl) {
		    this.imageUrl = imageUrl;
		}

		public String getContentType() {
		    if (contentType == null) {
				Tika tika = new Tika();
				try {
				    contentType = tika.detect(imageUrl);
				} catch (IOException e) {
				    contentType = null;
				}
		    }
		    return contentType;
		}

		public String getFileName() {
		    return FilenameUtils.getName(imageUrl.getFile());
		}

		public File readFileFromUrl() throws IOException {
		    File temp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
		    temp.deleteOnExit();
		    FileUtils.copyURLToFile(imageUrl, temp);
		    return temp;
		}
		
    }
    
    public String getContentType(File file) {
    	String contentType = null;
	    if (contentType == null) {
			Tika tika = new Tika();
			try {
			    contentType = tika.detect(file);
			} catch (IOException e) {
			    contentType = null;
			}
	    }
	    return contentType;
	}
    
	public File readFileFromUrl(URL url) throws Exception {
		// This will get input data from the server
		InputStream inputStream = null;
		try {
			// This user agent is for if the server wants real humans to visit
			String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
			// This socket type will allow to set user_agent
			URLConnection con = url.openConnection();
			// Setting the user agent
			con.setRequestProperty("User-Agent", USER_AGENT);
			// Requesting input data from server
			inputStream = con.getInputStream();
			File temp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
			FileUtils.copyToFile( inputStream, temp );
			return temp;
		}finally {
			IOUtils.closeQuietly(inputStream);
		}
	} 
	
	/**
	 * 이미지를 생성 / 업데이트 한다. 
	 * @param objectType
	 * @param objectId
	 * @param imageId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws UnAuthorizedException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
    @RequestMapping(value = "/images/upload.json", method = RequestMethod.POST)
    @ResponseBody
    public List<Image> upload(
    		@RequestParam(value = "objectType", defaultValue = "-1", required = false) Integer objectType,
    		@RequestParam(value = "objectId", defaultValue = "-1", required = false) Long objectId,
    		@RequestParam(value = "imageId", defaultValue = "-1", required = false) Long imageId,
    		MultipartHttpServletRequest request) throws NotFoundException, IOException, UnAuthorizedException {

		User user = SecurityHelper.getUser();
		List<Image> list = new ArrayList<Image>();
		Iterator<String> names = request.getFileNames();		
		while (names.hasNext()) {
		    String fileName = names.next();
		    MultipartFile mpf = request.getFile(fileName);
		    InputStream is = mpf.getInputStream();
		    log.debug("upload file:{}, size:{}, type:{} ", mpf.getOriginalFilename(), mpf.getSize() , mpf.getContentType() ); 
		    Image image ;
		    if( imageId > 0) {
		    	image = imageService.getImage(imageId); 
		    	((DefaultImage)image).setContentType(mpf.getContentType());
		    	((DefaultImage)image).setInputStream(is);
		    	image.setSize((int) mpf.getSize());
		    	((DefaultImage)image).setName(mpf.getOriginalFilename());
		    }else {
		    	image = imageService.createImage(objectType, objectId, mpf.getOriginalFilename(), mpf.getContentType(), is, (int) mpf.getSize());
		    	
		    }
		    image.setUser(user);		    
		    imageService.saveImage(image);
		    list.add(image);
		}			
		return list;
    }


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
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
    @RequestMapping(value = "/images/upload_image_and_link.json", method = RequestMethod.POST)
    @ResponseBody
    public List<ImageLink> uploadAndRetureLink(
    		@RequestParam(value = "objectType", defaultValue = "-1", required = false) Integer objectType,
    		@RequestParam(value = "objectId", defaultValue = "-1", required = false) Long objectId,
    		@RequestParam(value = "imageId", defaultValue = "0", required = false) Long imageId,
    		MultipartHttpServletRequest request) throws NotFoundException, IOException, UnAuthorizedException { 
		User user = SecurityHelper.getUser();
		if( user.isAnonymous() )
		    throw new UnAuthorizedException(); 
		List<ImageLink> list = new ArrayList<ImageLink>(); 
		Iterator<String> names = request.getFileNames();		
		while (names.hasNext()) {
		    String fileName = names.next();
		    MultipartFile mpf = request.getFile(fileName);
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
		    imageService.saveImage(image);
		    ImageLink link = imageService.getImageLink(image, true);
		    link.setFilename(image.getName());
		    list.add( link ) ; 
		}			
		return list ; 
    } 

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/imagebrowser/{objectType:[\\p{Digit}]+}/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getImagesByObjectType(
		@RequestBody DataSourceRequest dataSourceRequest, 	
		@PathVariable Integer objectType,
		NativeWebRequest request) {
		dataSourceRequest.setData("objectType", objectType);
		
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_IMAGE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID");
		List<Long> items = customQueryService.list(dataSourceRequest, Long.class);
		List<Image> images = new ArrayList<Image>(items.size());
		for( Long id : items ) {
			try {
				images.add(imageService.getImage(id));
			} catch (NotFoundException e) {
			}
		}
		return new ItemList(images, images.size() );
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/imagebrowser/{objectType:[\\p{Digit}]+}/{objectId:[\\p{Digit}]+}/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getImagesByObjectTypeAndObjectId(
		@PathVariable Integer objectType,
		@PathVariable Long objectId,
		@RequestBody DataSourceRequest dataSourceRequest,
		NativeWebRequest request) {
		
		dataSourceRequest.setData("objectType", objectType);
		dataSourceRequest.setData("objectId", objectId);
		
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_IMAGE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID");
		List<Long> items = customQueryService.list(dataSourceRequest, Long.class);
		List<Image> images = new ArrayList<Image>(items.size());
		for( Long id : items ) {
			try {
				images.add(imageService.getImage(id));
			} catch (NotFoundException e) {
			}
		}
		return new ItemList(images, images.size() );
		
	}	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/delete.json", method = { RequestMethod.POST })
	@ResponseBody
	public Result deleteImages(@RequestBody List<DefaultImage> images, NativeWebRequest request) throws NotFoundException { 
		
		Result result = Result.newResult();
		for( Image image : images ) {
			imageService.deleteImage(image);
			result.setCount( result.getCount() + 1 );
		}
		
		return result;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result removeImageAndLink (
		@PathVariable Long imageId,
		@RequestBody DataSourceRequest dataSourceRequest, 
		NativeWebRequest request) throws NotFoundException { 
		Image image = 	imageService.getImage(imageId);
		imageService.deleteImage(image); 
		return Result.newResult();
	}	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/delete-link.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result removeLink (
		@PathVariable Long imageId,
		@RequestBody DataSourceRequest dataSourceRequest, 
		NativeWebRequest request) throws NotFoundException {
		
		Image image = 	imageService.getImage(imageId); 
		return Result.newResult();
	}	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Image getImage (
		@PathVariable Long imageId,
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		NativeWebRequest request) throws NotFoundException {
		
		boolean includeImageLink = org.apache.commons.lang3.StringUtils.contains(fields, "imageLink");  
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");  
		
		Image image = 	imageService.getImage(imageId);
		if( includeImageLink ) {
			try {
				ImageLink link = imageService.getImageLink(image);
				((DefaultImage)image).setImageLink( link );
			} catch (Exception ignore) {
				
			}
		}
		if( includeTags && tagService!= null ) {
			String tags = tagService.getTagsAsString(Models.IMAGE.getObjectType(), image.getImageId());
			((DefaultImage)image).setTags( tags );
		}
		
		
		return image;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/link.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ImageLink getImageLinkAndCreateIfNotExist (
		@PathVariable Long imageId,
		@RequestParam(value = "create", defaultValue = "false", required = false) Boolean createIfNotExist,
		NativeWebRequest request) throws NotFoundException {
		
		Image image = 	imageService.getImage(imageId);
		return imageService.getImageLink(image, createIfNotExist);
	}	
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/properties/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> getImageProperties (
		@PathVariable Long imageId, 
		NativeWebRequest request) throws NotFoundException {
		Image image = 	imageService.getImage(imageId);
		Map<String, String> properties = image.getProperties(); 
		return Utils.toList(properties);
	}

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/properties/update.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> updateImageProperties (
		@PathVariable Long imageId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		Image image = 	imageService.getImage(imageId);
		Map<String, String> properties = image.getProperties();   
		// update or create
		for (Property property : newProperties) {
		    properties.put(property.getName(), property.getValue().toString());
		} 
		imageService.saveOrUpdate(image); 
		return Utils.toList(image.getProperties());
	}

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/properties/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<Property> deleteImageProperties (
		@PathVariable Long imageId, 
		@RequestBody List<Property> newProperties,
		NativeWebRequest request) throws NotFoundException {
		Image image = 	imageService.getImage(imageId);
		Map<String, String> properties = image.getProperties();  
		for (Property property : newProperties) {
		    properties.remove(property.getName());
		}
		imageService.saveOrUpdate(image);
		return Utils.toList(image.getProperties());
	} 
}