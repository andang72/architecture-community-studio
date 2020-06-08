package architecture.community.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.eventbus.Subscribe;

import architecture.community.user.dao.UserAvatarDao;
import architecture.community.user.event.UserRemovedEvent;
import architecture.ee.exception.RuntimeError;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
import net.coobird.thumbnailator.Thumbnails;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CommunityUserAvatarService implements UserAvatarService {

	private static final Logger log = LoggerFactory.getLogger(CommunityUserAvatarService.class);

	private Lock lock = new ReentrantLock();

	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("userManager")
	private UserManager userManager;

	@Inject
	@Qualifier("repository")
	private Repository repository;

	@Inject
	@Qualifier("userAvatarDao")
	private UserAvatarDao userAvatarDao;

	@Inject
	@Qualifier("avatarImageIdsCache")
	private Cache avatarImageIdsCache;

	@Inject
	@Qualifier("avatarImageCache")
    private Cache avatarImageCache;

	 
	private File imageDir;
	
	public CommunityUserAvatarService() {
	}


	@Subscribe
	@EventListener 
	@Async
	public void handelUserRemovedEvent(UserRemovedEvent e) {
		log.debug("User romoved. Remove all avatar images for '{}'" , e.getUser().getUsername() );
		User user = e.getUser(); 
		for( AvatarImage img : getAvatarImages(user))
			removeAvatarImage(img);
	}
	
	
	/**
	 * Primary Avatar Image 를 리턴한다.
	 */
	public AvatarImage getAvatarImage(User user) throws AvatarImageNotFoundException { 
		
		AvatarImage image = getPrimaryUserAvatar(user);
		if( image == null)
			throw new AvatarImageNotFoundException();
		
		return image;
	}

	public AvatarImage getPrimaryUserAvatar(User user) {  
		List<AvatarImage> list = getAvatarImages(user);
		AvatarImage finalAvatarImage = null;
		for (AvatarImage image : list) {
		    if (image.isPrimary())
		    {	
		    	finalAvatarImage = image;
		    	break;
		    }
		}
		log.debug("find primary avatar image ({})  for user({})", finalAvatarImage!=null ? finalAvatarImage.getFilename() : "none", user.getUserId());
		return finalAvatarImage;				  
	 }
	
	public List<AvatarImage> getAvatarImages(User user) {		
		List<Long> list = getAvatarImageIdList(user.getUserId());		
		List<AvatarImage> images = new ArrayList<AvatarImage>(list.size());
		for(Long id : list) {
			try {
				images.add(getAvatarImageById(id));
			} catch (AvatarImageNotFoundException e) {
			}
		}
		return images;
	}

	public Integer getAvatarImageCount(User user) {
		return getAvatarImageIdList(user.getUserId()).size();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addAvatarImage(AvatarImage image, File file, User user) {
		if (image.getAvatarImageId() < 1) {
		    // clear cache
		    List<Long> list = getAvatarImageIdList(image.getUserId());		
		    for (Long avatarId : list) {
				avatarImageCache.remove(avatarId);
			}
		    avatarImageIdsCache.remove(image.getUserId()); 
		    userAvatarDao.addAvatarImage(image, file);
		}
	}
	
	public void addAvatarImage(AvatarImage image, InputStream is, User user) {
		if (image.getAvatarImageId() < 1) {
		    // clear cache
		    List<Long> list = getAvatarImageIdList(image.getUserId());		
		    for (Long logoImageId : list) {
				avatarImageCache.remove(logoImageId);
			}
		    avatarImageIdsCache.remove(image.getUserId()); 
		    userAvatarDao.addAvatarImage(image, is);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeAvatarImage(AvatarImage image) {	
		if (image.getAvatarImageId() < 1) {
			 // clear cache
			List<Long> list = getAvatarImageIdList(image.getUserId());			
			for (Long logoImageId : list) {
				avatarImageCache.remove(logoImageId);
			}
			avatarImageIdsCache.remove(image.getUserId()); 
			userAvatarDao.removeAvatarImage(image);
			
			deleteImageFileCache(image);
		}		
	}
 
	public AvatarImage getAvatarImageById(Long profileImageId) throws AvatarImageNotFoundException {
		if (profileImageId < 1)
		    throw new AvatarImageNotFoundException();
		AvatarImage imageToUse;
		if (avatarImageCache.get(profileImageId) == null) {
		    imageToUse = userAvatarDao.getAvatarImageById(profileImageId);
		    avatarImageCache.put( new Element(imageToUse.getAvatarImageId(), imageToUse) );
		} else {
		    imageToUse = (AvatarImage) avatarImageCache.get(profileImageId).getObjectValue() ;
		}
		return imageToUse;
	}
 
	public AvatarImage getAvatareImageByUsername(String username) throws AvatarImageNotFoundException, UserNotFoundException {
		User user = userManager.getUser(username);
		return getAvatarImage(user);
	}
	
	public InputStream getImageInputStream(AvatarImage image) {
		try {
			File file = getImageFromCacheIfExist(image);
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			throw new RuntimeError(e);
		}
	} 

	public InputStream getImageThumbnailInputStream(AvatarImage image, int width, int height) {
		try {
			File file = getThumbnailFromCacheIfExist(image, width, height);
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			throw new RuntimeError(e);
		} finally {
		}
	}	
	
	protected List<Long> getAvatarImageIdList(long userId) {
		List<Long> idsList;
		if (avatarImageIdsCache.get(userId) == null) {
			idsList = userAvatarDao.getAvatarImageIds(userId);
			avatarImageIdsCache.put( new Element( userId, idsList ) );
		} else {
			idsList = (List<Long>) avatarImageIdsCache.get(userId).getObjectValue() ;
		}
		return idsList;
	}
	 
	protected File getImageFromCacheIfExist(AvatarImage image) throws IOException {
		File dir = getAvatarImageCacheDir();
		StringBuilder sb = new StringBuilder();
		sb.append(image.getAvatarImageId()).append(".bin");
		File file = new File(dir, sb.toString());
		if (file.exists()) {
			long size = FileUtils.sizeOf(file);
			if (size != image.getImageSize()) {
				// size different make cache new one....
				InputStream inputStream = userAvatarDao.getInputStream(image);
				FileUtils.copyInputStreamToFile(inputStream, file);
			}
		} else {
			// doesn't exist, make new one ..
			InputStream inputStream = userAvatarDao.getInputStream(image);
			FileUtils.copyInputStreamToFile(inputStream, file);
		}
		return file;
	}

	protected File getThumbnailFromCacheIfExist(AvatarImage image, int width, int height) throws IOException {
		try {
			lock.lock();
			log.debug("thumbnail : " + width + " x " + height);
			File dir = getAvatarImageCacheDir();
			File file = new File(dir, toThumbnailFilename(image, width, height));
			File originalFile = getImageFromCacheIfExist(image);
			log.debug("orignal image source:{}, size:{}, thumbnail: {} , exist: {}.", 
					originalFile.getAbsoluteFile(), 
					originalFile.length(), 
					file.getAbsoluteFile(), 
					file.exists());
			if (file.exists()) {
				if (file.length() > 0) {
					image.setThumbnailSize((int) file.length());
					return file;
				} 
			}
			log.debug("create thumbnail image.");
			BufferedImage originalImage = ImageIO.read(originalFile);
			if (originalImage.getHeight() < height || originalImage.getWidth() < width) {
				image.setThumbnailSize(0);
				return originalFile;
			}
			BufferedImage thumbnail = Thumbnails.of(originalImage).size(width, height).asBufferedImage();
			ImageIO.write(thumbnail, "png", file);
			image.setThumbnailSize((int) file.length());
			
			/**
			 * TIP : 윈동우 경우 Thumbnail 파일 생성후에도 해당 파일을 참조하는 문제가 있음.
			 */
			return file;
		} finally {
			lock.unlock();
		}

	}

	protected synchronized File getImageDir() {
		if (imageDir == null) {
			imageDir = repository.getFile("images");
			if (!imageDir.exists()) {
				boolean result = imageDir.mkdir();
				if (!result)
					log.error((new StringBuilder()).append("Unable to create image directory: '").append(imageDir).append("'").toString());
			} else {
				File dir = getAvatarImageTempDir();
				try {
					FileUtils.cleanDirectory(dir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return imageDir;
	}

	protected File getAvatarImageDir() {
		File dir = new File(getImageDir(), "avatar");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	protected File getAvatarImageCacheDir() {
		File dir = new File(getAvatarImageDir(), "cache");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	protected File getAvatarImageTempDir() {
		File dir = new File(getAvatarImageDir(), "temp");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	protected File getTemeFile() {
		UUID uuid = UUID.randomUUID();
		File tmp = new File(getAvatarImageTempDir(), uuid.toString());
		return tmp;
	}

	protected String toThumbnailFilename(AvatarImage image, int width, int height) {
		StringBuilder sb = new StringBuilder();
		sb.append(image.getUserId()).append("_").append(image.getAvatarImageId()).append("_").append(width).append("_").append(height).append(".bin");
		return sb.toString();
	}

	private void deleteImageFileCache(AvatarImage image) {		
		StringBuilder sb = new StringBuilder();
		sb.append(image.getUserId()).append("_").append(image.getAvatarImageId());
		Collection<File> files = FileUtils.listFiles(getAvatarImageCacheDir(),
			FileFilterUtils.prefixFileFilter(sb.toString()),
			FileFilterUtils.suffixFileFilter(".bin"));
		for (File file : files) {
		    log.debug("{} found ({})" , file.getPath() , file.isFile());
		    try {
		    		FileUtils.forceDelete(file);
		    } catch (IOException e) {
		    		log.error(e.getMessage());
		    }
		}
	}

}