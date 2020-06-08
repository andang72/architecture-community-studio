package architecture.community.streams;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.Models;
import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.streams.model.json.JsonStreamMessageDeserializer;
import architecture.community.util.CommunityContextHelper;

public class DefaultStreamThread extends PropertyModelObjectAwareSupport implements StreamThread {

	private long threadId;

	private Date creationDate;

	private Date modifiedDate;

	private StreamMessage latestMessage;

	private StreamMessage rootMessage;

	private AtomicInteger messageCount = new AtomicInteger(-1);

	public DefaultStreamThread() {
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.threadId = UNKNOWN_OBJECT_ID;
		this.rootMessage = null;
		this.latestMessage = null;
		this.messageCount = new AtomicInteger(-1);
	}

	public DefaultStreamThread(long threadId) {
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.threadId = threadId;
		this.rootMessage = null;
		this.latestMessage = null;
		this.messageCount = new AtomicInteger(-1);
	}

	public DefaultStreamThread(int objectType, long objectId, StreamMessage rootMessage) {
		super(objectType, objectId);
		this.threadId = -1L;
		this.rootMessage = rootMessage;
		this.latestMessage = null;
		boolean isNew = rootMessage.getThreadId() < 1L;
		if (isNew) {
			this.creationDate = rootMessage.getCreationDate();
			this.modifiedDate = this.creationDate;
		}
		this.messageCount = new AtomicInteger(-1);
	}

	public void setMessageCount(int messageCount) {
		this.messageCount.set(messageCount);
	}

	public int getViewCount() {
		if (threadId < 1)
			return -1;

		return CommunityContextHelper.getViewCountServive().getViewCount(Models.STREAMS_THREAD.getObjectType(),
				threadId);
	}

	public int getMessageCount() {
		int count = messageCount.get();
		if (count <= 0)
			return 1;
		else
			return count;
	}

	public void incrementMessageCount() {
		if (messageCount.get() < 0)
			messageCount.set(1);
		else
			messageCount.incrementAndGet();
	}

	public void decrementMessageCount() {
		if (messageCount.get() > 0)
			messageCount.decrementAndGet();
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
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

	public StreamMessage getLatestMessage() {
		return latestMessage;
	}

	@JsonDeserialize(using = JsonStreamMessageDeserializer.class)
	public void setLatestMessage(StreamMessage latestMessage) {
		this.latestMessage = latestMessage;
	}

	public StreamMessage getRootMessage() {
		return rootMessage;
	}

	@JsonDeserialize(using = JsonStreamMessageDeserializer.class)
	public void setRootMessage(StreamMessage rootMessage) {
		this.rootMessage = rootMessage;
	}

	private String coverImgSrc = null;
	
	public String getCoverImgSrc() {
		if(this.coverImgSrc == null && this.rootMessage != null && StringUtils.isNotEmpty(rootMessage.getBody()))
		{
			Document doc = Jsoup.parse(this.rootMessage.getBody());
			Elements eles = doc.select("img");
			String src = null;
			for( Element ele : eles ) { 				
				coverImgSrc = ele.attr("src");
				break;
			} 
		}
		return coverImgSrc;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoardThread [threadId=").append(threadId).append(", objectType=").append(getObjectType())
				.append(", objectId=").append(getObjectId()).append(", ");
		if (creationDate != null)
			builder.append("creationDate=").append(creationDate).append(", ");
		if (modifiedDate != null)
			builder.append("modifiedDate=").append(modifiedDate).append(", ");
		if (latestMessage != null)
			builder.append("latestMessage=").append(latestMessage).append(", ");
		if (rootMessage != null)
			builder.append("rootMessage=").append(rootMessage).append(", ");
		if (messageCount != null)
			builder.append("messageCount=").append(messageCount);
		builder.append("]");
		return builder.toString();
	}
}
