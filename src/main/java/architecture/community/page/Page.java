package architecture.community.page;

import java.util.Date;

import architecture.community.model.PropertyModelObjectAware;
import architecture.community.tag.TagDelegator;
import architecture.community.user.User;

public interface Page extends PropertyModelObjectAware {

    public long getPageId();

    public void setPageId(long pageId);

    public String getName();

    public void setName(String name);

    public Integer getVersionId();

    public void setVersionId(Integer versionId);

    public PageState getPageState();

    public String getTitle();

    public void setTitle(String title);

    public String getSummary();

    public void setSummary(String summary);

    public String getBodyText();

    public void setBodyText(String body);
    
    public String getBodyHtml();
    
    public BodyContent getBodyContent();

    public void setBodyContent(BodyContent bodyContent);

    public void setPageState(PageState state);

    public Date getCreationDate();

    public void setCreationDate(Date creationDate);

    public Date getModifiedDate();

    public void setModifiedDate(Date modifiedDate);

    public abstract User getUser();

    public abstract void setUser(User user);

    public abstract int getViewCount();

    public abstract int getCommentCount();

    public abstract TagDelegator getTagDelegator();

    public abstract String getTagsString();
    
    public abstract String getTemplate();
    
    public abstract void setTemplate(String template);
    
    public abstract String getScript();
    
    public abstract void setScript(String script);    
 
	public abstract boolean isSecured() ;
 
	public abstract void setSecured(boolean secured) ;
	 
	public abstract String getPattern() ;
	 
	public abstract void setPattern(String pattern) ; 
	
	
}
