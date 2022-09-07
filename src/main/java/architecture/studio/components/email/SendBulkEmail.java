package architecture.studio.components.email;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyAwareSupport;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.community.user.model.json.JsonUserDeserializer;
import architecture.studio.components.templates.Templates;

public class SendBulkEmail extends PropertyAwareSupport implements SendEmail  {
	
	private Long emailId;
	
	private String fromEmailAddress ;
	
	private Templates templates ;
	
	private User creator;
	
	private User modifier;
	
	private Date creationDate;
	
	private Date modifiedDate; 
	
	public SendBulkEmail() {
		 
		this.emailId = -1L;
		this.fromEmailAddress = null;
		this.templates = null;
		this.creator = new UserTemplate(-1L);
		this.modifier = new UserTemplate(-1L);
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate; 
	
	}
 
	public Long getEmailId() {
		return emailId;
	}

	public void setEmailId(Long emailId) {
		this.emailId = emailId;
	}

	public String getFromEmailAddress() {
		return fromEmailAddress;
	}

	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}

	public Templates getTemplates() {
		return templates;
	}

	public void setTemplates(Templates templates) {
		this.templates = templates;
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
}
