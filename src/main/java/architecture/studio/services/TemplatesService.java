package architecture.studio.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import architecture.community.exception.NotFoundException;
import architecture.studio.components.templates.Templates;

public interface TemplatesService {
	
	public Templates getTemplatesByName(String name) throws NotFoundException ;
	
	public Templates getTemplates(long templateId) throws NotFoundException ;
	
	public Templates createGenericTemplates(int objectType, long objectId, String name, String displayName, String description, String subject, InputStream file ) throws IOException ;
	
	public void saveOrUpdate(Templates forms);
	
	public void remove(Templates templates) throws IOException; 
	
	public String processBody( Templates templates , Map<String, Object> model ) throws Exception;
	
	public String processSubject( Templates templates , Map<String, Object> model ) throws Exception;
	
}
