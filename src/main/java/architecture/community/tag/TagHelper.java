package architecture.community.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.community.exception.UnAuthorizedException;

public class TagHelper {

	private Logger log = LoggerFactory.getLogger(getClass());

	private final TagService tagService;

	public TagHelper(TagService tagService) {
		this.tagService = tagService;
	}

	public void setTags(String tags, int objectType, long objectId) throws UnAuthorizedException {
		if (tags == null || "".equals(tags.trim())) {
			tagService.removeAllTags(objectType, objectId);
		} else {
			List<String> tagAr = Arrays.asList(tags.trim().toLowerCase().split("(\\s|,|\\\\)"));
			List<ContentTag> removedTags = new ArrayList<ContentTag>();
			for (String tag : tagAr) {
				String safeTag = tag.trim();
				if (!"".equals(safeTag)) {
					ContentTag ct = tagService.createTag(safeTag);
					boolean hasNewTag = false;
					List<ContentTag> existingTags = tagService.getTags(objectType, objectId);
					for (ContentTag existingTag : existingTags) {
						if (ct.equals(existingTag))
							hasNewTag = true;
						if (!removedTags.contains(existingTag) && !tagAr.contains(existingTag.getName())) {
							tagService.removeTag(existingTag, objectType, objectId);
							removedTags.add(existingTag);
						}
					}
					if (!hasNewTag)
						tagService.addTag(ct, objectType, objectId);
				}
			}
		}
	}

	public String getTagsAsString(int objectType, long objectId) {
		StringBuilder tagValues = new StringBuilder();

		List<ContentTag> existingTags = tagService.getTags(objectType, objectId);

		log.debug("existingTags:" + existingTags);

		for (ContentTag existingTag : existingTags) {
			tagValues.append(existingTag.getName());
			tagValues.append(" ");
		}

		return tagValues.toString().trim();
	}

}