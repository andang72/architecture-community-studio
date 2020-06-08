package architecture.community.share;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.exception.NotFoundException;
import architecture.community.share.dao.SharedLinkDao;
import architecture.ee.service.Repository;

public class CommunityLInkService implements SharedLinkService , InitializingBean {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	@Qualifier("repository")
	private Repository repository;
 
	@Inject
	@Qualifier("sharedLinkDao")
	private SharedLinkDao sharedLinkDao;
	
	private com.google.common.cache.LoadingCache<String, SharedLink> linkCache = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		log.debug("Creating Cache for LInk.");
		linkCache = CacheBuilder.newBuilder().maximumSize(5000).expireAfterAccess( 10, TimeUnit.MINUTES).build(		
				new CacheLoader<String, SharedLink>(){			
					public SharedLink load(String linkId) throws Exception {
						return sharedLinkDao.getSharedLink(linkId); 
				}}
		);
	}
	
	
	private NotFoundException newNotFoundException( String linkId, Exception e ) {
		
		String msg = (new StringBuilder()).append("Unable to find any object by ").append(linkId).toString();
		
		return new NotFoundException(msg , e);
	}
	
	private NotFoundException newNotFoundException( int objectType, long objectId, Exception e ) {
		
		String msg = (new StringBuilder()).append("Unable to find any object by t").append(objectType).append(" and ").append(objectId).toString();
		
		return new NotFoundException(msg , e);
	}
	
	public SharedLink getSharedLink(String linkId) throws NotFoundException { 
		try {
			return linkCache.get(linkId);
		} catch (ExecutionException e) {
			throw newNotFoundException(linkId, e);
		}
	}
	
	public SharedLink getSharedLink(int objectType, long objectId) throws NotFoundException {
		return getSharedLink(objectType, objectId, false);
	}
 
	public SharedLink getSharedLink(int objectType, long objectId, boolean createIfNotExist) throws NotFoundException {
		
		SharedLink link ;
		try {
			link = sharedLinkDao.getSharedLinkByObjectTypeAndObjectId(objectType, objectId);
		} catch (Exception e) {
			log.debug("fail to get link ..", e);
			
			if(createIfNotExist) {
				link = new SharedLink(RandomStringUtils.random(64, true, true), true, objectType, objectId );
				sharedLinkDao.saveOrUpdateSharedLink(link);
			}else {
				throw newNotFoundException(objectType, objectId , e);
			}
		}
		return link;
	}
 
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeSharedLink(String linkId) {
		linkCache.invalidate(linkId);
		sharedLinkDao.removeSharedLinkById(linkId);
		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
 	public void saveOrUpdate(SharedLink link) { 
		linkCache.invalidate(link.getLinkId());
		sharedLinkDao.saveOrUpdateSharedLink(link);
		
	}


}
