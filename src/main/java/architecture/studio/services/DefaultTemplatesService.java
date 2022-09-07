package architecture.studio.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
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
import architecture.community.web.spring.freemarker.FreemarkerTemplateBuilder;
import architecture.ee.service.Repository;
import architecture.studio.components.templates.Templates;
import architecture.studio.components.templates.dao.TemplatesDao;
import freemarker.template.Template;

public class DefaultTemplatesService implements TemplatesService {

	@Inject
	@Qualifier("repository")
	private Repository repository;

	@Inject
	@Qualifier("userManager")
	private UserManager userManager;
	
	@Inject
	@Qualifier("templatesDao")
	private TemplatesDao templatesDao;
	
	private FreemarkerTemplateBuilder builder ;
	
	private boolean cacheable = true ;
	
	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private com.google.common.cache.LoadingCache<String, Long> templatesIdCache; 
	
	private com.google.common.cache.LoadingCache<Long, Templates> templatesCache; 
 
	public void initialize() {
		if(cacheable)
			createCache(1000L, 10L);
		builder = new FreemarkerTemplateBuilder();
	}

	
	private void createCache (Long maximumSize, Long duration) {  
		templatesIdCache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess( duration , TimeUnit.MINUTES).build(		
				new CacheLoader<String, Long>(){			
					public Long  load(String name) throws Exception { 
						return templatesDao.getTemplatesIdByName(name);
				}}
		);
		
		templatesCache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess( duration , TimeUnit.MINUTES).build(		
				new CacheLoader<Long, Templates>(){			
					public Templates load(Long formId) throws Exception { 
						return templatesDao.getTemplates(formId);
				}}
		);
	}
	
	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	 
	public Templates getTemplatesByName(String name) throws NotFoundException { 
		try {
			Long formsId = templatesIdCache.get(name);
			return templatesCache.get(formsId);
		} catch (Exception e) {
			throw new NotFoundException(e);
		} 
	}
 
	public Templates getTemplates(long templatesId) throws NotFoundException {
		try { 
			return templatesCache.get(templatesId);
		} catch (Exception e) {
			String msg = (new StringBuilder()).append("Unable to find templates ").append(templatesId).toString();
			throw new NotFoundException(msg, e);
		} 
	}

 
	public Templates createGenericTemplates(int objectType, long objectId, String name, String displayName, String description, String subject, InputStream file) throws IOException {
		return null;
	}
 
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Templates templates) {
		if(cacheable){
			templatesCache.invalidate(templates.getName());
			templatesCache.invalidate(templates.getTemplatesId());  
		} 
		templatesDao.saveOrUpdate(templates);
	}
 
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void remove(Templates templates) throws IOException {
		if(cacheable){
			templatesCache.invalidate(templates.getName());
			templatesCache.invalidate(templates.getTemplatesId());  
		} 
		templatesDao.saveOrUpdate(templates);
	}
	
	public String processSubject( Templates templates , Map<String, Object> model ) throws Exception {
		StringWriter stringWriter = new StringWriter();	
		Template  template = new Template ("templates-subject-"+ templates.getTemplatesId(), new StringReader(templates.getSubject()), builder.getConfiguration());	
		builder.processTemplate( template , model , stringWriter ); 
		return stringWriter.toString();
	}
	
	public String processBody( Templates templates , Map<String, Object> model ) throws Exception {
		StringWriter stringWriter = new StringWriter();	
		Template  template = new Template ("templates-body-"+ templates.getTemplatesId(), new StringReader(templates.getBody()), builder.getConfiguration());	
		builder.processTemplate( template , model , stringWriter ); 
		return stringWriter.toString();
	}
	
	
}
