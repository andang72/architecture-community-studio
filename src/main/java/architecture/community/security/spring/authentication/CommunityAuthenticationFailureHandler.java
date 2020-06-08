package architecture.community.security.spring.authentication;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import architecture.community.web.model.Result;
import architecture.community.web.util.ServletUtils;
import architecture.ee.util.StringUtils;

public class CommunityAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{

	private Logger logger = LoggerFactory.getLogger(getClass());
	 
	public CommunityAuthenticationFailureHandler() { 
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		logger.debug("no savend request");			
		if (ServletUtils.isAcceptJson(request)) {
			logger.debug("handle json request");
			handleJsonRequest(request, response, exception);
		}else{
			logger.debug("handle normal request");
			super.onAuthenticationFailure(request, response, exception);
		}
		return;
		
	}

	protected void handleJsonRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException  exception) throws IOException, ServletException{
		Result result = Result.newResult();
		result.setError(exception);
		
		result.getData().put("returnUrl", ServletUtils.getReturnUrl(request, response));		
		String referer = request.getHeader("Referer");		
		if (StringUtils.isNullOrEmpty(referer))
			result.getData().put("referer", referer);
		
		Map<String, Object> model = new ModelMap();
		model.put("item", result);
		try {
			createJsonViewAndRender(model, request, response);
		} catch (Exception e) {}	
	}
	
	
	protected void createJsonViewAndRender(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setHeader("Access-Control-Allow-Origin","*");
		
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setExtractValueFromSingleKeyModel(true);
		view.setModelKey("item");
		view.render(model, request, response);
	}
	
}
