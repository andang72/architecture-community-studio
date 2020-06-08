package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.management.BadAttributeValueExpException;
import javax.management.BadBinaryOpValueExpException;
import javax.management.BadStringOperationException;
import javax.management.InvalidApplicationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.services.CommunityAdminService;
import architecture.community.util.CommunityConstants;
import architecture.ee.component.platform.DiskUsage;
import architecture.ee.component.platform.MemoryUsage;
import architecture.ee.component.platform.SystemInfo;
import architecture.ee.service.ConfigService;
import architecture.ee.util.StringUtils;
import architecture.ee.util.maven.MavenVersionReader;

@Controller("community-mgmt-status-secure-data-controller")
@RequestMapping("/data/secure/mgmt/status")
public class StatusDataController {

	private static final Logger log = LoggerFactory.getLogger(StatusDataController.class);

	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("adminService")
	private CommunityAdminService adminService;
	
	public StatusDataController() { 
	}

	/**
	 * Application Property 
	 */
	public static class ApplicationProperty {
		
		private Boolean datasourceEnabled = false ;
		private Boolean databaseInitialized = false;
		private Boolean viewCountEnabled = false;
		private Boolean auditEnabled = false;
		private String language;
		private String timezone ;
		
		public Boolean getDatasourceEnabled() {
			return datasourceEnabled;
		}

		public Boolean getDatabaseInitialized() {
			return databaseInitialized;
		}

		public String getLanguage() {
			return language;
		}

		public String getTimezone() {
			return timezone;
		}
		
		public Boolean getAuditEnabled() {
			return auditEnabled;
		}

		public static ApplicationProperty build(ConfigService configService) {
			ApplicationProperty props = new ApplicationProperty();
			props.datasourceEnabled = configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_SETUP_DATASOURCES_ENABLED_PROP_NAME, false);
			props.databaseInitialized = configService.isDatabaseInitialized();
			props.language = configService.getLocale().getLanguage();
			props.timezone = configService.getTimeZone().getID();
			props.viewCountEnabled = configService.getApplicationBooleanProperty( CommunityConstants.SERVICES_VIEWCOUNT_ENABLED_PROP_NAME, false);
			props.auditEnabled = configService.getApplicationBooleanProperty( CommunityConstants.SERVICES_AUDIT_ENABLED_PROP_NAME, false);
			return props;
		}
	}
	
	@RequestMapping(value="/config.json",method={RequestMethod.POST, RequestMethod.GET} )
	@ResponseBody
	public ApplicationProperty getApplicationProperty ( NativeWebRequest request) throws Exception 	
	{	
		return ApplicationProperty.build(configService);
	}
	
	
	/**
	 * Memory  Usage 
	 */
	
	@RequestMapping(value="/disk/usage.json",method={RequestMethod.POST, RequestMethod.GET} )
	@ResponseBody
	public List<DiskUsage> getDiskUsages( NativeWebRequest request) throws Exception 	
	{	
		List<DiskUsage> usage = adminService.getDiskUsages();
		if( usage == null )
			Collections.emptyList();
		
		return usage;
	}
	
	@RequestMapping(value="/memory/usage.json",method={RequestMethod.POST, RequestMethod.GET} )
	@ResponseBody
	public MemoryUsage getMemoryUsage( NativeWebRequest request) throws Exception 	
	{	
		MemoryUsage usage = adminService.getMemoryUsage();
		return usage;
	}	

	@RequestMapping(value="/platform/info.json",method={RequestMethod.POST, RequestMethod.GET} )
	@ResponseBody
	public SystemInfo getSystemInfo( NativeWebRequest request) throws Exception 	
	{	
		return adminService.getSystemInfo(); 
	}	
	
	/**
	 * Disk Usage
	 */
	
	/**
	 * Library 
	 */
	
	@RequestMapping(value="/library/list.json",method={RequestMethod.POST, RequestMethod.GET} )
	@ResponseBody
	public List<LibraryObject> getLibraries(
			NativeWebRequest request) throws Exception 	
	{	
		ArrayList<LibraryObject> aBeans = new ArrayList<LibraryObject>();
		String context = request.getContextPath();
		if( StringUtils.isEmpty(context) )
			context = "/";
		
		List<URL> classpath = getClassPathUrls(context);
		for (URL url : classpath){
			String fileName = url.getFile();
			if (!fileName.endsWith(".jar"))
				continue;
			File f = new File(fileName);
			LibraryObject bean = new LibraryObject();
			int lastSlash = fileName.lastIndexOf('/');
			try{
				bean.setName(fileName.substring(lastSlash + 1));
				bean.setMavenVersion(MavenVersionReader.readVersionFromJar(f));				
				if (bean.getMavenVersion()==null){
					bean.setLastModified(new Date(f.lastModified()));
				}
			}catch(Exception e){
				log.warn("couldn't obtain lib version, skipped this url "+url, e);
			}
			aBeans.add(bean);
		}
		
		return aBeans;
	}
	
	private List<URL> getClassPathUrls(final String context){
		log.debug("get classpath urls : {}", context);
		
		List<URL> forTomcat7 = getClassPathUrlsForTomcat(context, "context");
		if (forTomcat7!=null && forTomcat7.size()>0)
			return forTomcat7;
		
		List<URL> forTomcat6 = getClassPathUrlsForTomcat(context, "path");
		if (forTomcat6!=null && forTomcat6.size()>0)
			return forTomcat6;
		
		//add another lookup methods here.
		return new ArrayList<URL>();
	}	
	
	private List<URL> getClassPathUrlsForTomcat(final String context, final String propertyName) { 
		List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
		for (MBeanServer mbeanServer: servers) {
			Set<ObjectInstance> instances = mbeanServer.queryMBeans(null, new QueryExp() { 
				public boolean apply(ObjectName name)
						throws BadStringOperationException,
						BadBinaryOpValueExpException,
						BadAttributeValueExpException,
						InvalidApplicationException { 
					String type = name.getKeyProperty("type");
					log.debug("domain '{}' properties : {} " , name.getDomain() , name.getKeyPropertyList() ); 
					if (!type.equals("WebappClassLoader"))
						return false;
					
					if (!name.getDomain().equals("Catalina"))
						return false;
					
					if (!name.getKeyProperty(propertyName).equals(context))
						return false;
					
					return true;
				} 
				public void setMBeanServer(MBeanServer s) {
				}
			});
			if (instances.size() > 0) {
				try {
					URL[] urls = (URL[])mbeanServer.getAttribute(instances.iterator().next().getObjectName(), "URLs");
					return Arrays.asList(urls);
				} catch (Exception e) {
				} 
			}
		}
		return null;
	}	
}
