package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.io.FileReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import architecture.community.exception.NotFoundException;
import architecture.community.web.spring.freemarker.FreemarkerHelper;
import architecture.ee.service.Repository;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class GroovyClassBuilder {

	private static BeansWrapper wrapper = new BeansWrapperBuilder(Configuration.VERSION_2_3_25).build();
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private DefaultResourceLoader loader = new DefaultResourceLoader() ;
	
	private Repository repository;
	
	public void build(GroovyForm form, Writer writer ) throws Exception { 
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("data", form);
		populateStatics(model);
		
		freemarker.template.SimpleHash root = new freemarker.template.SimpleHash(wrapper); 
		root.putAll(model); 
		
		Template template = getTemplate(); 
		template.process(root, writer); 
	}
	
	
	protected void populateStatics(Map<String, Object> model) {
		FreemarkerHelper.populateStatics(wrapper, model); 
	}
	
	private Template getTemplate() throws NotFoundException {
		try {
			//Resource resource = loader.getResource("groovy-template.ftl");   
			//InputStream is = resource.getInputStream();
			// InputStreamReader reader = new InputStreamReader( is ); 
			
			File file = repository.getConfigRoot().getFile("groovy-template.ftl");
			FileReader reader = new FileReader(file);
			freemarker.template.Template template = new freemarker.template.Template("groovy-template", reader, null);
			template.setNumberFormat("computer");
			return template;
		} catch (Throwable e) {
			throw new NotFoundException("", e);
		}
	} 
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
}
