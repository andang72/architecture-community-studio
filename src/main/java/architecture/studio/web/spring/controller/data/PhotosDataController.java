package architecture.studio.web.spring.controller.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.DataSourceRequest.FilterDescriptor;
import architecture.community.web.spring.controller.data.AbstractResourcesDataController;
import architecture.community.web.spring.controller.data.ResourceUtils;
import architecture.community.web.model.ItemList;
import architecture.studio.web.spring.controller.data.secure.mgmt.ResourcesImagesDataController.UrlImageUploader;

@Controller("studio-media-photos-data-controller")
public class PhotosDataController extends AbstractResourcesDataController {

    private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;

    @Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

    @Autowired( required = false) 
	@Qualifier("tagService")
	private TagService tagService;
    

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
		log.debug("user from security : {}, principal : {} , authentication : {}", user, principal != null ? principal.getName() : "anonymous", authentication);

		boolean includeImageLink = org.apache.commons.lang3.StringUtils.contains(fields, "link");
		boolean includeTags = org.apache.commons.lang3.StringUtils.contains(fields, "tags");

		log.debug("fields link : {} , tags : {}", includeImageLink, includeTags);
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_IMAGE_BY_REQUEST");

		FilterDescriptor userIdFilter = new FilterDescriptor();
		userIdFilter.setField("USER_ID");
		userIdFilter.setOperator("eq");
		userIdFilter.setValue(user.getUserId());
		dataSourceRequest.getFilter().getFilters().add(userIdFilter);

		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_IMAGE_IDS_BY_REQUEST");

		List<Long> imageIDs = customQueryService.list(dataSourceRequest, Long.class);
		List<Image> images = getImages(imageIDs, includeImageLink, includeTags);
		return new ItemList(images, totalCount);
	}

	protected List<Image> getImages(List<Long> imageIDs, boolean includeImageLink, boolean includeTags) {
		List<Image> images = new ArrayList<Image>(imageIDs.size());
		for (Long id : imageIDs) {
			try {
				Image image = imageService.getImage(id);
				if (includeImageLink) {
					setImageLink(image, false);
				}
				if (includeTags && tagService != null) {
					String tags = tagService.getTagsAsString(Models.IMAGE.getObjectType(), image.getImageId());
					((DefaultImage) image).setTags(tags);
				}
				images.add(image);

			} catch (NotFoundException e) {
			}
		}
		return images;
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
	@Secured({ "ROLE_USER" })
	@RequestMapping(value = "/data/users/me/images/upload_by_url", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Image uploadImageByUrl(@RequestBody UrlImageUploader upload, NativeWebRequest request)
			throws NotFoundException, Exception {

		User user = SecurityHelper.getUser();
		if( log.isDebugEnabled() ){
			log.debug("downloading {}", upload.getFileName());
		} 
		File file = readFileFromUrl(upload.getImageUrl());
		String contentType = ResourceUtils.getContentType(file);
		Image imageToUse;
		if (upload.getImageId() > 0) {
			FileInputStream inputStream = new FileInputStream(file);
			imageToUse = imageService.getImage(upload.getImageId());
			((DefaultImage) imageToUse).setContentType(contentType);
			((DefaultImage) imageToUse).setInputStream(inputStream);
			((DefaultImage) imageToUse).setName(upload.getFileName());
			((DefaultImage) imageToUse).setDescription(upload.getDescription());
			imageToUse.setSize(IOUtils.toByteArray(inputStream).length);
		} else {
			imageToUse = imageService.createImage(upload.getObjectType(), upload.getObjectId(), upload.getFileName(), contentType, file);
		}
		imageToUse.setUser(user);
		imageToUse.getProperties().put("url", upload.getImageUrl().toString());
		if (upload.isWallpaper()) {
			imageToUse.getProperties().put("wallpaper", "true");
		}
		imageToUse = imageService.saveImage(imageToUse);
		if (upload.isShare()) {
			imageService.getImageLink(imageToUse, true);
		}
		return imageToUse;
	}

}
