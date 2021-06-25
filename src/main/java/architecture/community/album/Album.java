package architecture.community.album;

import java.util.Date;

import architecture.community.image.Image;
import architecture.community.model.PropertyModelObjectAware;
import architecture.community.user.User;

public interface Album extends PropertyModelObjectAware {

	public abstract Long getAlbumId();
	
	public abstract String getName(); 
	
	public abstract String getDescription(); 

    public abstract User getUser();

    public abstract void setUser(User user);
    
	public abstract Date getCreationDate();

	public abstract void setCreationDate(Date creationDate);
	
	public abstract Date getModifiedDate();
	
	public abstract void setModifiedDate(Date modifiedDate);
	
	
	public abstract Image getCoverImage();

}
