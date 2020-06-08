package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.query.CustomQueryService;
import architecture.community.util.CommunityConstants;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.spring.controller.data.model.ServicesConfig;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;

@Controller("community-mgmt-services-audit-secure-data-controller")
@RequestMapping("/data/secure/mgmt/services/audit")
public class ServiceAuditDataController {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/config.json", method = { RequestMethod.POST })
	@ResponseBody
	public ServicesConfig getConfig (NativeWebRequest request) throws NotFoundException { 
		boolean enabled = configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_AUDIT_ENABLED_PROP_NAME, false);
		ServicesConfig config = new ServicesConfig();
		config.setEnabled(enabled);
		return config;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/config/save-or-update.json", method = { RequestMethod.POST })
	@ResponseBody
	public ServicesConfig saveOrUpdate (@RequestBody  ServicesConfig config,  NativeWebRequest request) throws NotFoundException {  
		log.debug("viewcounts : {}", config.isEnabled());
		configService.setApplicationProperty( CommunityConstants.SERVICES_AUDIT_ENABLED_PROP_NAME, Boolean.toString(config.isEnabled()));
		return config;
	}	
	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getAudits(
		@RequestBody DataSourceRequest dataSourceRequest, 
		@RequestParam(value = "fields", defaultValue = "none", required = false) String fields,
		NativeWebRequest request) { 
		
		dataSourceRequest.setStatement("COMMUNITY_SECURITY.COUNT_AUDIT_TRAIL_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_SECURITY.SELECT_AUDIT_TRAIL_BY_REQUEST");
		/**
		List<AuditActionContext> items = customQueryService.list(dataSourceRequest, new RowMapper<AuditActionContext>() { 
			public AuditActionContext mapRow(ResultSet rs, int rowNum) throws SQLException {
				final String principal = rs.getString("AUD_USER");
				final String resource = rs.getString("AUD_RESOURCE");
				final String clientIp = rs.getString("AUD_CLIENT_IP");
				final String serverIp = rs.getString("AUD_SERVER_IP");
				final Date audDate = rs.getDate("AUD_DATE");
				final String appCode = rs.getString("APPLIC_CD");
				final String action = rs.getString("AUD_ACTION");
				final String userAgent = rs.getString("AUD_CLIENT_USERAGENT"); 
				Assert.notNull(principal, "AUD_USER cannot be null");
				Assert.notNull(resource, "AUD_RESOURCE cannot be null");
				Assert.notNull(clientIp, "AUD_CLIENT_IP cannot be null");
				Assert.notNull(serverIp, "AUD_SERVER_IP cannot be null");
				Assert.notNull(audDate, "AUD_DATE cannot be null");
				Assert.notNull(appCode, "APPLIC_CD cannot be null");
				Assert.notNull(action, "AUD_ACTION cannot be null"); 
				final AuditActionContext audit = new AuditActionContext( principal, resource, action, appCode, audDate, clientIp, serverIp, userAgent); 
				return audit;
		}});
		*/
		List<Map<String, Object>> items = customQueryService.list(dataSourceRequest);
		for( Map<String, Object> row : items) {
			Date date = (Date)row.get("AUD_DATE");
			row.put("AUD_DATE", ServletUtils.getDataAsISO8601(date));
		}
		return new ItemList(items, totalCount );
	}		
}
