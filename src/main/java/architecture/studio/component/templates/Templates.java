package architecture.studio.component.templates;

import java.util.Date;

import architecture.community.model.PropertyModelObjectAware;
import architecture.community.user.User;

public interface Templates extends PropertyModelObjectAware {

	public Long getTemplatesId() ;

	public void setTemplatesId(Long templatesId) ;

	public String getName();

	public void setName(String name) ;

	public String getDisplayName();

	public void setDisplayName(String displayName) ;

	public String getDescription();

	public void setDescription(String description) ;

	public User getCreator() ;

	public void setCreator(User creator) ;

	public User getModifier() ;

	public void setModifier(User modifier) ;

	public Date getCreationDate() ;

	public void setCreationDate(Date creationDate);

	public Date getModifiedDate();

	public void setModifiedDate(Date modifiedDate) ;

	public String getSubject();
	
	public void setSubject(String subject);

	public String getBody();

	public void setBody(String body);
	
}
