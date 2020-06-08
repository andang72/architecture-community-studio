package architecture.community.streams;

import java.util.Date;

import architecture.community.model.PropertyModelObjectAware;
 

public interface StreamThread extends PropertyModelObjectAware  {

	public long getThreadId() ;

	public Date getCreationDate();

	public void setCreationDate(Date creationDate);

	public Date getModifiedDate() ;

	public void setModifiedDate(Date modifiedDate);

	public StreamMessage getLatestMessage();

	public void setLatestMessage(StreamMessage latestMessage);

	public StreamMessage getRootMessage() ;

	public void setRootMessage(StreamMessage rootMessage) ;	
	
}
