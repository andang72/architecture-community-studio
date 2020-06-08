package architecture.community.announce;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.attachment.Attachment;
import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.User;
import architecture.community.user.model.json.JsonUserDeserializer;

public class Announce extends PropertyModelObjectAwareSupport implements Serializable {

	private Long announceId;
    private String subject;
    private String body;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date endDate;
    
    private User user;
    
    private List<Attachment> attachments;

    private String firstImageSrc;
    private int imageCount = 0;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date creationDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private Date modifiedDate;
	
	public Announce() {  
		
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID);
		this.announceId = UNKNOWN_OBJECT_ID;
		this.subject = null;
		this.attachments = new ArrayList<Attachment>();
		Date now = new Date();
		this.startDate = now;
		this.endDate = now;
		this.firstImageSrc = null;
		this.creationDate = now;
		this.modifiedDate = creationDate;
		
	}

	public Announce(Long announceId) { 
		
		super(UNKNOWN_OBJECT_TYPE, UNKNOWN_OBJECT_ID); 
		this.announceId = -1L;
		this.subject = null;
		this.attachments = new ArrayList<Attachment>();
		Date now = new Date();
		this.startDate = now;
		this.endDate = now;
		this.firstImageSrc = null;
		this.creationDate = now;
		this.modifiedDate = creationDate;
		this.announceId = announceId;
	}
	
	public Announce(Long announceId, int objectType, Long objectId, User user) { 
		
		super(objectType, objectId); 
		
		this.announceId = -1L;
		this.subject = null;
		this.attachments = new ArrayList<Attachment>();
		Date now = new Date();
		this.startDate = now;
		this.endDate = now;
		this.firstImageSrc = null;
		this.creationDate = now;
		this.modifiedDate = creationDate;
		this.announceId = announceId; 
		this.user = user;
	}

    /**
     * @return announceId
     */
    public Long getAnnounceId() {
    		return announceId;
    }

    /**
     * @param announceId
     *            설정할 announceId
     */
    public void setAnnounceId(Long announceId) {
    		this.announceId = announceId;
    } 
    
	/**
     * @return subject
     */
    public String getSubject() {
    		return subject;
    }

    /**
     * @return attachments
     */
    public List getAttachments() {
    		return attachments;
    }

    /**
     * @param attachments
     *            설정할 attachments
     */
    public void setAttachments(List attachments) {
    		this.attachments = attachments;
    }

    /**
     * @param subject
     *            설정할 subject
     */
    public void setSubject(String subject) {
    		this.subject = subject;
    }

    /**
     * @return body
     */
    public String getBody() {
    		return body;
    }

    /**
     * @param body
     *            설정할 body
     */
    public void setBody(String body) {
		this.body = body; 
		Document doc = Jsoup.parse(this.body);
		Elements links = doc.select("img");
		this.imageCount = links.size();
		if (imageCount > 0)
		    firstImageSrc = links.first().attr("src"); 
    }

    public String getFirstImageSrc() {
	return this.firstImageSrc;
    }

    /**
     * @return imageCount
     */
    public int getImageCount() {
    		return imageCount;
    }

    /**
     * @return startDate
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getStartDate() {
    		return startDate;
    }

    /**
     * @param startDate
     *            설정할 startDate
     */

    @JsonDeserialize(using = JsonDateDeserializer.class)
    public void setStartDate(Date startDate) {
		if (startDate == null)
		    throw new NullPointerException("Start data cannot be null.");
	
		if (this.startDate != null && this.startDate.getTime() == startDate.getTime())
		    return;
	
		if (endDate != null && startDate.getTime() > endDate.getTime()) {
		    throw new IllegalArgumentException();
		} else {
		    this.startDate = startDate;
		    return;
		}
    }

    /**
     * @return endDate
     */

    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getEndDate() {
    		return endDate;
    }

    /**
     * @param endDate
     *            설정할 endDate
     */
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public void setEndDate(Date endDate) {
		if (this.endDate != null && endDate != null && this.endDate.getTime() == endDate.getTime())
		    return;
	
		if (endDate != null && endDate.getTime() < this.startDate.getTime())
		    throw new IllegalArgumentException();
	
		if (endDate != null)
		    this.endDate = endDate;
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

	/**
     * @return user
     */
    public User getUser() {
    		return user;
    }

    /**
     * @param user
     *            설정할 user
     */

    @JsonDeserialize(using = JsonUserDeserializer.class)
    public void setUser(User user) {
    		this.user = user;
    }

    public void deleteAttachments(Attachment attachment) {
    		this.attachments.remove(attachment);
    }

    public int attachmentCount() {
    		return attachments.size();
    }

    /*
     * (비Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
	@JsonIgnore
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Announce [");
		if (announceId != null) {
			builder.append("announceId=");
			builder.append(announceId);
			builder.append(", ");
		}
 
		if (subject != null) {
			builder.append("subject=");
			builder.append(subject);
			builder.append(", ");
		}
		if (body != null) {
			builder.append("body=");
			builder.append(body);
			builder.append(", ");
		}
		if (startDate != null) {
			builder.append("startDate=");
			builder.append(startDate);
			builder.append(", ");
		}
		if (endDate != null) {
			builder.append("endDate=");
			builder.append(endDate);
		}
		builder.append("]");
		return builder.toString();
	}
}
