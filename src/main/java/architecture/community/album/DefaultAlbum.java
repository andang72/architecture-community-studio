package architecture.community.album;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.image.Image;
import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.User;
import architecture.community.user.model.json.JsonUserDeserializer;
import architecture.community.util.SecurityHelper;

public class DefaultAlbum extends PropertyModelObjectAwareSupport  implements Album  {

	private Long albumId;
	
	private String name;
	
	private String description; 
			
	private Date creationDate;
	
	private Date modifiedDate;
	
	private User user ;
	
	private Image coverImage ;
	

	public DefaultAlbum() {
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.user = SecurityHelper.ANONYMOUS;
		this.albumId = UNKNOWN_OBJECT_ID; 
		this.name = null;
	}
	
	public DefaultAlbum(Long albumId) {
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.albumId = albumId;
		this.name = null;
	}

	public Long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(Long albumId) {
		this.albumId = albumId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public User getUser() {
		return user;
	}

	@JsonDeserialize(using = JsonUserDeserializer.class)
	public void setUser(User user) {
		this.user = user;
	}
 
	public void setCoverImage(Image coverImage) {
		this.coverImage = coverImage;
	}

	public Image getCoverImage() { 
		return coverImage;
	}


}
