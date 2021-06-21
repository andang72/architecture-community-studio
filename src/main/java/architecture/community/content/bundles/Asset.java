package architecture.community.content.bundles;

import java.io.InputStream;
import java.util.Date;

import architecture.community.model.PropertyModelObjectAware;
import architecture.community.user.User;

public interface Asset extends PropertyModelObjectAware {

	public abstract Long getAssetId();
	
	public abstract String getLinkId();
	
	public abstract String getFilename();
	
	public abstract Integer getFilesize();
	
	public abstract void setFilesize(int filesize);
	
	public abstract String getDescription();
	
	public abstract User getCreator();
	
	public abstract void setCreator(User user );
	
	public abstract boolean isEnabled();
	 
	public abstract boolean isSecured();
	
	public abstract InputStream getInputStream();
	
    public abstract Date getCreationDate();

    public abstract void setCreationDate(Date creationDate);

    public abstract Date getModifiedDate();

    public abstract void setModifiedDate(Date modifiedDate);
    
}
