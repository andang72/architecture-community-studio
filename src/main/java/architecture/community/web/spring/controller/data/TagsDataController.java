package architecture.community.web.spring.controller.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.UnAuthorizedException;
import architecture.community.query.CustomQueryService;
import architecture.community.tag.DefaultContentTag;
import architecture.community.tag.TagService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

 
@Controller("community-tags-data-controller")
@RequestMapping("/data")	
public class TagsDataController {
	
	private Logger log = LoggerFactory.getLogger(UserDataController.class);
	
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
	 
	@RequestMapping(value = "/tags/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<ItemList> getTags(@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) throws UnAuthorizedException  {
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_CONTENT_TAGS"); 
		
		User user = SecurityHelper.getUser();
		//if(user.isAnonymous())
		//	throw new UnAuthorizedException("No Authorized. Please signin first.");
		
		List<DefaultContentTag> list = customQueryService.list(dataSourceRequest, new RowMapper<DefaultContentTag>() {
			public DefaultContentTag mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new DefaultContentTag(rs.getLong(1), rs.getString(2), rs.getTimestamp(3));
			}
		});  
		return ResponseEntity.ok(new ItemList(list, list.size()) );
	}	
	
}
