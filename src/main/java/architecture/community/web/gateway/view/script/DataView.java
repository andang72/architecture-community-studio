package architecture.community.web.gateway.view.script;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface DataView {
 
	Object handle(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
	
}
