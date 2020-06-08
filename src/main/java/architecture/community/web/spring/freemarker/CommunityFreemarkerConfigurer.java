package architecture.community.web.spring.freemarker;

import java.io.IOException;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import architecture.community.util.CommunityConstants;
import architecture.ee.service.ConfigService;
import freemarker.template.TemplateException;

public class CommunityFreemarkerConfigurer extends FreeMarkerConfigurer {

	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	public void afterPropertiesSet() throws IOException, TemplateException {
		if( configService != null)
		{
			setTemplateLoaderPath(configService.getLocalProperty(CommunityConstants.VIEW_RENDER_FREEMARKER_TEMPLATE_LOCATION_PROP_NAME, "/WEB-INF/template/ftl/"));
		}
		super.afterPropertiesSet();
	}

}
