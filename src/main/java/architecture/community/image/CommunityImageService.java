/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.attachment.AbstractAttachmentService;
import architecture.community.exception.NotFoundException;
import architecture.community.image.dao.ImageDao;
import architecture.community.image.dao.ImageLinkDao;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.util.CommunityConstants.Platform;
import architecture.ee.exception.RuntimeError;
import architecture.ee.service.Repository;
import architecture.ee.util.StringUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class CommunityImageService extends AbstractAttachmentService implements ImageService {

	private Lock lock = new ReentrantLock();

	@Inject
	@Qualifier("repository")
	private Repository repository;

	@Inject
	@Qualifier("imageDao")
	private ImageDao imageDao;

	@Inject
	@Qualifier("imageLinkDao")
	private ImageLinkDao imageLinkDao;
	
	@Inject
	@Qualifier("logoImageIdsCache")
	private Cache logoImageIdsCache;

	@Inject
	@Qualifier("logoImageCache")
	private Cache logoImageCache;

	@Inject
	@Qualifier("imageCache")
	private Cache imageCache;

	
	@Inject
	@Qualifier("imageLinkIdCache")
    private Cache imageLinkIdCache;
	
	@Inject
	@Qualifier("imageLinkCache")
    private Cache imageLinkCache;
 
	@Inject
	@Qualifier("userManager")
	private UserManager userManager;
	
	@Autowired
	@Qualifier("customQueryJdbcDao")
	private CustomQueryJdbcDao customQueryJdbcDao;
	
	private ImageConfig imageConfig;;

	private File imageDir;

	public CommunityImageService() {
		imageConfig = new ImageConfig();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addLogoImage(LogoImage logoImage, File file) {
		if (logoImage.getImageId() < 1) {
			// clear cache
			List<Long> list = getLogoImageIdList(logoImage.getObjectType(), logoImage.getObjectId());
			for (Long logoImageId : list) {
				this.logoImageCache.remove(logoImageId);
			}
			this.logoImageIdsCache.remove(getLogoImageIdListCacheKey(logoImage.getObjectType(), logoImage.getObjectId()));
			imageDao.addLogoImage(logoImage, file);
		}
	}

	public LogoImage createLogoImage() {
		return new DefaultLogoImage();
	}

	public LogoImage createLogoImage(int objectType, long objectId, boolean primary) {
		DefaultLogoImage image = new DefaultLogoImage(objectType, objectId, primary);
		return image;
	}
	
	public LogoImage createLogoImage(int objectType, long objectId, boolean primary, String name, String contentType, File file) {
		DefaultLogoImage image = new DefaultLogoImage(objectType, objectId, primary);
		
		image.setContentType(contentType);
		
		image.setName(name);
		
		if(StringUtils.isNullOrEmpty(image.getName())){
			image.setName(file.getName());
		}
		
		image.setSize((int) FileUtils.sizeOf(file));
		
		try {
			image.setInputStream(FileUtils.openInputStream(file));
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		
		return image;
	}
	
	public void addLogoImage(LogoImage logoImage, InputStream is) {
		imageDao.addLogoImage(logoImage, is);
	}

	public void updateLogoImage(LogoImage logoImage, File file) throws ImageNotFoundException {
		// clear cache
		List<Long> list = getLogoImageIdList(logoImage.getObjectType(), logoImage.getObjectId());
		for (Long logoImageId : list) {
			this.logoImageCache.remove(logoImageId);
		}
		this.logoImageIdsCache.remove(getLogoImageIdListCacheKey(logoImage.getObjectType(), logoImage.getObjectId()));
		if (file != null)
			deleteImageFileCache(logoImage);
		Date now = Calendar.getInstance().getTime();
		logoImage.setModifiedDate(now);
		imageDao.updateLogoImage(logoImage, file);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeLogoImage(LogoImage logoImage) throws ImageNotFoundException {
		if (logoImage.getImageId() > 1) {
			// clear cache
			List<Long> list = getLogoImageIdList(logoImage.getObjectType(), logoImage.getObjectId());
			for (Long logoImageId : list) {
				this.logoImageCache.remove(logoImageId);
			}
			this.logoImageIdsCache
					.remove(getLogoImageIdListCacheKey(logoImage.getObjectType(), logoImage.getObjectId()));
			imageDao.removeLogoImage(logoImage);
			deleteImageFileCache(logoImage);
		}
	}

	private void deleteImageFileCache(LogoImage image) {
		Collection<File> files = FileUtils.listFiles(getImageCacheDir(),
				FileFilterUtils.prefixFileFilter(Long.toString(image.getImageId())),
				FileFilterUtils.suffixFileFilter(".logo"));
		for (File file : files) {
			log.debug("deleting {} - {}.", file.getPath(), file.isFile());
			try {
				FileUtils.forceDelete(file);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	public LogoImage getLogoImageById(Long logoId) throws ImageNotFoundException {
		if (logoId < 1)
			throw new ImageNotFoundException();
		LogoImage imageToUse;
		if (logoImageCache.get(logoId) == null) {
			imageToUse = imageDao.getLogoImageById(logoId);
			logoImageCache.put(new Element(imageToUse.getImageId(), imageToUse));
		} else {
			imageToUse = (LogoImage) logoImageCache.get(logoId).getObjectValue();
		}
		return imageToUse;
	}

	public LogoImage getPrimaryLogoImage(int objectType, long objectId) throws ImageNotFoundException {
		List<LogoImage> list = getLogoImages(objectType, objectId);
		for (LogoImage logo : list) {
			if (logo.isPrimary())
				return logo;
		}
		throw new ImageNotFoundException();
	}

	public List<LogoImage> getLogoImages(int objectType, long objectId) {
		List<Long> ids = getLogoImageIdList(objectType, objectId);
		List<LogoImage> list = new ArrayList<LogoImage>(ids.size());
		for (long logoId : ids) {
			try {
				list.add(getLogoImageById(logoId));
			} catch (ImageNotFoundException e) {
			}
		}
		return list;
	}

	public int getLogoImageCount(int objectType, long objectId) {
		String key = getLogoImageIdListCacheKey(objectType, objectId);
		if (logoImageIdsCache.get(key) != null) {
			return ((List<Long>) logoImageIdsCache.get(key).getObjectValue()).size();
		}
		return imageDao.getLogoImageCount(objectType, objectId);
	}

	public InputStream getImageInputStream(LogoImage image) throws IOException {
		try {
			File file = getImageFromCacheIfExist(image);
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			throw new RuntimeError(e);
		}
	}

	public InputStream getImageThumbnailInputStream(LogoImage image, int width, int height) {
		try {
			File file = getThumbnailFromCacheIfExist(image, width, height);
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			throw new RuntimeError(e);
		} finally {

		}
	}

	protected List<Long> getLogoImageIdList(int objectType, long objectId) {
		String key = getLogoImageIdListCacheKey(objectType, objectId);
		List<Long> idsList;
		if (logoImageIdsCache.get(key) == null) {
			idsList = imageDao.getLogoImageIds(objectType, objectId);
			logoImageIdsCache.put(new Element(key, idsList));
		} else {
			idsList = (List<Long>) logoImageIdsCache.get(key).getObjectValue();
		}
		return idsList;
	}

	protected String getLogoImageIdListCacheKey(int objectType, long objectId) {
		return (new StringBuilder()).append("objectType-").append(objectType).append("-objectId-").append(objectId).toString();
	}

	protected String toThumbnailFilename(LogoImage image, int width, int height) {
		StringBuilder sb = new StringBuilder();
		sb.append(image.getImageId()).append("_").append(width).append("_").append(height).append(".logo");
		return sb.toString();
	}

	protected File getThumbnailFromCacheIfExist(LogoImage image, int width, int height) throws IOException {
		try {
			lock.lock();
			log.debug("thumbnail : " + width + " x " + height);
			File dir = getImageCacheDir();
			File file = new File(dir, toThumbnailFilename(image, width, height));
			File originalFile = getImageFromCacheIfExist(image);
			log.debug("orignal image source: " + originalFile.getAbsoluteFile() + ", " + originalFile.length()
					+ " thumbnail:" + file.getAbsoluteFile() + " - " + file.exists());
			if (file.exists()) {
				log.debug("file size : {}", file.length());
				if (file.length() > 0) {
					image.setThumbnailSize((int) file.length());
					return file;
				} else {
				}
			}

			/**
			 * TIP : 윈동우 경우 Thumbnail 파일 생성후에도 해당 파일을 참조하는 문제가 있음 이를 해결하기 위하여
			 * 임시파이을 생성하고 이를 다시 복사하도록 함.
			 */
			log.debug("create thumbnail {}.", file.getAbsolutePath());
			if (Platform.current() == Platform.WINDOWS) {
				File tmp = getTemeFile();
				Thumbnails.of(originalFile).size(width, height).outputFormat("png").toOutputStream(new FileOutputStream(tmp));
				image.setThumbnailSize((int) tmp.length());
				FileUtils.copyFile(tmp, file);
			} else {
				try {
					Thumbnails.of(originalFile).allowOverwrite(true).size(width, height).outputFormat("png").toOutputStream(new FileOutputStream(file));
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
				}
				image.setThumbnailSize((int) file.length());
			}
			return file;
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 
	 * @param image
	 * @return
	 * @throws IOException
	 */
	protected File getImageFromCacheIfExist(LogoImage image) throws IOException {
		File dir = getImageCacheDir();
		StringBuilder sb = new StringBuilder();
		sb.append(image.getImageId()).append(".logo");
		File file = new File(dir, sb.toString());
		if (file.exists()) {
			long size = FileUtils.sizeOf(file);
			if (size != image.getSize()) {
				InputStream inputStream = imageDao.getInputStream(image);
				FileUtils.copyInputStreamToFile(inputStream, file);
			}
		} else {
			// doesn't exist, make new one ..
			InputStream inputStream = imageDao.getInputStream(image);
			FileUtils.copyInputStreamToFile(inputStream, file);
		}
		return file;
	}

	protected synchronized File getImageDir() {
		if (imageDir == null) {
			imageDir = repository.getFile("images");
			if (!imageDir.exists()) {
				boolean result = imageDir.mkdir();
				if (!result)
					log.error((new StringBuilder()).append("Unable to create image directory: '").append(imageDir).append("'").toString());
				getImageCacheDir();
				getImageTempDir();
			} else {
				File dir = getImageTempDir();
				try {
					FileUtils.cleanDirectory(dir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return imageDir;
	}

	protected File getImageCacheDir() {
		File dir = new File(getImageDir(), "cache");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	protected File getImageTempDir() {
		File dir = new File(getImageDir(), "temp");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	protected File getTemeFile() {
		UUID uuid = UUID.randomUUID();
		File tmp = new File(getImageTempDir(), uuid.toString());
		return tmp;
	}

	public boolean isImageEnabled() {
		return imageConfig.isEnabled();
	}

	public void setImageEnabled(boolean enabled) {
		imageConfig.setEnabled(enabled);
		String str = (new StringBuilder()).append("").append(enabled).toString();
		// ApplicationHelper.getConfigService().setApplicationProperty("image.enabled",
		// str);
	}

	public int getMaxImageSize() {
		return imageConfig.getMaxImageSize();
	}

	public void setMaxImageSize(int maxImageSize) {
		imageConfig.setMaxImageSize(maxImageSize);
		String str = (new StringBuilder()).append("").append(maxImageSize).toString();
		// ApplicationHelper.getConfigService().setApplicationProperty("image.maxImageSize",
		// str);
	}

	public int getImagePreviewMaxSize() {
		return imageConfig.getImagePreviewMaxSize();
	}

	public void setImagePreviewMaxSize(int imagePreviewMaxSize) {
		imageConfig.setImagePreviewMaxSize(imagePreviewMaxSize);
		String str = (new StringBuilder()).append("").append(imagePreviewMaxSize).toString();
		// ApplicationHelper.getConfigService().setApplicationProperty("image.imagePreviewMaxSize",
		// str);
	}

	public boolean isForceThumbnailsEnabled() {
		return imageConfig.isForceThumbnailsEnabled();
	}

	public void setFourceThumbnailsEnabled(boolean forceThumbnailsEnabled) {
		imageConfig.setForceThumbnailsEnabled(forceThumbnailsEnabled);
		String str = (new StringBuilder()).append("").append(forceThumbnailsEnabled).toString();
		// ApplicationHelper.getConfigService().setApplicationProperty("image.forceThumbnailsEnabled",
		// str);
	}

	public int getImageMaxWidth() {
		return imageConfig.getImageMaxWidth();
	}

	public void setImageMaxWidth(int imageMaxWidth) {
		imageConfig.setImageMaxWidth(imageMaxWidth);
		String str = (new StringBuilder()).append("").append(imageMaxWidth).toString();
		// ApplicationHelper.getConfigService().setApplicationProperty("image.imageMaxWidth",
		// str);
	}

	public int getImageMaxHeight() {
		return imageConfig.getImageMaxHeight();
	}

	public void setImageMaxHeight(int imageMaxHeight) {
		imageConfig.setImageMaxHeight(imageMaxHeight);
		String str = (new StringBuilder()).append("").append(imageMaxHeight).toString();
		// ApplicationHelper.getConfigService().setApplicationProperty("image.imageMaxHeight",
		// str);
	}

	public boolean isValidType(String contentType) {
		boolean flag;
		if (isAllowAllByDefault())
			flag = !getDisallowedTypes().contains(contentType);
		else
			flag = getAllowedTypes().contains(contentType);
		return flag;
	}

	public List<String> getDisallowedTypes() {
		return imageConfig.getDisallowedTypes();
	}

	public void addDisallowedType(String contentType) {
		if (!imageConfig.getDisallowedTypes().contains(contentType)) {
			imageConfig.getDisallowedTypes().add(contentType);
			String str = listToString(imageConfig.getDisallowedTypes());
			// ApplicationHelper.getConfigService().setApplicationProperty("image.disallowedTypes",
			// str);
		}
	}

	public void removeDisallowedType(String contentType) {
		if (imageConfig.getDisallowedTypes().contains(contentType)) {
			imageConfig.getDisallowedTypes().remove(contentType);
			String str = listToString(imageConfig.getDisallowedTypes());
			// ApplicationHelper.getConfigService().setApplicationProperty("image.disallowedTypes",
			// str);
		}
	}

	public void addAllowedType(String contentType) {
		if (!imageConfig.getAllowedTypes().contains(contentType)) {
			imageConfig.getAllowedTypes().add(contentType);
			String str = listToString(imageConfig.getAllowedTypes());
			// ApplicationHelper.getConfigService().setApplicationProperty("image.allowedTypes",
			// str);
		}
	}

	public void removeAllowedType(String contentType) {
		if (imageConfig.getAllowedTypes().contains(contentType)) {
			imageConfig.getAllowedTypes().remove(contentType);
			String str = listToString(imageConfig.getAllowedTypes());
			// ApplicationHelper.getConfigService().setApplicationProperty("image.allowedTypes",
			// str);
		}
	}

	public List<String> getAllowedTypes() {
		return imageConfig.getAllowedTypes();
	}

	public boolean isAllowAllByDefault() {
		return imageConfig.isAllowAllByDefault();
	}

	public void setAllowAllByDefault(boolean allowed) {
		imageConfig.setAllowAllByDefault(allowed);
		String str = (new StringBuilder()).append("").append(allowed).toString();
		// ApplicationHelper.getConfigService().setApplicationProperty("image.allowAllByDefault",
		// str);
	}

	public Image createImage(int objectType, long objectId, String name, String contentType, File file) {
		Date now = new Date();
		DefaultImage image = new DefaultImage();
		image.setCreationDate(now);
		image.setModifiedDate(now);
		image.setObjectType(objectType);
		image.setObjectId(objectId);
		image.setContentType(contentType);
		image.setName(name);
		image.setImageId(-1L);

		image.setSize((int) FileUtils.sizeOf(file));
		try {
			image.setInputStream(FileUtils.openInputStream(file));
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		return image;
	}

	public Image createImage(int objectType, long objectId, String name, String contentType, InputStream inputStream) {
		Date now = new Date();
		DefaultImage image = new DefaultImage();
		image.setCreationDate(now);
		image.setModifiedDate(now);
		image.setObjectType(objectType);
		image.setObjectId(objectId);
		image.setContentType(contentType);
		image.setName(name);
		image.setImageId(-1L);
		image.setInputStream(inputStream);
		try {
			image.setSize(IOUtils.toByteArray(inputStream).length);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		return image;
	}
	
	public Image createImage(int objectType, long objectId, String name, String contentType, InputStream inputStream, int size) {
		Date now = new Date();
		DefaultImage image = new DefaultImage();
		image.setCreationDate(now);
		image.setModifiedDate(now);
		image.setObjectType(objectType);
		image.setObjectId(objectId);
		image.setContentType(contentType);
		image.setName(name);
		image.setImageId(-1L);
		image.setInputStream(inputStream);
		image.setSize(size);
		
		return image;
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Image image) {
		try {
			if (image.getImageId() < 0) {
				imageDao.createImage(image);
				imageDao.saveImageInputStream(image, image.getInputStream());
			} else {
				Date now = new Date();
				((DefaultImage) image).setModifiedDate(now);
				imageDao.updateImage(image);
				if( image.getInputStream() != null )
					imageDao.saveImageInputStream(image, image.getInputStream());
			}
			invalidate(image, true);	
		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Image saveImage(Image image) {
		try { 
			if (image.getImageId() < 0) {
				Image newImage = imageDao.createImage(image);
				imageDao.saveImageInputStream(newImage, image.getInputStream());
			} else {
				Date now = new Date();
				((DefaultImage) image).setModifiedDate(now);
				imageDao.updateImage(image);
				if( image.getInputStream() != null )
					imageDao.saveImageInputStream(image, image.getInputStream());
			}
			invalidate(image, true);
			Image imageToUse = getImage(image.getImageId());
			return imageToUse;
		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteImage(Image image)  {
		if (image.getImageId() > 1) {
			Image imageToUse = imageDao.getImageById(image.getImageId());
			try {
				getImageLink(imageToUse);
				removeImageLink(imageToUse);
			} catch (NotFoundException e) {}
			imageDao.deleteImage(imageToUse);
			invalidate(imageToUse, true);
		}
	}
	
	public void invalidate(Image image, boolean deleteFile) {
		if( imageCache.isKeyInCache(image.getImageId())) 
			imageCache.remove(image.getImageId());
		
		if(deleteFile) {
			Collection<File> files = FileUtils.listFiles(getImageCacheDir(), FileFilterUtils.prefixFileFilter(image.getImageId() + ""), null);
			for (File file : files) {
				FileUtils.deleteQuietly(file);
			}
		}
	}

	public Image getImage(long imageId) throws NotFoundException {
		Image imageToUse = null;
		if (imageCache.get(imageId) == null) {
			try {
				imageToUse = getImageById(imageId);
				if(	imageToUse.getUser() != null && imageToUse.getUser().getUserId() > 0 ) {
					User user = userManager.getUser(imageToUse.getUser().getUserId());
					imageToUse.setUser(user);
				}
				imageCache.put(new Element(imageId, imageToUse));
			} catch (Exception e) {
				String msg = (new StringBuilder()).append("Unable to find image ").append(imageId).toString();
				throw new NotFoundException(msg, e);
			}
		} else {
			imageToUse = (Image) imageCache.get(imageId).getObjectValue();
		}
		return imageToUse;
	}

	private Image getImageById(long imageId) throws NotFoundException {
		try {
			return imageDao.getImageById(imageId);
		} catch (Exception e) {
			String msg = (new StringBuilder()).append("Unable to find image ").append(imageId).toString();
			throw new NotFoundException(msg, e);
		}
	}

	public InputStream getImageInputStream(Image image) {
		try {
			File file = getImageFromCacheIfExist(image);
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			throw new RuntimeError(e);
		}
	}

	public InputStream getImageThumbnailInputStream(Image image, int width, int height) {
		try {
			File file = getThumbnailFromCacheIfExist(image, width, height);
			return FileUtils.openInputStream(file);
		} catch (Exception e) {
			throw new RuntimeError(e.getMessage(), e);
		}
	}

	public Image getImageByImageLink(String linkId) throws NotFoundException {
		Long imageIdToUse = -1L;
		if (imageLinkIdCache.get(linkId) == null) {
			try {
				ImageLink link = imageLinkDao.getImageLink(linkId);
				imageLinkIdCache.put(new Element(link.getLinkId(), link.getImageId()));
				return getImageById(link.getImageId());
			} catch (Exception e) {
				String msg = (new StringBuilder()).append("Unable to find image ").append(linkId).toString();
				throw new NotFoundException(msg, e);
			}
		} else {
			imageIdToUse = (Long) imageLinkIdCache.get(linkId).getObjectValue();
		}
		return getImage(imageIdToUse);
	}

	public ImageLink getImageLink(Image image) throws NotFoundException {
		ImageLink link = null;
		Long imageIdToUse = image.getImageId();
		if (imageLinkCache.get(imageIdToUse) == null) {
			try {
				link = imageLinkDao.getImageLinkByImageId(imageIdToUse);
				imageLinkCache.put(new Element(imageIdToUse, link));
			} catch (Exception e) {
				String msg = (new StringBuilder()).append("Unable to find image link for iamge : ").append(imageIdToUse).toString();
				throw new NotFoundException(msg, e);
			}
		} else {
			link = (ImageLink) imageLinkCache.get(imageIdToUse).getObjectValue();
		}
		return link;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(ImageLink image) {
		try { 
			if (image.getImageId() > 0) {
				imageLinkDao.update(image); 
				imageLinkIdCache.remove(image.getLinkId());
				imageLinkCache.remove(image.getImageId());
			}
		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public ImageLink getImageLink(Image image, boolean createIfNotExist) throws NotFoundException {
		try {
			return getImageLink(image);
		} catch (NotFoundException e) {
			if (createIfNotExist) {
				ImageLink link = new ImageLink(RandomStringUtils.random(64, true, true), image.getImageId(), true);
				imageLinkDao.saveImageLink(link);
				imageLinkCache.put(new Element(image.getImageId(), link));
				return link;
			} else {
				throw e;
			}
		}
	}

	public void removeImageLink(Image image) {
		try {
			ImageLink link = getImageLink(image);
			imageLinkIdCache.remove(link.getLinkId());
			imageLinkCache.remove(image.getImageId());
			imageLinkDao.removeImageLink(link);
		} catch (NotFoundException e) {
		}
	}


	
	

	/**
	 * 
	 * @param image
	 * @return
	 * @throws IOException
	 */
	protected File getImageFromCacheIfExist(Image image) throws IOException {
		File dir = getImageCacheDir();
		File file = new File(dir, toImageFilename(image));
		if (file.exists()) {
			long size = FileUtils.sizeOf(file);
			if (size != image.getSize()) {
				// size different make cache new one....
				InputStream inputStream = imageDao.getImageInputStream(image);
				FileUtils.copyInputStreamToFile(inputStream, file);
			}
		} else {
			// doesn't exist, make new one ..
			InputStream inputStream = imageDao.getImageInputStream(image);
			FileUtils.copyInputStreamToFile(inputStream, file);
		}
		return file;
	}
	
	private String toImageFilename(Image image) {
		StringBuilder sb = new StringBuilder();
		sb.append(image.getImageId()).append(".bin");
		return sb.toString();
	}

	protected String toThumbnailFilename(Image image, int width, int height) {
		StringBuilder sb = new StringBuilder();
		sb.append(image.getImageId()).append("_").append(width).append("_").append(height).append(".bin");
		return sb.toString();
	}

	protected File getThumbnailFromCacheIfExist(Image image, int width, int height) throws IOException, JCodecException {

		log.debug("extracting thumbnail {}x{} for {}", width , height, image.getContentType());
		File dir = getImageCacheDir();
		File targetFile = new File(dir, toThumbnailFilename(image, width, height));
		File sourceFile = getImageFromCacheIfExist(image);
		log.debug("source: " + sourceFile.getAbsoluteFile() + ", " + sourceFile.length());
		log.debug("target:" + targetFile.getAbsoluteFile());
		if (targetFile.exists() && targetFile.length() > 0) {
			image.setThumbnailSize((int) targetFile.length());
			return targetFile;
		}
		lock.lock(); 
		try {
			log.debug("extracting thumbnail from {}", image.getContentType() );
			if (image.getContentType().startsWith("video")) {  
				Picture picture = FrameGrab.getFrameFromFile(sourceFile, 0);  
				log.debug("frame from image {} x {} ", picture.getWidth(), picture.getHeight());
				//for JDK (jcodec-javase)
				BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
				ImageIO.write(bufferedImage, IMAGE_PNG_FORMAT, targetFile );  
				return targetFile;
			}else
			if (StringUtils.startsWithIgnoreCase(image.getContentType(), "image")) { 
				BufferedImage originalImage = ImageIO.read(sourceFile);
				log.debug("from original image {} x {} ", originalImage.getWidth(), originalImage.getHeight());
				if (originalImage.getHeight() < height || originalImage.getWidth() < width) {
					FileUtils.copyFile(sourceFile, targetFile);
				}else {
					BufferedImage thumbnail = Thumbnails.of(originalImage).size(width, height).asBufferedImage();
					ImageIO.write(thumbnail, "png", targetFile);					
				} 
				image.setThumbnailSize((int) targetFile.length());
				return targetFile;	
			}
			
		}finally {
			lock.unlock();
		}
		return null;
	}

	public void initialize() {
		log.debug("initializing image manager");
		ImageConfig imageConfigToUse = new ImageConfig();
		/*
		 * imageConfigToUse.setEnabled(
		 * ApplicationHelper.getApplicationBooleanProperty("image.enabled",
		 * true) ); imageConfigToUse.setAllowAllByDefault(
		 * ApplicationHelper.getApplicationBooleanProperty(
		 * "image.allowAllByDefault", true) );
		 * imageConfigToUse.setForceThumbnailsEnabled(
		 * ApplicationHelper.getApplicationBooleanProperty(
		 * "image.forceThumbnailsEnabled", true) );
		 * imageConfigToUse.setMaxImageSize(
		 * ApplicationHelper.getApplicationIntProperty("", 2048) );
		 * imageConfigToUse.setImagePreviewMaxSize(ApplicationHelper.
		 * getApplicationIntProperty("image.imagePreviewMaxSize", 250));
		 * imageConfigToUse.setImageMaxWidth(ApplicationHelper.
		 * getApplicationIntProperty("image.imageMaxWidth", 450));
		 * imageConfigToUse.setImageMaxHeight(ApplicationHelper.
		 * getApplicationIntProperty("image.imageMaxHeight", 600));
		 * imageConfigToUse.setMaxImagesPerObject(ApplicationHelper.
		 * getApplicationIntProperty("image.maxImagesPerObject", 50));
		 * imageConfigToUse.setAllowedTypes(
		 * stringToList(ApplicationHelper.getApplicationProperty(
		 * "image.allowedTypes", "")) ); imageConfigToUse.setDisallowedTypes(
		 * stringToList(
		 * ApplicationHelper.getApplicationProperty("image.disallowedTypes",
		 * "")));
		 */
		this.imageConfig = imageConfigToUse;
		getImageDir();
		log.debug(imageConfig.toString());
	}

	public void destroy() {

	}

	@Override
	public List<Image> getImages(int objectType, long objectId) {
		List<Long> ids = customQueryJdbcDao.getExtendedJdbcTemplate().queryForList(
			customQueryJdbcDao.getBoundSql("COMMUNITY_WEB.SELECT_IMAGE_IDS_BY_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
			Long.class,
			new SqlParameterValue(Types.NUMERIC, objectType),
			new SqlParameterValue(Types.NUMERIC, objectId)
		);
		List<Image> list = new ArrayList<Image>(ids.size());
		for( Long id : ids ) {
			try {
				list.add(getImage(id));
			} catch (NotFoundException e) {
				
			}
		}
		return list;
	}

}
