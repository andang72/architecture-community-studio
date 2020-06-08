package architecture.community.web.spring.controller.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import architecture.community.services.CommunitySpringEventPublisher;
import architecture.community.web.util.ServletUtils;

@Controller("error-page-controller")
public class ErrorPageController {

	private static final Logger log = LoggerFactory.getLogger(ErrorPageController.class); 
	
	@Autowired(required=false)
	@Qualifier("communityEventPublisher")
	private CommunitySpringEventPublisher communitySpringEventPublisher;
	
	@RequestMapping(value="/error/{error}" , method = { RequestMethod.POST, RequestMethod.GET } )
    public String displayErrorPage (@PathVariable String error, HttpServletRequest request, HttpServletResponse response, Model model) {
	
		String restOfTheUrl = ServletUtils.getRestOfTheUrl(request,response); 
		log.debug("error restOfTheUrl:" + restOfTheUrl);	 
		if( ServletUtils.isAcceptJson(request)){
			ServletUtils.setContentType( ServletUtils.JSON_CONTENT_TYPE, response);
			model.addAttribute("output", "json"); 
		}else{
			ServletUtils.setContentType(null, response);
			model.addAttribute("output", "html") ;
		}
		
		 
		return "/error/" + error ;
    }
}
