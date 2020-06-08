package architecture.community.web.spring.controller.data.secure.mgmt;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.announce.Announce;
import architecture.community.announce.AnnounceNotFoundException;
import architecture.community.announce.AnnounceService;
import architecture.community.audit.annotation.Audit;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.image.Image;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.query.CustomTransactionCallbackWithoutResult;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

@Controller("community-mgmt-services-announces-secure-data-controller")
@RequestMapping("/data/secure/mgmt/announces")
public class ServicesAnnouncesDataController {

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
	
	@Inject
	@Qualifier("announceService")
	private AnnounceService announceService;
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;
	

	/**
	 * ANNOUNCE API 
	******************************************/
	

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_OPERATOR"})
	@RequestMapping(value = "/0/save-or-update.json", method = RequestMethod.POST)
	@ResponseBody
	public Announce saveOrUpdateAnnounce(@RequestBody Announce announce, NativeWebRequest request) throws AnnounceNotFoundException, UnAuthorizedException {

		User user = SecurityHelper.getUser();
		if (announce.getUser() == null && announce.getAnnounceId() == 0)
			announce.setUser(user);

		if ( user.isAnonymous() || ( !SecurityHelper.isUserInRole("ROLE_ADMINISTRATOR")  && user.getUserId() != announce.getUser().getUserId()) ) {
			throw new UnAuthorizedException();
		}

		Announce target;
		if (announce.getAnnounceId() > 0) {
			target = announceService.getAnnounce(announce.getAnnounceId());
		} else {
			target = announceService.createAnnounce(user, announce.getObjectType(), announce.getObjectId());
		}

		target.setSubject(announce.getSubject());
		target.setBody(announce.getBody());
		target.setStartDate(announce.getStartDate());
		target.setEndDate(announce.getEndDate());
		
		if (target.getAnnounceId() > 0) {
			announceService.updateAnnounce(target);
		} else { 
			announceService.addAnnounce(target);
		} 
		customQueryService.execute(new CustomTransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(CustomQueryJdbcDao dao) { 

				List<String> linkIds = architecture.community.web.spring.controller.data.Utils
						.getImageLinksFromHtml(target.getBody());
				for (String linkId : linkIds) {
					try {
						Image image = imageService.getImageByImageLink(linkId);
						if (image.getObjectId() < 1) {
							log.debug("update image object type and object id.");
							dao.getExtendedJdbcTemplate().update(
								dao.getBoundSql("COMMUNITY_WEB.UPDATE_IMAGE_OBJECT_TYPE_AND_OBJECT_ID").getSql(),
								new SqlParameterValue(Types.NUMERIC, Models.ANNOUNCE.getObjectType()),
								new SqlParameterValue(Types.NUMERIC, announce.getAnnounceId()),
								new SqlParameterValue(Types.NUMERIC, image.getImageId()));
							imageService.invalidate(image, false);
						}
					} catch (NotFoundException e) {
					}
				} 
			} 
		}); 
		return target;
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_OPERATOR"})
	@RequestMapping(value = "/{announceId:[\\p{Digit}]+}/delete.json", method = RequestMethod.POST)
	@ResponseBody
	public Result destoryAnnounce( @PathVariable Long announceId, NativeWebRequest request) throws AnnounceNotFoundException {
		
		User user = SecurityHelper.getUser();

		Announce announce = announceService.getAnnounce(announceId);
		if( announce.getUser().getUserId() == user.getUserId())
			announceService.deleteAnnounce(announceId);
		return Result.newResult();
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_OPERATOR"})
	@RequestMapping(value = "/{announceId:[\\p{Digit}]+}/get.json", method = RequestMethod.POST)
	@ResponseBody
	public Announce getAnnounce( @PathVariable Long announceId, NativeWebRequest request) throws AnnounceNotFoundException {
		
		User user = SecurityHelper.getUser();

		Announce announce = announceService.getAnnounce(announceId);
		
		return announce;
	}
	
	
	@Audit(action="LIST_ANNOUNCES", 
		   applicationCode = "LIST_ANNOUNCES",
		   actionResolverName="LIST_ANNOUNCES",  
		   resourceResolverName="GRANT_SERVICE_TICKET_RESOURCE_RESOLVER")
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_OPERATOR"})
    @RequestMapping(value = "/list.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
	public ItemList getAnnounces(
		@RequestParam(value = "objectType", defaultValue = "0", required = false) Integer objectType,
		@RequestParam(value = "objectId", defaultValue = "0", required = false) Long objectId,			
		@RequestBody DataSourceRequest dataSourceRequest,
		NativeWebRequest request) throws NotFoundException {
		
		User user = SecurityHelper.getUser(); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date startDate = null;
		Date endDate = null;		
		try {
			if(dataSourceRequest.getDataAsString("startDate", null)!=null)
				startDate = simpleDateFormat.parse(dataSourceRequest.getDataAsString("startDate", null));
			if(dataSourceRequest.getDataAsString("endDate", null)!=null)
				endDate = simpleDateFormat.parse(dataSourceRequest.getDataAsString("endDate", null)); 
			if (startDate == null)
			    startDate = Calendar.getInstance().getTime();
			if (endDate == null)
			    endDate = Calendar.getInstance().getTime();
		} catch (ParseException e) {} 
		
		dataSourceRequest.setStatement("COMMUNITY_WEB.COUNT_ANNOUNCE_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_WEB.SELECT_ANNOUNCE_IDS_BY_REQUEST");
		List<Long> items = customQueryService.list(dataSourceRequest, Long.class);
		List<Announce> announces = new ArrayList<Announce>(items.size());
		for( Long announceId : items ) {
			try {
				Announce announce = announceService.getAnnounce(announceId); 
				announces.add(announce); 
			} catch (NotFoundException e) {
			}
		}  
		return new ItemList(announces, totalCount );
	} 
	
	private int getTotalAnnounceCount(int objectType, long objectId, Date startDate, Date endDate) {
		if (startDate != null) {
		    return announceService.getAnnounceCount(objectType, objectId, startDate,
			    endDate == null ? Calendar.getInstance().getTime() : endDate);
		}
		return announceService.getAnnounceCount(objectType, objectId,
			endDate == null ? Calendar.getInstance().getTime() : endDate);
	}
	
}
