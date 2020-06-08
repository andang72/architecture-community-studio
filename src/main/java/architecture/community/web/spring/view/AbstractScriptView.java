package architecture.community.web.spring.view;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;

import architecture.community.web.spring.view.script.ScriptSupport;
import architecture.community.web.util.ServletUtils;

public abstract class AbstractScriptView extends ScriptSupport implements View {

	private static final String DEFAULT_PREFIX = "view.";
	
	private String prefix = DEFAULT_PREFIX ;
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	public AbstractScriptView() {
		
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getContentType() { 
		return ServletUtils.DEFAULT_HTML_CONTENT_TYPE;
	}
	
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("Rendering view {} with model '{}'", getClass().getName(), model );
		}
		prepareResponse(request, response);
		renderMergedOutputModel((Map<String, Object>) model, request, response); 
	}

	protected void redirect (HttpServletRequest request, HttpServletResponse response, String dispatcherPath) throws Exception {
		RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
		if (rd == null) {
			throw new ServletException("Could not get RequestDispatcher for [" + dispatcherPath +
					"]: Check that the corresponding file exists within your web application archive!");
		}
		if (log.isDebugEnabled()) {
			log.debug("Forwarding to resource [" + dispatcherPath + "] in InternalResourceView '" + this.getClass().getName() + "'");
		}
		response.setHeader("Location", dispatcherPath );
		response.setStatus(302);
	}
	
	protected void dispatch(HttpServletRequest request, HttpServletResponse response, String dispatcherPath) throws Exception {
		RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
		if (rd == null) {
			throw new ServletException("Could not get RequestDispatcher for [" + dispatcherPath +
					"]: Check that the corresponding file exists within your web application archive!");
		}
		if (log.isDebugEnabled()) {
			log.debug("Forwarding to resource [" + dispatcherPath + "] in InternalResourceView '" + this.getClass().getName() + "'");
		}
		rd.forward(request, response);			
	}
		
	protected RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
		return request.getRequestDispatcher(path);
	}

	protected abstract void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
		
	} 
	
}
