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

package architecture.community.attachment;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.share.SharedLink;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;

public class DefaultAttachment extends PropertyModelObjectAwareSupport implements Attachment {

	private User user ;
	
    private long attachmentId = UNKNOWN_OBJECT_ID;
	
    private String name;
	
	private String contentType ; 
	
	private int size = 0 ;
	
	private int downloadCount = 0;
	
	private Map<String, String> properties ;
	
	@JsonIgnore
	private InputStream inputStream;
	
	private String extrnalLink;
	
	private Date creationDate;
	
	private Date modifiedDate;
	
	private SharedLink sharedLink ;
	
	private String tags;
	
	public DefaultAttachment() {
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.name = null;
		this.inputStream = null;
		this.user = new UserTemplate(-1L);
		this.properties = new HashMap<String, String>(); 
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = this.creationDate;
		this.sharedLink = null;
		this.tags = null;
	}
	
	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;  
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
 
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	 @JsonIgnore
	public InputStream getInputStream() {
		return inputStream;
	}
	 
	 @JsonIgnore
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
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

	public String getExtrnalLink() {
		if( extrnalLink == null &&  sharedLink != null )
			return sharedLink.getLinkId();
		return extrnalLink;
	}

	public void setExtrnalLink(String extrnalLink) {
		this.extrnalLink = extrnalLink;
	}

	public SharedLink getSharedLink() {
		return sharedLink;
	}

	public void setSharedLink(SharedLink sharedLink) {
		this.sharedLink = sharedLink;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Attachement{");
	    sb.append("attachmentId=").append(attachmentId);
	    sb.append(",name=").append(getName());
	    sb.append(",contentType=").append(contentType);
	    sb.append("size=").append(size);
		sb.append("}");
		return sb.toString();
	}
}
