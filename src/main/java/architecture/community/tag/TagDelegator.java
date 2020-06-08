package architecture.community.tag;

import java.util.List;

import architecture.community.exception.UnAuthorizedException;

public interface TagDelegator {
	
    public ContentTag createTag(String tagname);

    public ContentTag getTag(String tagname) throws TagNotFoundException;

    public ContentTag getTag(long tagId) throws TagNotFoundException;

    public void setTags(String tags);

    public String getTagsAsString();

    public void addTag(ContentTag tag) throws UnAuthorizedException;

    public List<ContentTag> getTags();

    public void removeTag(ContentTag tag) throws UnAuthorizedException;

    public void removeAllTags() throws UnAuthorizedException;

}