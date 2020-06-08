package architecture.community.web.spring.freemarker;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;

public class CommunityFreeMarkerView extends FreeMarkerView {
	
	protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
		BeansWrapper wrapper = (BeansWrapper) getObjectWrapper();
		populateStatics(wrapper, model);
	}
	

	/**
	 * 모든 freemarker 템플릿에서 사용하게될 유틸리티들을 정의한다.
	 * 
	 * @param wrapper
	 * @param model
	 */
	public void populateStatics(BeansWrapper wrapper, Map<String, Object> model){
		FreemarkerHelper.populateStatics(wrapper, model);
	}


	@Override
	protected Template getTemplate(String name, Locale locale) throws IOException {
		return super.getTemplate(name, locale);
	}
}
