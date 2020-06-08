package architecture.community.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;

import com.google.common.collect.ImmutableList;

import architecture.community.category.Category;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.model.ModelObjectKey;
import architecture.community.model.Models;
import architecture.community.tag.dao.TagDao;
import architecture.community.tag.event.TagChangeEvent;
import architecture.ee.component.event.PropertyChangeEvent.Type;
import architecture.ee.util.StringUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element; 

public class DefaultTagService implements TagService {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	@Qualifier("tagDao")
	private TagDao tagDao;

	@Inject
	@Qualifier("tagIdCache")
	private Cache tagIdCache;

	@Inject
	@Qualifier("tagCache")
	private Cache tagCache;

	@Inject
	@Qualifier("tagContentCache")
	private Cache tagContentCache;
 
	@Autowired(required=false)
	private ApplicationEventPublisher applicationEventPublisher;
	
	//@Inject
	//@Qualifier("tagSetManager")
	//private TagSetManager tagSetManager;
 
	private final TagHelper tagHelper = new TagHelper(this);

	public DefaultTagService() { 
	}
	
	@PostConstruct
	public void initialize() { 
		
	}

	@PreDestroy
	public void destroy() { 
	}
 
	public ContentTag createTag(String name) {
		try {
			return getTag(name);
		} catch (TagNotFoundException e) {
			DefaultContentTag newTag = new DefaultContentTag(-1L, name.toLowerCase(), new Date());
			tagDao.createContentTag(newTag);
			if (newTag.getTagId() > 0) {
				tagCache.put( new Element( newTag.getTagId(), newTag ) );
				tagIdCache.put( new Element( newTag.getName().toLowerCase(), newTag.getTagId() ) );
			}
			
			if(applicationEventPublisher!=null) {
				applicationEventPublisher.publishEvent(new TagChangeEvent(this, Type.ADDED, newTag));
			}
			// fire event;
			return newTag;
		}
	}

	@Override
	public ContentTag getTag(String name) throws TagNotFoundException {
		if (StringUtils.isEmpty(name)) {
			throw new TagNotFoundException("Tag with null value is not valid.");
		}
		return getTag(getTagId(name));
	}

	private long getTagId(String name) throws TagNotFoundException {
		if (tagIdCache.get(name) != null) {
			Long tagId = (Long) tagIdCache.get(name).getObjectValue();
			return tagId;
		} else {
			ContentTag tag = tagDao.getContentTagByName(name);
			if (tag == null) {
				throw new TagNotFoundException(new StringBuilder().append("No tag with name '").append(name).append("' exists.").toString());
			} else {
				tagCache.put( new Element( tag.getTagId(), tag) );
				tagIdCache.put( new Element( tag.getName().toLowerCase(), tag.getTagId() ) );
				return tag.getTagId();
			}
		}
	}
 
	public ContentTag getTag(long tagId) throws TagNotFoundException {
		ContentTag tag;
		if (tagCache.get(tagId) != null) {
			tag = (ContentTag) tagCache.get(tagId).getObjectValue();
		} else {
			tag = tagDao.getContentTagById(tagId);
 
			if (tag == null)
				throw new TagNotFoundException();
			tagCache.put( new Element( tag.getTagId(), tag)) ;
			tagIdCache.put( new Element( tag.getName().toLowerCase(), tag.getTagId() ) );
		}
		return tag;
	}
 
	public void addTag(ContentTag tag, int objectType, long objectId) throws UnAuthorizedException {
		if (objectType < 0 || objectId < 0L)
			throw new IllegalStateException();
		
		synchronized (getLock(objectType, objectId)) {
			List<Long> tags = getTagIds(objectType, objectId);
			int index = tags.indexOf(Long.valueOf(tag.getTagId()));
			if (index < 0) {
				tags.add(Long.valueOf(tag.getTagId()));
				tagContentCache.put( new Element(new ModelObjectKey(objectType, objectId), tags) );
			}
		}
		
		tagDao.addTag(tag.getTagId(), objectType, objectId);
		if(applicationEventPublisher!=null) {
			applicationEventPublisher.publishEvent(new TagChangeEvent(this, Type.ADDED, tag, objectType , objectId ));
		}
	}

	private Object getLock(int objectType, long objectId) {
		return LockUtils.intern( (new StringBuilder()).append("tagmgr-").append(objectType).append(",").append(objectId).toString()) ;
	}

	public String getCacheKey(int objectType, long objectId) {
		return LockUtils.intern((new StringBuilder()).append("t-").append(objectType).append("-").append(objectId).toString());
	}


