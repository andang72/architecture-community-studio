package architecture.community.user;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;

public class AvatarImage implements Serializable {

	public static final String DEFAULT_THUMBNAIL_CONTENT_TYPE = "image/png";

	private Long avatarImageId;

	private Long userId;

	private Boolean primary;

	private String imageContentType;

	private Integer imageSize;

	private String thumbnailContentType;

	private Integer thumbnailSize;

	private Date creationDate;

	private Date modifiedDate;

	private String filename;

	public AvatarImage(User user) {
		this.userId = user.getUserId();
		this.avatarImageId = -1L;
		this.primary = false;
		this.imageContentType = null;
		this.imageSize = 0;
		this.thumbnailContentType = DEFAULT_THUMBNAIL_CONTENT_TYPE;
		this.thumbnailSize = 0;
		Date now = Calendar.getInstance().getTime();
		this.creationDate = now;
		this.modifiedDate = now;
	}

	/**
	 * 
	 */
	public AvatarImage() {
		this.avatarImageId = -1L;
		this.userId = -1L;
		this.primary = false;
		this.imageContentType = null;
		this.imageSize = 0;
		this.thumbnailContentType = DEFAULT_THUMBNAIL_CONTENT_TYPE;
		this.thumbnailSize = 0;
		Date now = Calendar.getInstance().getTime();
		this.creationDate = now;
		this.modifiedDate = now;
	}

	/**
	 * @return primary
	 */
	public Boolean isPrimary() {
		return primary;
	}

	/**
	 * @param primary
	 *            설정할 primary
	 */
	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	/**
	 * @return profileImageId
	 */
	public Long getAvatarImageId() {
		return avatarImageId;
	}

	/**
	 * @param profileImageId
	 *            설정할 profileImageId
	 */
	public void setAvatarImageId(Long avatarImageId) {
		this.avatarImageId = avatarImageId;
	}

	/**
	 * @return userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            설정할 userId
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return imageContentType
	 */
	public String getImageContentType() {
		return imageContentType;
	}

	/**
	 * @param imageContentType
	 *            설정할 imageContentType
	 */
	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	/**
	 * @return imageSize
	 */
	public Integer getImageSize() {
		return imageSize;
	}

	/**
	 * @param imageSize
	 *            설정할 imageSize
	 */
	public void setImageSize(Integer imageSize) {
		this.imageSize = imageSize;
	}

	/**
	 * @return thumbnailContentType
	 */
	public String getThumbnailContentType() {
		return thumbnailContentType;
	}

	/**
	 * @param thumbnailContentType
	 *            설정할 thumbnailContentType
	 */
	public void setThumbnailContentType(String thumbnailContentType) {
		this.thumbnailContentType = thumbnailContentType;
	}

	/**
	 * @return creationDate
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            설정할 creationDate
	 */
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return modifiedDate
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            설정할 modifiedDate
	 */
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return thumbnailSize
	 */
	public Integer getThumbnailSize() {
		return thumbnailSize;
	}

	/**
	 * @param thumbnailSize
	 *            설정할 thumbnailSize
	 */
	public void setThumbnailSize(Integer thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}

	/**
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            설정할 filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

}
