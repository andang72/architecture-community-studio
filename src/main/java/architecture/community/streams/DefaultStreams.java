package architecture.community.streams;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;

public class DefaultStreams extends PropertyModelObjectAwareSupport implements Streams {

	private long categoryId;
	
	private long streamId;
	
	private String name;
	
	private String displayName;
			
	private String description;
	
	private Date creationDate;

	private Date modifiedDate;

	
	public DefaultStreams() {
		super(-1, -1L);
		this.categoryId = 0;
		this.streamId = -1L;
		this.creationDate = new Date();
		this.modifiedDate = creationDate;
	}
	
	public DefaultStreams(long streamId) {
		super(-1, -1L);
		this.categoryId = 0;
		this.streamId = streamId;
		this.creationDate = new Date();
		this.modifiedDate = creationDate;		
	}
	
	public DefaultStreams(int objectType, long objectId) {
		super(objectType, objectId);
		this.categoryId = 0;
		this.streamId = -1L;
		this.creationDate = new Date();
		this.modifiedDate = creationDate;		
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public long getStreamId() {
		return streamId;
	}

	public void setStreamId(long streamId) {
		this.streamId = streamId;
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
