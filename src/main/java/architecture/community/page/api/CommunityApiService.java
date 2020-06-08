package architecture.community.page.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.page.PathPattern;
import architecture.community.page.api.dao.ApiDao;
import architecture.community.user.UserManager;

public class CommunityApiService implements ApiService {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	@Qualifier("userManager")
	private UserManager userManager;

	@Inject
	@Qualifier("apiDao")
	private ApiDao apiDao;
	
	private com.google.common.cache.LoadingCache<Long, Api> apiCache = null;
	
	private com.google.common.cache.LoadingCache<String, Long> apiIdCache = null;
	
	private com.google.common.cache.LoadingCache<String, List<PathPattern>> apiPatternMatchersCache = null;
	
	public CommunityApiService() { 
		apiCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(60 * 100, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, Api>(){			
					public Api load(Long apiId) throws Exception {
						Api api = apiDao.getApiById(apiId);
						return api;
				}}
		);
		apiIdCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(60 * 100, TimeUnit.MINUTES).build(		
				new CacheLoader<String, Long>(){			
					public Long load(String name) throws Exception {
						Long id = apiDao.getApiIdByName(name);
						return id;
				}}
		);
		
		apiPatternMatchersCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(60 * 100, TimeUnit.MINUTES).build(		
				new CacheLoader<String, List<PathPattern>>(){			
					public List<PathPattern>  load(String prefix) throws Exception {
						List<PathPattern> matchers =  new ArrayList<PathPattern>();
						for( Api p : apiDao.getAllApiHasPatterns()) {
							matchers.add(new PathPattern( p.getApiId(), prefix + p.getPattern()));
						} 
						return matchers;
				}}
		);
	}
 
	public Api getApi(String name) throws ApiNotFoundException {
		Long id;
		try {
			id = apiIdCache.get(name);
			return getApiById(id);
		} catch (ExecutionException e) {
			throw new ApiNotFoundException();
		}
		
	}

	public Api getApiById(long apiId) throws ApiNotFoundException {
		if (apiId < 1)
			throw new ApiNotFoundException();
		Api api = null;
		try {
			if (apiCache.get(apiId) != null) {
				api = apiCache.get(apiId);
			}
		}catch(Exception e) { 
			log.warn("Api Not Found. " , e);
		}
		if (api == null) {
			throw new ApiNotFoundException();
		}
		return api;
	}
	
	public List<PathPattern> getPathPatterns(String prefix){  
		try {
			return apiPatternMatchersCache.get(prefix);
		} catch (ExecutionException e) {
			return Collections.emptyList();
		}
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteApi(Api api) {
		if( api.getApiId() > 0 )
			apiCache.invalidate(api.getApiId());
		apiDao.deleteApi(api);
		
		apiPatternMatchersCache.invalidateAll();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Api api) {
		if( api.getApiId() > 0 )
			apiCache.invalidate(api.getApiId());
		apiDao.saveOrUpdate(api);
		
		apiPatternMatchersCache.invalidateAll();
	}

}
