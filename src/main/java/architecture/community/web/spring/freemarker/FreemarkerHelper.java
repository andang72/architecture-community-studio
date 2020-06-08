package architecture.community.web.spring.freemarker;

import java.util.Map;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public class FreemarkerHelper {
	 
	public static void populateStatics(BeansWrapper wrapper, Map<String, Object> model) {
		try {
			TemplateHashModel enumModels = wrapper.getEnumModels();
			try {

			} catch (Exception e) {

			}
			model.put("enums", wrapper.getEnumModels());
		} catch (UnsupportedOperationException e) {
		}

		TemplateHashModel staticModels = wrapper.getStaticModels();
		try {
			model.put("CommunityConstants",	staticModels.get("architecture.community.util.CommunityConstants"));
			model.put("StringUtils",	staticModels.get("architecture.ee.util.StringUtils"));
			model.put("LocaleUtils",	staticModels.get("architecture.ee.util.LocaleUtils"));
			model.put("ServletUtils",	staticModels.get("architecture.community.web.util.ServletUtils"));
			model.put("SecurityHelper",	staticModels.get("architecture.community.util.SecurityHelper"));
			model.put("CommunityContextHelper",	staticModels.get("architecture.community.util.CommunityContextHelper"));
			model.put("WebApplicationContextUtils",	staticModels.get("org.springframework.web.context.support.WebApplicationContextUtils"));
		} catch (TemplateModelException e) {

		}

		model.put("statics", BeansWrapper.getDefaultInstance().getStaticModels());

	}
}
