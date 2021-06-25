package architecture.community.album;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.json.JsonAttachmentDeserializer;
import architecture.community.image.Image;
import architecture.community.image.json.JsonImageDeserializer;
import architecture.community.model.Models;

public class AlbumContents implements Serializable {

	private Long albumId;

	private Integer order;
	
	private Integer contentType;
	
	private Long contentId;
	
	private Image image ;
	
	private Attachment attachment;
	
	public AlbumContents() {
		
	}
	
	public String getName () {
		
		if(attachment!=null)
			return attachment.getName();
		else 
			return image.getName();
	}

	public String getAlbumContentsKey () {
		StringBuilder sb = new StringBuilder();
		sb.append(albumId);
		sb.append("-");
		sb.append(contentType);
		sb.append("-");
		sb.append(contentId);
		return sb.toString();
	}
	
	public AlbumContents(Long albumId, Attachment attachment, Integer order) {
		this.albumId = albumId;
		this.contentType = Models.ATTACHMENT.getObjectType();
		this.contentId = attachment.getAttachmentId();
		this.attachment = attachment;
		this.order = order;
	} 
	
	public AlbumContents(Long albumId, Image image, Integer order) {
		this.albumId = albumId;
		this.contentType = Models.IMAGE.getObjectType();
		this.contentId = image.getImageId();
		this.image = image;
		this.order = order;
	} 
	
	public AlbumContents(Long albumId, Models models, Long contentId, Integer order) {
		super();
		this.albumId = albumId;
		this.contentType = models.getObjectType();
		this.contentId = contentId;
		this.order = order;
	}

	
	public AlbumContents(Long albumId, Integer contentType, Long contentId, Integer order) {
		super();
		this.albumId = albumId;
		this.contentType = contentType;
		this.contentId = contentId;
		this.order = order;
	}

	public Long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(Long albumId) {
		this.albumId = albumId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getContentType() {
		return contentType;
	}

	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public boolean isImage () {
		if( contentType == Models.IMAGE.getObjectType())
			return true;
		return false;
	}
	
	public boolean isAttachment() {
		if( contentType == Models.ATTACHMENT.getObjectType())
			return true;
		return false;
	}

	@JsonProperty
	public Image getImage() {
		return image;
	}
 
	@JsonDeserialize(using = JsonImageDeserializer.class)
	public void setImage(Image image) {
		this.image = image;
	}
	
	@JsonProperty
	public Attachment getAttachment() {
		return attachment;
	}
 
	@JsonDeserialize(using = JsonAttachmentDeserializer.class)
	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	
	
	
}
