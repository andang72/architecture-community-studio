package architecture.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import architecture.community.category.CategoryService;
import architecture.community.comment.CommentService;
import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.navigator.menu.MenuService;
import architecture.community.page.PageService;
import architecture.community.query.CustomQueryService;
import architecture.community.security.spring.acls.CommunityAclService;
import architecture.community.services.CommunityAdminService;
import architecture.community.tag.TagService;
import architecture.community.user.UserManager;
import architecture.community.viewcount.ViewCountService;
import architecture.ee.exception.ComponentNotFoundException;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
import architecture.ee.util.StringUtils;

public final class CommunityContextHelper implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(CommunityContextHelper.class);

	private static ApplicationContext applicationContext = null;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public static boolean isReady(){
		if( applicationContext == null )
			return false;
		return true;
	}
	public static Repository getRepository(){ 
		return getComponent(Repository.class);
	}
	
	public static UserManager getUserManager(){
		return getComponent(UserManager.class);
	}
	
	public static PageService getPageService(){
		return getComponent(PageService.class);
	}
	
	public static CategoryService getCategoryService(){
		return getComponent(CategoryService.class);
	}
	
	public static CommentService getCommentService(){
		return getComponent(CommentService.class);
	}
	
	public static ViewCountService getViewCountServive(){
		return getComponent(ViewCountService.class);
	}
	
	public static MenuService getMenuService(){
		return getComponent(MenuService.class);
	}
	
	public static TagService getTagService(){
		return getComponent(TagService.class);
	}
	
	public static CustomQueryService getCustomQueryService(){
		return getComponent(CustomQueryService.class);
	}
	
	public static CommunityAclService getCommunityAclService() {
		return getComponent(CommunityAclService.class);
	}
	
	public static ConfigService getConfigService(){
		return getComponent(ConfigService.class);
	}

	public static CommunityAdminService getAdminService(){
		return getComponent(CommunityAdminService.class);
	}
	
	public static  <T> Boolean isAvailable(Class<T> requiredType) {
		String names[] = applicationContext.getBeanNamesForType(requiredType) ;
		if(  names != null && names.length > 0 )
			return true ;
		else 
			return false;
	}
	
	public static <T> T getComponent(Class<T> requiredType) {
		if (applicationContext == null) {
			throw new IllegalStateException(CommunityLogLocalizer.getMessage("012005"));
		}
		
		if (requiredType == null) {
			throw new IllegalArgumentException(CommunityLogLocalizer.getMessage("012001"));
		} 
		try {
			return applicationContext.getBean(requiredType);
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012002", requiredType.getName()), e);
		}
	}

	public static void autowire(Object bean) {
		applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
	}
	
	public static <T> T getComponent(String requiredName, Class<T> requiredType) {
		
		if (applicationContext == null) {
			throw new IllegalStateException(CommunityLogLocalizer.getMessage("012005"));
		}		
		
		if (requiredType == null) {
			throw new IllegalArgumentException(CommunityLogLocalizer.getMessage("012001"));
		}
		
		try {
			if( !StringUtils.isNullOrEmpty(requiredName) ){
				return applicationContext.getBean(requiredName, requiredType);
			}else {
				return applicationContext.getBean(requiredType);
			}
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommunityLogLocalizer.format("012004", requiredName, requiredType.getName() ), e);
		}

	}

	
}
