package architecture.community.streams;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.attachment.AttachmentService;
import architecture.community.comment.CommentService;
import architecture.community.exception.NotFoundException;
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
	
	private String tags;

	private Integer attachmentsCount ;
	
	public DefaultStreamMessage() { 
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.messageId = UNKNOWN_OBJECT_ID; 
		this.keywords = null;
		this.tags = null;
		this.attachmentsCount = null;
	}

	public DefaultStreamMessage(long messageId) {
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.messageId = messageId;
		this.keywords = null;
		this.tags = null;
		this.attachmentsCount = null;
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
		this.tags = null;
		this.attachmentsCount = null;
	}
	

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
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

	@JsonIgnore
	protected void setAttachmentsCount( Integer count ){
		this.attachmentsCount = count;
	}

	@JsonGetter
	public int getAttachmentsCount() {
		if( threadId > 0  && attachmentsCount == null ){
			AttachmentService attachmentService = CommunityContextHelper.getComponent(AttachmentService.class); 
			return attachmentService.getAttachmentCount(Models.STREAMS_MESSAGE.getObjectType(), messageId);
		}
		if( attachmentsCount == null)
			return 0;
		return attachmentsCount;		
	}
	
	public int getReplyCount (){
		if( threadId > 0 ){
			try { 
				StreamsService streamsService = CommunityContextHelper.getComponent(StreamsService.class); 
				return streamsService.getTreeWalker(streamsService.getStreamThread(threadId)).getChildCount(this);
			} catch (NotFoundException e) {}
		}
		return 0;
	}

	public int getCommentCount (){
		if( threadId > 0 ){
			try { 
				CommentService commentService = CommunityContextHelper.getComponent(CommentService.class); 
				return commentService.getCommentTreeWalker(Models.STREAMS_MESSAGE.getObjectType(), messageId).getRecursiveChildCount(messageId);
			} catch (Exception e) {}
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
