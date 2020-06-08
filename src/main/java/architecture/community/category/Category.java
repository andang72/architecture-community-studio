package architecture.community.category;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.ModelObject;
import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;

public class Category extends PropertyModelObjectAwareSupport implements Serializable , ModelObject {
    

    private Long categoryId;
	private String name;
	private String displayName;
	private String description;
	private Date creationDate;
	private Date modifiedDate;
	
	public Category() { 
		
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		
		this.categoryId = -1L;
		this.name = null;
		this.displayName = null;
		this.description = null;
		Date now = new Date();
		this.creationDate = now;
		this.modifiedDate = creationDate;
		
	}

	public Category(Long categoryId) {
		
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		
		this.categoryId = categoryId;
		this.name = null;
		this.displayName = null;
		this.description = null;
		Date now = new Date();
		this.creationDate = now;
		this.modifiedDate = creationDate;
	}	
	
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
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