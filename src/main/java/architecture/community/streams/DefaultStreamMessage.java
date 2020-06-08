package architecture.community.streams;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.attachment.AttachmentService;
import architecture.community.model.Models;
import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.User;
import architecture.community.user.model.json.JsonUserDeserializer;
import architecture.community.util.CommunityContextHelper;

public class DefaultStreamMessage extends PropertyModelObjectAwareSupport implements StreamMessage {

	private User user;
	
	private long threadId;
	
	private long messageId;
	
	private long parentMessageId;
		
	private String subject;
	
	private String body;

	private Date creationDate;

	private Date modifiedDate;
	
	private String keywords;
	
	public DefaultStreamMessage() { 
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.messageId = UNKNOWN_OBJECT_ID; 
		this.keywords = null;
	}

	public DefaultStreamMessage(long messageId) {
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.messageId = messageId;
		this.keywords = null; 
	}

	public DefaultStreamMessage(int objectType, long objectId, User user) {
		super(objectType, objectId); 
		this.user = user;
		this.messageId = -1L;
		this.threadId = -1L;
		this.parentMessageId = -1L;
		this.subject = null;
		this.body = null;		
		Date now = new Date();
		this.creationDate = now;
		this.modifiedDate = now;
		this.keywords = null;  
	}
	

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public User getUser() {
		return user;
	}

	@JsonDeserialize(using = JsonUserDeserializer.class)
	public void setUser(User user) {
		this.user = user;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}


	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
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

	public long getParentMessageId() {
		return parentMessageId;
	}

	public void setParentMessageId(long parentMessageId) {
		this.parentMessageId = parentMessageId;
	}
	
	public int getAttachmentsCount() {
		if( threadId > 0 ){
			AttachmentService attachmentService = CommunityContextHelper.getComponent(AttachmentService.class); 
			return attachmentService.getAttachmentCount(Models.STREAMS_MESSAGE.getObjectType(), messageId);
		}
		return 0;		
	}
	
	public int getReplyCount (){
		if( threadId > 0 ){
			try { 
				StreamsService streamsService = CommunityContextHelper.getComponent(StreamsService.class); 
				return streamsService.getTreeWalker(streamsService.getStreamThread(threadId)).getChildCount(this);
			} catch (StreamThreadNotFoundException e) {}
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoardMessage [");
		if (user != null)
			builder.append("user=").append(user).append(", ");
		builder.append("objectType=").append(getObjectType()).append(", objectId=").append(getObjectId()).append(", threadId=").append(threadId).append(", messageId=").append(messageId).append(", parentMessageId=").append(parentMessageId).append(", ");
		if (subject != null)
			builder.append("subject=").append(subject).append(", ");
		if (body != null)
			builder.append("body=").append(body).append(", ");
		if (creationDate != null)
			builder.append("creationDate=").append(creationDate).append(", ");
		if (modifiedDate != null)
			builder.append("modifiedDate=").append(modifiedDate);
		
		builder.append("]");
		return builder.toString();
	}
}
