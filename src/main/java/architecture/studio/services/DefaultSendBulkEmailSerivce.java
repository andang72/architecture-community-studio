package architecture.studio.services;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.exception.NotFoundException;
import architecture.community.user.UserManager;
import architecture.studio.components.email.SendBulkEmail;
import architecture.studio.components.email.SendEmail;
import architecture.studio.components.email.dao.SimpleEmailSendDao;

public class DefaultSendBulkEmailSerivce implements SendBulkEmailService {

	@Inject
	@Qualifier("userManager")
	private UserManager userManager;
	
	@Inject
	@Qualifier("simpleEmailSendDao")
	private SimpleEmailSendDao simpleEmailSendDao;
	
	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private com.google.common.cache.LoadingCache<Long, SendBulkEmail> sendEmailCache; 
	
	private boolean cacheable = true ; 
	
	public boolean isCacheable() {
		return cacheable;
	} 

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	} 

	public void initialize() {
		if(cacheable)
			createCache(1000L, 10L); 
	}
	
	private void createCache (Long maximumSize, Long duration) {  
		sendEmailCache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess( duration , TimeUnit.MINUTES).build(		
			new CacheLoader<Long, SendBulkEmail>(){			
				public SendBulkEmail load(Long sendEmailId) throws Exception { 
					return simpleEmailSendDao.getSendBulkEmail(sendEmailId);
			}}
		);
	}

	public SendBulkEmail getSendBulkEmail(long sendEmailId) throws NotFoundException {
		try { 
			return sendEmailCache.get(sendEmailId);
		} catch (Exception e) {
			String msg = (new StringBuilder()).append("Unable to find send bulk email ").append(sendEmailId).toString();
			throw new NotFoundException(msg, e);
		} 
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(SendEmail sendEmail) {  
		if(cacheable){ 
			sendEmailCache.invalidate(sendEmail.getEmailId());  
		}
		simpleEmailSendDao.saveOrUpdate(sendEmail);  
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void remove(SendEmail sendEmail) throws IOException {
		if(cacheable){ 
			sendEmailCache.invalidate(sendEmail.getEmailId());  
		}
		simpleEmailSendDao.saveOrUpdate(sendEmail);  
	}
	
}
