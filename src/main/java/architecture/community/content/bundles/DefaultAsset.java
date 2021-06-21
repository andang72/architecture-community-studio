package architecture.community.content.bundles;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.community.user.model.json.JsonUserDeserializer;

public class DefaultAsset extends PropertyModelObjectAwareSupport  implements Asset {
	
    private Long assetId = UNKNOWN_OBJECT_ID;
	
    private String filename;
    
    private int filesize ;
	
	private String description ;
	
	private String linkId;
	
	private boolean enabled;
	
	private boolean secured;
	
	private User creator;
	
	private Date creationDate;
	
	private Date modifiedDate;
	
	@JsonIgnore
	private InputStream inputStream;
	
	public DefaultAsset() {
		super(-1, -1L);
		this.filename = null;
		this.description = null;
		this.linkId = null; 
		this.enabled = false;
		this.secured = false;
		this.creator = new UserTemplate(-1L);
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
		this.filesize = 0 ;
	}
	
	public DefaultAsset(Long assetId) {
		super(-1, -1L);
		this.assetId = assetId;
		this.filename = null;
		this.description = null;
		this.linkId = null; 
		this.enabled = false;
		this.secured = false;
		this.creator = new UserTemplate(-1L);
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
		this.filesize = 0 ;
	}
	
	public DefaultAsset(Integer objectType, Long objectId) {
		super(objectType, objectId);
		this.filename = null;
		this.description = null;
		this.linkId = null; 
		this.enabled = false;
		this.secured = false;
		this.creator = new UserTemplate(-1L);
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
		this.filesize = 0 ;
	}
	
	@JsonIgnore
	public InputStream getInputStream() {
		return inputStream;
	}
	 
	@JsonIgnore
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public User getCreator() {
		return creator;
	}

	
	
	public Integer getFilesize() {
		return filesize;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	@JsonDeserialize(using = JsonUserDeserializer.class)
	public void setCreator(User creator) {
		this.creator = creator;
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
 
}
