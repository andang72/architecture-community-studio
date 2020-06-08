package architecture.community.streams;

import java.util.Date;

import architecture.community.model.PropertyModelObjectAware;

public interface Streams extends PropertyModelObjectAware {

	public abstract long getCategoryId();
	
	public abstract long getStreamId();
	
	public abstract String getName();
	
	public abstract String getDisplayName();
	
	public abstract String getDescription();
	
	public abstract Date getCreationDate();

	public abstract void setCreationDate(Date creationDate);
	
	public abstract Date getModifiedDate();
	
	public abstract void setModifiedDate(Date modifiedDate);
	
}
