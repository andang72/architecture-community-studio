package architecture.community.web.spring.controller.data.secure.mgmt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.query.CustomQueryService;
import architecture.community.tag.ContentTag;
import architecture.community.tag.DefaultContentTag;
import architecture.community.tag.TagService;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

@Controller("community-mgmt-services-tags-secure-data-controller")
@RequestMapping("/data/secure/mgmt/services/")
public class ServicesTagsDataController {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("repository")
	private Repository repository;	
	
	@Autowired( required = false) 
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired( required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Autowired( required = false) 
	@Qualifier("tagService")
	private TagService tagService;
	
	/**
	 * TAG API 
	******************************************/	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/tags/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getTags(@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) {
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_CONTENT_TAGS"); 
		
		List<DefaultContentTag> list = customQueryService.list(dataSourceRequest, new RowMapper<DefaultContentTag>() {
			public DefaultContentTag mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new DefaultContentTag(rs.getLong(1), rs.getString(2), rs.getTimestamp(3));
			}
		}); 
		return new ItemList(list, list.size()); 
	}	
	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/tags/0/create.json", method = { RequestMethod.POST })
	@ResponseBody
	public ContentTag saveOrUpdate(@RequestBody DefaultContentTag tag, NativeWebRequest request) throws NotFoundException { 
		if (tag.getTagId() > 0) { 
			return tag; 
		} else {
			return tagService.createTag(tag.getName()); 
		} 
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/tags/0/delete.json", method = { RequestMethod.POST })
	@ResponseBody
	public ContentTag deleteTag(@RequestBody DefaultContentTag tag, NativeWebRequest request) throws NotFoundException { 
		return tag;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/tags/0/objects/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getTagObjects(@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) {
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_TAG_OBJECTS");
		 List<TagObject> list = customQueryService.list(dataSourceRequest, new RowMapper<TagObject>() {
			public TagObject mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new TagObject(rs.getInt(1), rs.getLong(2), rs.getLong(3));
			}
		}); 
		return new ItemList(list, list.size()); 
	}	 
 
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/tags/0/objects/create.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public TagObject createTagObject(@RequestBody TagObject tagObject, NativeWebRequest request) throws NotFoundException, UnAuthorizedException {
		
		if( tagObject.getTagId() > 0 ) {
			ContentTag tag = tagService.getTag(tagObject.getTagId());
			tagService.addTag(tag, tagObject.objectType, tagObject.objectId );
		}
		return tagObject;
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/tags/0/objects/add.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result addTagObject(@RequestBody TagsObjects tagsObjects, NativeWebRequest request) throws NotFoundException, UnAuthorizedException {
		Result result = Result.newResult();
		for( Long tagId : tagsObjects.tagIds)
		{
			ContentTag tag = tagService.getTag(tagId);
			for( Long objectId : tagsObjects.objectIds) {
				tagService.addTag(tag, tagsObjects.objectType, objectId );
				result.setCount(result.getCount() + 1);
			}
		}
		return result;
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/tags/0/objects/save-or-update.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result saveOrUpdateTagsObjects(@RequestBody TagsObjects tagsObjects, NativeWebRequest request) throws NotFoundException, UnAuthorizedException {
		
		Result result = Result.newResult(); 
		for( Long objectId : tagsObjects.objectIds) {
			tagService.removeAllTags(tagsObjects.objectType, objectId);
		}
		for( Long tagId : tagsObjects.tagIds) {
			ContentTag tag = tagService.getTag(tagId);
			for( Long objectId : tagsObjects.objectIds) {
				tagService.addTag(tag, tagsObjects.objectType, objectId );
				result.setCount(result.getCount() + 1);
			}
		}
		return result;
	}
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER" })
	@RequestMapping(value = "/tags/0/objects/delete.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public TagObject removeTagObject(@RequestBody TagObject tagObject, NativeWebRequest request)
			throws NotFoundException, UnAuthorizedException { 
		if (tagObject.getTagId() > 0) {
			ContentTag tag = tagService.getTag(tagObject.getTagId());
			tagService.removeTag(tag, tagObject.objectType, tagObject.objectId );
		} 
		return tagObject;
	}	
	
	
	public static class TagsObjects implements java.io.Serializable {
		
		private int objectType;
		private List<Long> objectIds;
		private List<Long> tagIds; 

		public TagsObjects() {
			
		}
		
		public int getObjectType() {
			return objectType;
		}

		public void setObjectType(int objectType) {
			this.objectType = objectType;
		}

		public List<Long> getObjectIds() {
			return objectIds;
		}

		public void setObjectIds(List<Long> objectIds) {
			this.objectIds = objectIds;
		}

		public List<Long> getTagIds() {
			return tagIds;
		}

		public void setTagIds(List<Long> tagIds) {
			this.tagIds = tagIds;
		}
		
	}
	
	public static class TagObject implements java.io.Serializable {
		
		private int objectType;
		private long objectId;
		private long tagId; 

		public TagObject() {
			super();
		}

		public TagObject(int objectType, long objectId, long tagId) {

			this.objectType = objectType;
			this.objectId = objectId;
			this.tagId = tagId;
		}

		public int getObjectType() {
			return objectType;
		}

		public void setObjectType(int objectType) {
			this.objectType = objectType;
		}

		public long getObjectId() {
			return objectId;
		}

		public void setObjectId(long objectId) {
			this.objectId = objectId;
		}

		public long getTagId() {
			return tagId;
		}

		public void setTagId(long tagId) {
			this.tagId = tagId;
		}

		public String getKey() {
			return tagId + "_" + objectType + "_" + objectId ;
		}
 
	}
	
}
