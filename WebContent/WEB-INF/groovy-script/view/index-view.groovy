package services.view
 
 /** common package import here! */
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier;
import architecture.community.announce.*;
import architecture.community.page.Page; 

/** custom query service package import here! */
import architecture.community.query.CustomQueryService;
import architecture.community.web.model.DataSourceRequest;
	
/**
* Index Page View Script..
* 
* @author donghyuck son
*/
public class IndexView extends architecture.community.web.spring.view.script.AbstractPageView  {
	
 	@Inject
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Autowired(required = false)
	@Qualifier("announceService")
	private AnnounceService announceService;
	
	protected void renderMergedOutputModel(
		Map<String, ?> model, 
		HttpServletRequest request, 
		HttpServletResponse response) throws Exception {   
		if (log.isDebugEnabled()) {
			log.debug("Rendering view {} with model '{}'", getClass().getName(), model );
		} 
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT T1.LINK_ID  FROM AC_UI_IMAGE_LINK T1").append(System.lineSeparator() );
		sb.append("INNER JOIN AC_UI_IMAGE_PROPERTY T2").append(System.lineSeparator() );
		sb.append("ON T1.IMAGE_ID = T2.IMAGE_ID").append( System.lineSeparator() );
		sb.append("WHERE  T2.PROPERTY_NAME = 'wallpaper' AND T2.PROPERTY_VALUE = 'true'");
		List<String> list = customQueryService.getCustomQueryJdbcDao().jdbcTemplate.queryForList(sb.toString(), String.class);
		model.put("wallpapers", list ); 
		
		List<Announce> announces = announceService.getAnnounces(-1, 0);
		model.put("announces", announces ); 
		
		
		// get Page and variables from model.. 
		Page page = getPage(model);
		Map<String, ?> variables = getVariables(model); 
		log.debug( "PAGE : " + page + "-" + list ); 
		// checking roles using isUserInRole 
		// isUserInRole("ROLE_OPERATOR")) 
		
		
	} 
}