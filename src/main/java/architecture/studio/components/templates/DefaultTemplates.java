package architecture.studio.components.templates;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.community.user.model.json.JsonUserDeserializer;

public class DefaultTemplates extends PropertyModelObjectAwareSupport implements Templates {
	
	private Long templatesId = UNKNOWN_OBJECT_ID;
	
	private String name ;
	
	private String displayName ;
	
	private String description ;
	
	private User creator;
	
	private User modifier;
	
	private Date creationDate;
	
	private Date modifiedDate;
	
	private String subject ;
	
	private String body ;
	
	public DefaultTemplates() { 
		super(-1, -1L);
		this.name = null;
		this.displayName = null;
		this.description = null;
		this.templatesId = -1L;
		this.subject = null;
		this.body = null; 
		this.creator = new UserTemplate(-1L);
		this.modifier = new UserTemplate(-1L);
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate; 
	}
	
	public DefaultTemplates(Long templatesId) {
		super(-1, -1L);
		this.name = null;
		this.displayName = null;
		this.description = null;
		this.templatesId = templatesId;
		this.subject = null;
		this.body = null; 
		this.creator = new UserTemplate(-1L);
		this.modifier = new UserTemplate(-1L);
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
	} 

	public Long getTemplatesId() {
		return templatesId;
	}

	public void setTemplatesId(Long templatesId) {
		this.templatesId = templatesId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getCreator() {
		return creator;
	}

	@JsonDeserialize(using = JsonUserDeserializer.class)
	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getModifier() {
		return modifier;
	}

	@JsonDeserialize(using = JsonUserDeserializer.class)
	public void setModifier(User modifier) {
		this.modifier = modifier;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
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
	
}