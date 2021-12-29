package architecture.studio.component.templates.dao;

import java.io.IOException;

import architecture.community.exception.NotFoundException;
import architecture.studio.component.templates.Templates;

public interface TemplatesDao {
 
	public abstract Long getTemplatesIdByName(String name);
	
	public Templates getTemplates(long templateId) throws NotFoundException ;
	 
	public void saveOrUpdate(Templates templates);
	
	public void remove(Templates templates) throws IOException; 
	
}
