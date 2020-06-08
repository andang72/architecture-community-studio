package services.groovy.data;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.user.User
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.util.ParamUtils;
import architecture.community.web.spring.view.script.AbstractDataView;

// dependency 
import architecture.community.image.Image;

/**
* StatsIssueWeekly
*
* script type : data
* created : 2019. 10. 19 오후 6:08:22
*/
public class StatsIssueWeekly extends AbstractDataView 
{
	@Autowired(required=false)
	@Qualifier("customQueryJdbcDao")
	private architecture.community.query.dao.CustomQueryJdbcDao customQueryJdbcDao;  
	
	@Autowired(required=false)
	@Qualifier("imageService")
	private architecture.community.image.ImageService imageService;  
	
	public Object handle(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug( "StatsIssueWeekly" );
	
		return "welcome to island.";
	}
}