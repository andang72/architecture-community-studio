package architecture.community.tag;

import java.util.List;
import java.util.Map;

import architecture.community.category.Category;
import architecture.community.exception.UnAuthorizedException;

public interface TagService {
	
	public abstract TagDelegator getTagDelegator(Category category);

    public abstract ContentTag createTag(String name);

    public abstract ContentTag getTag(String name) throws TagNotFoundException;

    public abstract ContentTag getTag(long tagId) throws TagNotFoundException;

    public abstract void addTag(ContentTag contenttag, int objectType, long objectId) throws UnAuthorizedException;

    public abstract void setTags(String name, int objectType, long objectId);

    public abstract List<ContentTag> getTags(int objectType, long objectId);

    public abstract String getTagsAsString(int objectType, long objectId);

    public abstract int getTagCount(int objectType, long objectId);

    public abstract Map getTagMap(int objectType);

    public abstract void removeTag(ContentTag contenttag, int objectType, long objectId) throws UnAuthorizedException;

    public abstract void removeAllTags(int objectType, long objectId) throws UnAuthorizedException;

}
