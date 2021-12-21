package architecture.community.forms;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public class FormTemplate implements Serializable {

	protected Log log = LogFactory.getLog(getClass());
	
	private static BeansWrapper wrapper = new BeansWrapper();
	
	String subject ;
	
	String body ;

	public FormTemplate() {
		super(); 
	}

	public FormTemplate(String subject, String body) {
		super();
		this.subject = subject;
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
		
	public String getBody(Map<String, Object> map) {
		return processTemplate( map );
	}
	
	protected String processTemplate(Map<String, Object> map) {
		StringReader reader = new StringReader(body);
		StringWriter writer = new StringWriter();
		try {
			populateStatics(map);
			freemarker.template.SimpleHash root = new freemarker.template.SimpleHash();
			root.putAll(map);			
			freemarker.template.Template template = new freemarker.template.Template( "mail", reader, null );
			template.setNumberFormat("computer");			
			template.process(root, writer);
		} catch (IOException e) {
			log.error(e);
		} catch (TemplateException e) {
			log.error(e);
		}
		return writer.toString();
	}
	
	protected static void populateStatics(Map<String, Object> model) {
		try {
			TemplateHashModel enumModels = wrapper.getEnumModels();
			model.put("enums", enumModels );
		} catch (UnsupportedOperationException e) {
		}		
		
		TemplateHashModel staticModels = wrapper.getStaticModels();
		try {
			model.put("ServletUtils",	staticModels.get("com.podosw.web.util.ServletUtils"));
			model.put("StringUtils",	staticModels.get("com.podosw.web.util.StringUtils"));
		} catch (TemplateModelException e) {

		}
		
		model.put("statics", BeansWrapper.getDefaultInstance().getStaticModels());
	}
}
