package architecture.community.tag;

import java.util.List;

import architecture.community.exception.UnAuthorizedException;

public class DefaultTagDelegator implements TagDelegator {

	private int objectType = -1;
	private long objectId = -1L;
	private TagService tagService;

	public DefaultTagDelegator(int objectType, long objectId, TagService tagService) {
		this.objectType = objectType;
		this.objectId = objectId;
		this.tagService = tagService;
	}

	public ContentTag createTag(String tagname) {
		return tagService.createTag(tagname);
	}

	public ContentTag getTag(String tagname) throws TagNotFoundException {
		return tagService.getTag(tagname);
	}

	public ContentTag getTag(long tagId) throws TagNotFoundException {
		return tagService.getTag(tagId);
	}

	public void setTags(String tags) {
		tagService.setTags(tags, objectType, objectId);
	}

	public String getTagsAsString() {
		return tagService.getTagsAsString(objectType, objectId);
	}

	public void addTag(ContentTag tag) throws UnAuthorizedException {
		tagService.addTag(tag, objectType, objectId);
	}

	public List<ContentTag> getTags() {
		return tagService.getTags(objectType, objectId);
	}

	public void removeTag(ContentTag tag) throws UnAuthorizedException {
		tagService.removeTag(tag, objectType, objectId);
	}

	public void removeAllTags() throws UnAuthorizedException {
		tagService.removeAllTags(objectType, objectId);
	}
}