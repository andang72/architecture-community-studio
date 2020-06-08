package architecture.community.util;

import java.io.File;

public class CommunityConstants {
	
	public static String SERVICES_CONFIG_FILENAME = "services-config.xml";
	
	/** SECURITY PROPERTY KEY */
    public static final String SECURITY_AUTHENTICATION_ENCODING_ALGORITHM_PROP_NAME = "security.authentication.encoding.algorithm";
    public static final String SECURITY_AUTHENTICATION_ENCODING_SALT_PROP_NAME = "security.authentication.encoding.salt";
    public static final String SECURITY_AUTHENTICATION_AUTHORITY_PROP_NAME = "security.authentication.authority";
    
    /** LOCALE PROPERTY KEY */
    public static final String LOCALE_LANGUAGE_PROP_NAME = "locale.language";
    public static final String LOCALE_COUNTRY_PROP_NAME = "locale.country";
    public static final String LOCALE_CHARACTER_ENCODING_PROP_NAME = "locale.characterEncoding";
    public static final String LOCALE_TIMEZONE_PROP_NAME = "locale.timeZone";

 
    
    /** VIER RENDER */
    public static final String VIEW_RENDER_FREEMARKER_PROP_NAME = "view.render.freemarker";
    public static final String VIEW_RENDER_FREEMARKER_DEBUG_PROP_NAME = "view.render.freemarker.debug";
    public static final String VIEW_RENDER_FREEMARKER_TEMPLATE_LOCATION_PROP_NAME = "view.render.freemarker.template.location";    
    public static final String VIEW_RENDER_FREEMARKER_VARIABLES_PROP_NAME = "view.render.freemarker.freemarkerVariables";
    public static final String VIEW_RENDER_JSP_LOCATION_PROP_NAME = "view.render.jsp.location"; 
     
    public static final String FREEMARKER_TEMPLATE_UPDATE_DELAY_PROP_NAME = "framework.freemarker.templateUpdateDelay";
    public static final String FREEMARKER_LOG_ERROR_PROP_NAME = "framework.freemarker.logError";
    public static final String FREEMARKER_STRONG_TEMPLATE_CACHE_SIZE_PROP_NAME = "framework.freemarker.strongTemplateCacheSize";
    public static final String FREEMARKER_WEAK_TEMPLATE_CACHE_SIZE_PROP_NAME = "framework.freemarker.weakTemplateCacheSize";

    /** RESOURCES */
    
    public static final String RESOURCES_SQL_LOCATION_PROP_NAME = "resources.sql.location";  
    public static final String RESOURCES_GROOVY_LOCATION_PROP_NAME = "resources.groovy.location"; 
    public static final String RESOURCES_DECORATOR_LOCATION_PROP_NAME = "resources.decorator.location"; 
    
    /** SERVICES AUDIT */
    public static final String SERVICES_AUDIT_ENABLED_PROP_NAME = "services.audit.enabled";
    public static final String SERVICES_AUDIT_DEBUG_ENABLED_PROP_NAME = "services.audit.debug.enabled";
    /** SERVICES VIEWCOUNT */
    public static final String SERVICES_VIEWCOUNT_ENABLED_PROP_NAME = "services.viewcount.enabled";    
    
    /** SERVICES SETUP */
    public static final String SERVICES_SETUP_DATASOURCES_ENABLED_PROP_NAME = "services.setup.datasources.enabled";
    
    /** SERVICES MAIL */
    public static final String SERVICES_MAIL_ENABLED_PROP_NAME = "services.mail.enabled";
    public static final String SERVICES_MAIL_HOST_PROP_NAME = "services.mail.host";
    public static final String SERVICES_MAIL_PORT_PROP_NAME = "services.mail.port";
    public static final String SERVICES_MAIL_USERNAME_PROP_NAME = "services.mail.username";
    public static final String SERVICES_MAIL_PASSWORD_PROP_NAME = "services.mail.password";
    public static final String SERVICES_MAIL_PROTOCOL_PROP_NAME = "services.mail.protocol";
    public static final String SERVICES_MAIL_SSL_PROP_NAME = "services.mail.ssl";
    public static final String SERVICES_MAIL_DEFAULT_ENCODING_PROP_NAME = "services.mail.defaultEncoding"; 
    
    /** SERVICES PROFILE */
    public static final String SERVICES_USER_PROFILE_ENABLED_PROP_NAME = "services.user.profile.enabled";
    
    public static final String SERVICES_USER_PROFILE_CACHEABLE_PROP_NAME = "services.user.profile.cacheable";
    
	public enum Platform {
		
		WINDOWS(';'), UNIX(':');
		public final char pathSeparator;
		private Platform(char pathSeparator) {
			this.pathSeparator = pathSeparator;
		}
		
		public static Platform current() {
			if (File.pathSeparatorChar == ':')
				return UNIX;
			return WINDOWS;
		}
	}
}
