/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.User;
import architecture.community.user.model.json.JsonUserDeserializer;
import architecture.community.util.SecurityHelper;

public class DefaultImage extends PropertyModelObjectAwareSupport implements Image {
	
	private Long imageId;
	
	private String name;
	 
	private Integer size;
	
	private String contentType;
	
	private InputStream inputStream;
	
	private Integer thumbnailSize ;
	
	private String thumbnailContentType ;
	
	private User user;
	
	private ImageLink imageLink;
	
	private Date creationDate;
	
	private Date modifiedDate;
	
	private String tags;
	
	public DefaultImage() {
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.user = SecurityHelper.ANONYMOUS;
		this.imageId = UNKNOWN_OBJECT_ID;
		this.thumbnailContentType = DEFAULT_THUMBNAIL_CONTENT_TYPE;
		this.size = 0;
		this.thumbnailSize = 0 ;
		this.imageLink = null;
		this.tags = null;
	}

	public DefaultImage(Integer objectType, Long objectId) {
		super(objectType, objectId);
		this.imageId = UNKNOWN_OBJECT_ID; 
		this.thumbnailContentType = DEFAULT_THUMBNAIL_CONTENT_TYPE;
		this.size = 0;
		this.thumbnailSize = 0 ;
		this.user = SecurityHelper.ANONYMOUS;
		this.imageLink = null;
		this.tags = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public int getSize() {
		return size ;
	}

	@JsonIgnore
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}
 
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public Integer getThumbnailSize() {
		return thumbnailSize;
	}

	public void setThumbnailSize(Integer thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}

	public String getThumbnailContentType() {
		return thumbnailContentType;
	}

	public void setThumbnailContentType(String thumbnailContentType) {
		this.thumbnailContentType = thumbnailContentType;
	}

	public User getUser() {
		return user;
	}

	 @JsonDeserialize(using = JsonUserDeserializer.class)
	public void setUser(User user) {
		this.user = user;
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
	
	@JsonProperty
	public ImageLink getImageLink() {
		return imageLink;
	}

	@JsonIgnore
	public void setImageLink(ImageLink imageLink) {
		this.imageLink = imageLink;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Image{");
	    sb.append("imageId=").append(imageId);
	    sb.append(",name=").append(getName());
	    sb.append(",contentType=").append(contentType);
	    sb.append("size=").append(size);
		sb.append("}");
		return sb.toString();
	}
	
}
