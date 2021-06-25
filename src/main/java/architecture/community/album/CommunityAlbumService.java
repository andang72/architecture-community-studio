package architecture.community.album;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.album.dao.AlbumDao;
import architecture.community.image.Image;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.ee.exception.RuntimeError;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class CommunityAlbumService implements AlbumService {

	@Inject
	@Qualifier("albumDao")
	private AlbumDao albumDao;


	@Inject
	@Qualifier("albumCache")
	private Cache albumCache;
	

	@Inject
	@Qualifier("userManager")
	private UserManager userManager;
	
	private com.google.common.cache.LoadingCache<Long, List<AlbumImage>> albumImagesCache; 
	
	private com.google.common.cache.LoadingCache<Long, List<AlbumContents>> albumContentsCache; 
	

	public CommunityAlbumService() {
		createCache(1000L, 10L);
	}

	
	private void createCache (Long maximumSize, Long duration) { 
		albumImagesCache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess( duration , TimeUnit.MINUTES).build(		
				new CacheLoader<Long, List<AlbumImage>>(){			
					public List<AlbumImage>  load(Long albumId) throws Exception { 
						return albumDao.getImages(new DefaultAlbum(albumId));
				}}
		);
		albumContentsCache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess( duration , TimeUnit.MINUTES).build(		
				new CacheLoader<Long, List<AlbumContents>>(){			
					public List<AlbumContents>  load(Long albumId) throws Exception { 
						return albumDao.getContents(new DefaultAlbum(albumId));
				}}
		);
	}

	
	public List<AlbumImage> getAlbumImages(Album album){
		try {
			return albumImagesCache.get(album.getAlbumId());
		} catch (ExecutionException e) {
			return Collections.EMPTY_LIST;
		}
	};


	@Override
	public List<AlbumContents> getAlbumContents(Album album) {
		try {
			return albumContentsCache.get(album.getAlbumId());
		} catch (ExecutionException e) {
			return Collections.EMPTY_LIST;
		}
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Album album) { 
		try { 
			if (album.getAlbumId() <= 0) {
				Album newAlbum = albumDao.create(album); 
			} else {
				Date now = new Date();
				((DefaultAlbum) album).setModifiedDate(now);
				if( albumCache.isKeyInCache(album.getAlbumId())) 
					albumCache.remove(album.getAlbumId());
				albumDao.update(album);
			} 
			Album albumToUse = getAlbum(album.getAlbumId()); 
		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void delete(Album album) { 
		albumDao.delete(album);
		if( albumCache.isKeyInCache(album.getAlbumId())) 
			albumCache.remove(album.getAlbumId()); 
	} 
	
	@Override
	public Album getAlbum(long albumId) throws AlbumNotFoundException {
		Album albumToUse = null;
		if (albumCache.get(albumId) == null) {
			try {
				albumToUse = albumDao.getById(albumId);
				if(	albumToUse.getUser() != null && albumToUse.getUser().getUserId() > 0 ) {
					User user = userManager.getUser(albumToUse.getUser().getUserId());
					albumToUse.setUser(user);
				}
				albumCache.put(new Element(albumId, albumToUse));
			} catch (Exception e) {
				String msg = (new StringBuilder()).append("Unable to find album ").append(albumId).toString();
				throw new AlbumNotFoundException(msg, e);
			}
		} else {
			albumToUse = (Album) albumCache.get(albumId).getObjectValue();
		}
		return albumToUse;
	}

	@Override
	public List<Album> getAlbums() { 
		return null;
	}
	 

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Album album, List<Image> images) {
		if( album.getAlbumId() > 0 & albumCache.isKeyInCache(album.getAlbumId())) 
			albumCache.remove(album.getAlbumId()); 	  
		albumImagesCache.invalidate(album.getAlbumId()); 
		albumDao.update(album, images);
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Album album , List<AlbumContents> contents, boolean clearBeforeUpdate) {
		if( album.getAlbumId() > 0 & albumCache.isKeyInCache(album.getAlbumId())) 
			albumCache.remove(album.getAlbumId()); 
		
		if( contents.size() > 0 ) {
			albumImagesCache.invalidate(album.getAlbumId());
			albumDao.update(album, contents, true);
		}
	}
	
}