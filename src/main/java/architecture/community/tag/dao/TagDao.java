package architecture.community.tag.dao;

import java.util.List;

import architecture.community.tag.ContentTag;

public interface TagDao {

    public void addTag(long tagId, int objectType, long objectId);

    public void removeTag(long tagId, int objectType, long objectId);

    public int countTags(long tagId);

    public List<Long> getTagIds(int objectType, long objectId);

    public ContentTag getContentTagById(long tagId);

    public ContentTag getContentTagByName(String name);

    public void createContentTag(ContentTag tag);

    public void deleteContentTag(long tagId);

}
