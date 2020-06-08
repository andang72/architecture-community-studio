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

package architecture.community.viewcount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.model.Models;
import architecture.community.page.Page;
import architecture.community.viewcount.dao.ViewCountDao;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.event.EventSupport;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element; 


public class CommunityViewCountService extends EventSupport implements ViewCountService {

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private Map<String, ViewCountInfo> queue;

	private Lock lock = new ReentrantLock();
	
	private boolean viewCountsEnabled; 
	
	@Inject
	@Qualifier("configService")
	protected ConfigService configService;

	@Inject
	@Qualifier("viewCountDao")
	protected ViewCountDao viewCountDao;

	@Inject
	@Qualifier("viewCountCache")
	private Cache viewCountCache;
	
	
	public CommunityViewCountService() {
		this.viewCountsEnabled = true;
	}

	public boolean isViewCountsEnabled() {
		return viewCountsEnabled;
	}

	@PostConstruct
	public void initialize() throws Exception {
		logger.debug("initialize queue");
		this.queue = Collections.synchronizedMap(new HashMap<String, ViewCountInfo>());
		
		logger.debug("register event listener");
		registerEventListener(this);
	}
	
	@PreDestroy
	public void destory(){
		logger.debug("unregister event listener");
		unregisterEventListener(this);
	}
		

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addViewCount(int objectType, long objectId) {
		if (viewCountsEnabled) {
			addCount(objectType, objectId , viewCountCache, 1);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int getViewCount(int objectType, long objectId) {
		if (viewCountsEnabled) {
			return getCachedCount(objectType, objectId );
		} else {
			return -1;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void clearCount(int objectType, long objectId) {
		if (viewCountsEnabled) {
			String key = getCacheKey(objectType, objectId );
			queue.remove(key);
			doClearCount(objectType, objectId );
		}
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addViewCount(Page page) {
		if (viewCountsEnabled) {
			addCount(Models.PAGE.getObjectType(), page.getPageId(), viewCountCache, 1);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int getViewCount(Page page) {
		if (viewCountsEnabled) {
			return getCachedCount(Models.PAGE.getObjectType(), page.getPageId());
		} else {
			return -1;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void clearCount(Page page){
		if (viewCountsEnabled) {
			String key = getCacheKey(Models.PAGE.getObjectType(), page.getPageId());
			queue.remove(key);
			doClearCount(Models.PAGE.getObjectType(), page.getPageId());
		}
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateViewCounts() {		
		Map<String, ViewCountInfo> localQueue = queue;
		queue = Collections.synchronizedMap(new HashMap<String, ViewCountInfo>());
		logger.debug("update view counts {}", localQueue.size() );
		if (localQueue.size() > 0) {
			List<ViewCountInfo> list = new ArrayList<ViewCountInfo>(localQueue.values());
			viewCountDao.updateViewCounts(list);
		}		
	} 
	
	private synchronized void doClearCount(int entityType, long entityId) {
		viewCountDao.deleteViewCount(entityType, entityId);
	} 

	private void addCount(int entityType, long entityId, Cache cache, int amount) {
		int count = -1;
		String cacheKey = getCacheKey(entityType, entityId);
		if (cache.get(cacheKey) != null)
			count = (Integer) cache.get(cacheKey).getObjectValue() ;
		else
			count = viewCountDao.getViewCount(entityType, entityId);
		count += amount;
		cache.put( new Element(cacheKey, Integer.valueOf(count) ) );
		
		Map<String, ViewCountInfo> queueRef = queue;
		synchronized (queueRef) {
			queueRef.put(cacheKey, new ViewCountInfo(entityType, entityId, count));
		}
	}

	private int getCachedCount(Integer entityType, Long entityId) {		
		Integer cachedCount;
		String cacheKey = getCacheKey(entityType, entityId);
		if (viewCountCache.get(cacheKey) != null) {
			cachedCount = (Integer) viewCountCache.get(cacheKey).getObjectValue() ;			
		}else{
			try{
				lock.lock();
				cachedCount = viewCountDao.getViewCount(entityType, entityId);
				viewCountCache.put( new Element( cacheKey, cachedCount )  );				
			}finally{
				lock.unlock();
			}
		}
		return cachedCount;
	}

	private static String getCacheKey(int entityType, long entityId) {
		StringBuffer buf = new StringBuffer();
		buf.append(entityType).append("-").append(entityId);
		return buf.toString();
	}

}
