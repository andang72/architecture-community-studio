package architecture.community.web.spring.controller.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.model.Models;
import architecture.community.util.CommunityConstants;
import architecture.community.viewcount.ViewCountService;
import architecture.community.web.model.Result;
import architecture.ee.service.ConfigService;

@RestController("community-viewcount-data-controller") 
public class ViewCountDataController {
    
    private Logger log = LoggerFactory.getLogger(StreamsDataController.class);

    @Autowired(required=false)
	@Qualifier("configService")
	private ConfigService configService;

    @Autowired(required = false) 
	@Qualifier("viewCountService")
	private ViewCountService viewCountService;

     
	@RequestMapping(value = "/data/viewcount/{objectTypeName}/{objectId:[\\p{Digit}]+}", method = { RequestMethod.POST },produces = MediaType.APPLICATION_JSON_VALUE)
	public Result view (
        @PathVariable("objectTypeName") String objectTypeName,  
        @PathVariable("objectId") Long objectId,    
        NativeWebRequest request) throws NotFoundException {   

        if( log.isDebugEnabled() ){
            log.debug("viewcount ({}) objectName:{}, objectId:{}", isViewCountsEnabled()? "enabled" : "disabled", objectTypeName, objectId);
        } 

		if(isViewCountsEnabled()){ 
            Models models = Models.valueOf(objectTypeName.toUpperCase()); 
            viewCountService.addViewCount(models.getObjectType(), objectId);
        }
        return Result.newResult();
	}

    public boolean isViewCountsEnabled(){
        return configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, false);
    }

} 