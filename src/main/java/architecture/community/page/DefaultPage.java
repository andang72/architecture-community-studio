package architecture.community.page;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.comment.CommentService;
import architecture.community.model.Models;
import architecture.community.model.PropertyModelObjectAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.page.model.json.BodyContentDeserializer;
import architecture.community.page.model.json.PageStateDeserializer;
import architecture.community.tag.DefaultTagDelegator;
import architecture.community.tag.TagDelegator;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.community.user.model.json.JsonUserDeserializer;
import architecture.community.util.CommunityContextHelper;
import architecture.community.viewcount.ViewCountService;
import architecture.ee.exception.ComponentNotFoundException;
import architecture.ee.util.StringUtils;

public class DefaultPage extends PropertyModelObjectAwareSupport implements Page {

	private long pageId;
	private String name;
	private Integer versionId;
	private PageState pageState;
	private String title;
	private String summary;
	private String template;
	private String pattern;
	private String script;
	private BodyContent bodyContent;
	private Date creationDate;
	private Date modifiedDate;
	private User user;
	private boolean secured;
	private String contentType;
	private String bodyHtml;
	
	public DefaultPage() {
		super(-1, -1L);
		this.name = null;
		this.pageId = -1L;
		this.versionId = -1;
		this.pageState = PageState.INCOMPLETE;
		this.user = new UserTemplate(-1L);
		this.title = "";
		this.template = "";
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
		this.secured = false;
		this.pattern = null;
		this.script = null;
		this.contentType = null;
		this.bodyHtml = null;
	}

	public DefaultPage(Long pageId) {
		super(-1, -1L);
		this.pageId = pageId;
		this.name = null;
		this.versionId = -1;
		this.pageState = PageState.INCOMPLETE;
		this.user = new UserTemplate(-1L);
		this.title = "";
		this.template = "";
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
		this.secured = false;
		this.pattern = null;
		this.script = null;
		this.contentType = null;
		this.bodyHtml = null;
	}

	public DefaultPage(int objectType, long objectId) {
		super(objectType, objectId);
		this.name = null;
		this.pageId = -1L;
		this.versionId = -1;
		this.pageState = PageState.INCOMPLETE;
		this.user = new UserTemplate(-1L);
		this.title = "";
		this.template = "";
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
		this.secured = false;
		this.pattern = null;
		this.script = null;
		this.contentType = null;
		this.bodyHtml = null;
	}

 
	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	/**
	 * @return pageId
	 */
	public long getPageId() {
		return pageId;
	}

	/**
	 * @param pageId
	 *            설정할 pageId
	 */
	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            설정할 name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return versionId
	 */
	public Integer getVersionId() {
		return versionId;
	}

	/**
	 * @param versionId
	 *            설정할 versionId
	 */
	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	/**
	 * @return pageState
	 */
	public PageState getPageState() {
		return pageState;
	}

	/**
	 * @param pageState
	 *            설정할 pageState
	 */

	@JsonDeserialize(using = PageStateDeserializer.class)
	public void setPageState(PageState pageState) {
		this.pageState = pageState;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            설정할 title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            설정할 summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return bodyContent
	 */
	public BodyContent getBodyContent() {
		return bodyContent;
	}

	/**
	 * @param bodyContent
	 *            설정할 bodyContent
	 */
	@JsonDeserialize(using = BodyContentDeserializer.class)
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent = bodyContent;
	}

	@JsonProperty
	public int getViewCount() {
		if( pageId < 1)
			return -1;
		if( CommunityContextHelper.isAvailable(ViewCountService.class))
			return CommunityContextHelper.getViewCountServive().getViewCount(this);		
		return 0 ;
	}

	@JsonIgnore
	public void setViewCount(int viewCount) {

	}

	@JsonIgnore
	public void setCommentCount(int commentCount) {

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

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	@JsonIgnore
	public String getBodyText() {
		if (bodyContent == null)
			return null;
		else
			return bodyContent.getBodyText();
	}

	@JsonIgnore
	public void setBodyText(String body) {
		bodyContent.setBodyText(body);
	}

	@JsonProperty
	@Override
	public int getCommentCount() {
		try {
			if( CommunityContextHelper.isAvailable(CommentService.class))
				return CommunityContextHelper.getCommentService().getCommentTreeWalker(Models.PAGE.getObjectType(), pageId).getRecursiveChildCount(-1L);			
		} catch (ComponentNotFoundException e) {
			return 0;
		}
		return 0;
	}

	@JsonIgnore
	public TagDelegator getTagDelegator() {
		if (this.getPageId() == -1L)
			throw new IllegalStateException("Cannot retrieve tag manager prior to document being saved.");
		else { 
			if( CommunityContextHelper.isAvailable(TagDelegator.class ) )
				return new DefaultTagDelegator(Models.PAGE.getObjectType(), this.getPageId(), CommunityContextHelper.getComponent(TagService.class) );		
			throw new ComponentNotFoundException("tag manager not found.");
		}
	}

	@JsonProperty
	public String getTemplate() {
		return template;
	}

	@JsonProperty
	public void setTemplate(String template) {
		this.template = template;
	}

	@JsonIgnore
	public void setTagsString(String tagsString) {
	}

	
	@JsonProperty
	public String getTagsString() {
		if (this.getPageId() > 0 && CommunityContextHelper.isAvailable(TagDelegator.class ) ){
			return getTagDelegator().getTagsAsString();
		}
		return null;
	}

	@JsonProperty
	public boolean isSecured() {
		return secured;
	}

	@JsonProperty
	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	@JsonProperty
	public String getPattern() {
		return pattern;
	}
	
	@JsonProperty
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultPage [pageId=").append(pageId).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (versionId != null)
			builder.append("versionId=").append(versionId).append(", ");
		if (pageState != null)
			builder.append("pageState=").append(pageState).append(", ");
		if (title != null)
			builder.append("title=").append(title).append(", ");
		if (summary != null)
			builder.append("summary=").append(summary).append(", ");
		if (template != null)
			builder.append("template=").append(template).append(", ");
		if (bodyContent != null)
			builder.append("bodyContent=").append(bodyContent).append(", ");
		if (creationDate != null)
			builder.append("creationDate=").append(creationDate).append(", ");
		if (modifiedDate != null)
			builder.append("modifiedDate=").append(modifiedDate).append(", ");
		if (user != null)
			builder.append("user=").append(user).append(", ");
		builder.append("secured=").append(secured).append("]");
		return builder.toString();
	}
 
	public void setBodyHtml(String bodyHtml) {
		this.bodyHtml = bodyHtml;
	}

	public String getBodyHtml() {
		if( StringUtils.isNullOrEmpty( this.bodyHtml ) )
			return this.getBodyText(); 
		return bodyHtml;
	}  
} 