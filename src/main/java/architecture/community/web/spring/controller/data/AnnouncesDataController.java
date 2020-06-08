package architecture.community.web.spring.controller.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
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
import architecture.community.exception.NotFoundException;
import architecture.community.query.CustomQueryService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.ee.service.ConfigService;

@Controller("community-announces-data-controller")
public class AnnouncesDataController {

	@Autowired(required = false)
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Autowired(required = false)
	@Qualifier("announceService")
	private AnnounceService announceService;

	@Autowired(required = false)
	@Qualifier("configService")
	private ConfigService configService;

	private Logger log = LoggerFactory.getLogger(ResourcesDataController.class);

	@PreAuthorize("permitAll")
	@RequestMapping(value = "/data/announces/{announceId:[\\p{Digit}]+}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Announce getAnnounce(@PathVariable Long announceId, NativeWebRequest request) throws AnnounceNotFoundException {

		User user = SecurityHelper.getUser();
		Announce announce = announceService.getAnnounce(announceId);
		return announce;
	}

	@PreAuthorize("permitAll")
	@RequestMapping(value = "/data/announces/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ItemList getAnnounces( 
			@RequestParam(value = "objectType", defaultValue = "0", required = false) Integer objectType,
			@RequestParam(value = "objectId", defaultValue = "0", required = false) Long objectId,
			@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) throws NotFoundException {
		
		ItemList items = new ItemList();
		User user = SecurityHelper.getUser(); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date startDate = null;
		Date endDate = null; 
		try {
			if (dataSourceRequest.getDataAsString("startDate", null) != null)
				startDate = simpleDateFormat.parse(dataSourceRequest.getDataAsString("startDate", null));
			if (dataSourceRequest.getDataAsString("endDate", null) != null)
				endDate = simpleDateFormat.parse(dataSourceRequest.getDataAsString("endDate", null));
			if (startDate == null)
				startDate = Calendar.getInstance().getTime();
			if (endDate == null)
				endDate = Calendar.getInstance().getTime();
		} catch (ParseException e) {
		}

		items.setItems(announceService.getAnnounces(objectType, objectId, startDate, endDate));
		items.setTotalCount(getTotalAnnounceCount(objectType, objectId, startDate, endDate));

		return items;
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