	private List<Long> getTagIds(int objectType, long objectId) {
		List<Long> tagIds;
		if (objectType < 0 || objectId < 0L)
			tagIds = Collections.emptyList();
		synchronized (getLock(objectType, objectId)) {
			String cacheKey = getCacheKey(objectType, objectId);
			if (tagContentCache.get(cacheKey) != null) {
				tagIds = (List<Long>) tagContentCache.get(cacheKey).getObjectValue();
			} else {
				tagIds = tagDao.getTagIds(objectType, objectId);
				tagContentCache.put( new Element( cacheKey, tagIds ) );
			}
		}
		return tagIds;
	}

	@Override
	public void setTags(String tags, int objectType, long objectId) {
		try {
			tagHelper.setTags(tags, objectType, objectId);
		} catch (UnAuthorizedException e) { 
			e.printStackTrace();
		}
	}
 
	public List<ContentTag> getTags(int objectType, long objectId) {
		List<Long> tagIds = getTagIds(objectType, objectId);
		List<ContentTag> tags = new ArrayList<ContentTag>(tagIds.size());

		for (Long tagId : tagIds) {
			log.debug("tag:" + tagId);
			try {
				tags.add(getTag(tagId));
			} catch (TagNotFoundException e) {
				log.error(e.getMessage());
			}
		}
		return tags;
	}

	@Override
	public String getTagsAsString(int objectType, long objectId) {
		return tagHelper.getTagsAsString(objectType, objectId);
	}

	@Override
	public int getTagCount(int objectType, long objectId) {
		return getTagIds(objectType, objectId).size();
	}

	@Override
	public Map getTagMap(int objectType) {
		return null;
	}

	@Override
	public void removeTag(ContentTag tag, int objectType, long objectId) throws UnAuthorizedException {
		if (objectType < 0 || objectId < 0L)
			throw new IllegalStateException();
		synchronized (getLock(objectType, objectId)) {
			List<Long> tags = getTagIds(objectType, objectId);
			int index = tags.indexOf(Long.valueOf(tag.getTagId()));
			if (index >= 0) {
				tags.remove(index);
				tagContentCache.put( new Element( getCacheKey(objectType, objectId), tags ) );
			} else {
				throw new IllegalArgumentException("Tag is not associated with this object");
			}
		}

		// fire event..
		removeTagFromDb(tag, objectType, objectId);
		if(applicationEventPublisher!=null) {
			applicationEventPublisher.publishEvent(new TagChangeEvent(this, Type.REMOVED, tag, objectType , objectId ));
		} 
	}

	private void removeTagFromDb(ContentTag tag, int objectType, long objectId) {
		tagDao.removeTag(tag.getTagId(), objectType, objectId);
		if (tagContentCache.get(getCacheKey(objectType, objectId)) != null)
			((List<Long>) tagContentCache.get(getCacheKey(objectType, objectId)).getObjectValue()).remove(Long.valueOf(tag.getTagId()));
		/*
		 * if(tagDao.countTags(tag.getTagId()) <= 0 &&
		 * !tagSetManager.getTagSetsTagBelongsTo(tag).hasNext()) {
		 * tagDao.deleteContentTag(tag.getTagId());
		 * tagCache.remove(Long.valueOf(tag.getTagId()));
		 * tagIdCache.remove(tag.getName().toLowerCase());
		 * 
		 * }
		 */
	}
 
	public void removeAllTags(int objectType, long objectId) throws UnAuthorizedException {
		if (objectType < 0 || objectId < 0L)
			throw new IllegalStateException();
		
		synchronized (getLock(objectType, objectId)) {
			List<ContentTag> contentTags = new ImmutableList.Builder<ContentTag>().addAll(getTags(objectType, objectId)).build();
			for (ContentTag tag : contentTags) {
				List<Long> tags = getTagIds(objectType, objectId);
				int index = tags.indexOf(Long.valueOf(tag.getTagId()));
				if (index >= 0) {
					tags.remove(index);
					tagDao.removeTag(tag.getTagId(), objectType, objectId);
				}else {
					throw new IllegalArgumentException("Tag is not associated with this object");
				}
			}
			
			tagContentCache.remove(getCacheKey(objectType, objectId));			
		}
	}
	
	public TagDelegator getTagDelegator(Category category) { 
		return new DefaultTagDelegator( Models.CATEGORY.getObjectType(), category.getCategoryId(), this); 
	}
	
}
