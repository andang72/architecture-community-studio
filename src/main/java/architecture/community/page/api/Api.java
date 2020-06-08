package architecture.community.page.api;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyModelObjectAware;
import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.User;
import architecture.community.user.model.json.JsonUserDeserializer;

public class Api extends PropertyModelObjectAwareSupport implements PropertyModelObjectAware  {

	private Long apiId;
	
	private String mame;
	
	private String version;
	
	private String description;
	
	private String contentType;
	
	private boolean secured;
	
	private boolean enabled;
	
	private String scriptSource;
	
	private User creator; 
	
	private Date creationDate;
	
	private Date modifiedDate;
	
	private String title ;
	
	private String pattern;
	
	public Api( ) {
		this(-1, -1L);
		this.apiId = -1L;
		this.pattern = null;
		this.title = null;
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = this.creationDate;		 
	}

	public Api(long apiId  ) {
		this(-1, -1L);
		this.apiId = apiId;
		this.pattern = null;
		this.title = null;
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = this.creationDate;		 
	}
	
	public Api(int objectType, long objectId, long taskId) {
		super(objectType, objectId);
		this.title = null;
		this.pattern = null;
		this.apiId = taskId;
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = this.creationDate;		 
	}
	
	public Api(int objectType, long objectId) {
		super(objectType, objectId);
		this.title = null;
		this.apiId = -1L;
		this.pattern = null;
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = this.creationDate;		
	} 
	
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreationDate() {
		return creationDate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Long getApiId() {
		return apiId;
	}

	public void setApiId(Long apiId) {
		this.apiId = apiId;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public String getName() {
		return mame;
	}

	public void setName(String apiName) {
		this.mame = apiName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String apiVersion) {
		this.version = apiVersion;
	}
 
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getScriptSource() {
		return scriptSource;
	}

	public void setScriptSource(String scriptSource) {
		this.scriptSource = scriptSource;
	}

	public User getCreator() {
		return creator;
	}
	
	@JsonDeserialize(using = JsonUserDeserializer.class)
	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
}
