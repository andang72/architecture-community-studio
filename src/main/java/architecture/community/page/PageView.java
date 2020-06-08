package architecture.community.page;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.json.JsonDateSerializer;
import architecture.community.tag.TagDelegator;
import architecture.community.user.User;

public class PageView implements Page {

	public static Page build(Page page, boolean includeBodyContent) {
		return new PageView(page, includeBodyContent);
	}
	
	private Page page ;
	private boolean includeBodyContent;
	
	public PageView(Page page) {
		this.page = page;
		this.includeBodyContent = false;
	}
	
	public PageView(Page page, boolean includeBodyContent) {
		this.page = page;
		this.includeBodyContent = includeBodyContent;
	}	

	@JsonProperty
	public int getObjectType() { 
		return page.getObjectType();
	}

	@JsonProperty
	public long getObjectId() { 
		return page.getObjectId();
	}

	@JsonProperty
	public Map<String, String> getProperties() { 
		return page.getProperties();
	}

	@JsonIgnore
	public void setProperties(Map<String, String> properties) { 
		
	}

	@JsonProperty
	public long getPageId() { 
		return page.getPageId();
	}

	@JsonIgnore
	public void setPageId(long pageId) { 
		
	}

	@JsonProperty
	public String getName() { 
		return page.getName();
	}

	@JsonIgnore
	public void setName(String name) { 
		
	}

	@JsonProperty
	public Integer getVersionId() { 
		return page.getVersionId();
	}

	@JsonIgnore
	public void setVersionId(Integer versionId) { 
		
	}

	@JsonProperty
	public PageState getPageState() { 
		return page.getPageState();
	}

	@JsonProperty
	public String getTitle() { 
		return page.getTitle();
	}

	@Override
	public void setTitle(String title) { 
		
	}

	@JsonProperty
	public String getSummary() { 
		return page.getSummary();
	}

	@JsonIgnore
	public void setSummary(String summary) { 
		
	}

	@JsonIgnore
	public String getBodyText() { 
		return page.getBodyText();
	}

	@JsonIgnore
	public void setBodyText(String body) {  
	}

	@JsonProperty
	public BodyContent getBodyContent() { 
		if(includeBodyContent)
			return page.getBodyContent();
		else 
			return null;
	}

	@JsonIgnore
	public void setBodyContent(BodyContent bodyContent) { 
		
	}

	@JsonIgnore
	public void setPageState(PageState state) { 
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreationDate() { 
		return page.getCreationDate();
	}

	@JsonIgnore
	public void setCreationDate(Date creationDate) { 
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getModifiedDate() { 
		return page.getModifiedDate();
	}

	@JsonIgnore
	public void setModifiedDate(Date modifiedDate) { 
		
	}

	@JsonProperty
	public User getUser() { 
		return page.getUser();
	}

	@JsonIgnore
	public void setUser(User user) { 
		
	}

	@JsonProperty
	public int getViewCount() { 
		return 0;
	}

	@JsonProperty
	public int getCommentCount() { 
		return page.getCommentCount();
	}

	@JsonIgnore
	public TagDelegator getTagDelegator() { 
		return null;
	}

	@JsonProperty
	public String getTagsString() { 
		return page.getTagsString();
	}

	@JsonProperty
	public String getTemplate() {
		return page.getTemplate();		
	}

	@JsonProperty
	public void setTemplate(String template) {
		page.setTemplate(template);
	}

	@JsonProperty
	public boolean isSecured() {
		return page.isSecured();
	}

	@JsonProperty
	public void setSecured(boolean secured) {
		page.setSecured(secured);		
	}

	@JsonProperty
	public String getPattern() {
		return page.getPattern();
	}

	@Override
	public void setPattern(String pattern) {
		page.setPattern( pattern );
	}

	@JsonProperty
	public String getScript() {
		return page.getScript();
	}
 
	public void setScript(String script) {
		page.setScript(script);
	}
 
	public String getBodyHtml() { 
		return page.getBodyHtml();
	}

}