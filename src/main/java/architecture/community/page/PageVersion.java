package architecture.community.page;

import java.util.Date;

import architecture.community.user.User;

public interface PageVersion {

    public int getVersionNumber();

    public void setVersionNumber(int versionNumber);

    public Page getPage();

    public User getAuthor();

    public void setAuthor(User author);

    public PageState getPageState();

    public void setPageState(PageState state);

    public Date getCreationDate();

    public void setCreationDate(Date creationDate);

    public Date getModifiedDate();

    public void setModifiedDate(Date modifiedDate);

}