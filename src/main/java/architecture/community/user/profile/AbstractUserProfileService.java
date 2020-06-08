package architecture.community.user.profile;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.exception.NotFoundException;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.user.User;
import architecture.community.user.UserProfile;
import architecture.community.user.UserProfileService;
import architecture.community.util.CommunityConstants;
import architecture.ee.service.ConfigService;

public abstract class AbstractUserProfileService implements UserProfileService , InitializingBean {
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("configService")
	ConfigService configService;	
	
	@Autowired(required=false)
	@Qualifier("customQueryJdbcDao")
	protected CustomQueryJdbcDao customQueryJdbcDao ;
	
	private com.google.common.cache.LoadingCache<Long, UserProfile> profileCache = null;
	 
	public boolean isCacheable() {
		return configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_USER_PROFILE_CACHEABLE_PROP_NAME, false);
	}

	public void setCacheable(boolean cacheable) {
		configService.setApplicationProperty(CommunityConstants.SERVICES_USER_PROFILE_CACHEABLE_PROP_NAME, Boolean.toString(cacheable));
	}

	public void setEnabled(boolean enabled) {
		configService.setApplicationProperty(CommunityConstants.SERVICES_USER_PROFILE_ENABLED_PROP_NAME, Boolean.toString(enabled));
	}

	public boolean isEnabled() {
		return configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_USER_PROFILE_ENABLED_PROP_NAME, false);
	}	
	
	public void afterPropertiesSet() throws Exception { 
		
		if( isCacheable() ) {
			createCacheIfNotExist();
		}
	}
	
	private void createCacheIfNotExist() {
		if( profileCache == null) {
			log.debug("Creating cache for User Profile.");
			profileCache = CacheBuilder.newBuilder().maximumSize(5000).expireAfterAccess( 10, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, UserProfile>(){			
					public UserProfile load(Long userId) throws Exception {
						return loadUserProfile(userId);
				}}
			);
		}
	}
	
	public void refresh () {
		createCacheIfNotExist(); 
		if( isCacheable()  && profileCache != null ) {  
			profileCache.invalidateAll(); 
		}
	} 
	
	public UserProfile getUserProfile(User user) throws NotFoundException { 
		UserProfile profile = null;
		try {
			if( isCacheable() ) {
				createCacheIfNotExist(); 
				profile = profileCache.get(user.getUserId());
			} else {
				profile = loadUserProfile (user.getUserId()); 
			}
			return profile;
		} catch (Exception e) {
			String msg = (new StringBuilder()).append("Unable to find profile object by ").append(user.getUserId()).toString(); 
			throw new NotFoundException(msg, e);
		}
	} 
	
	 
	public void saveOrUpdate(User user, UserProfile profile) {  
		saveOrUpdate(profile);
		if( isCacheable() && profileCache != null )
			profileCache.invalidate(user.getUserId());
	}	

	protected abstract UserProfile loadUserProfile( Long userId ) throws  Exception; 
	
	protected abstract void saveOrUpdate( UserProfile profile ) ; 
	
}
