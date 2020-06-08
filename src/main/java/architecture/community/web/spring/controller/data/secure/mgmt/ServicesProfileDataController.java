package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.NotFoundException;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserProfile;
import architecture.community.user.UserProfileService;
import architecture.community.util.DateUtils;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.AbstractResourcesDataController;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

@Controller("community-mgmt-services-profile-secure-data-controller")
@RequestMapping("/data/secure/mgmt/services/profile")
public class ServicesProfileDataController extends AbstractResourcesDataController {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("repository")
	private Repository repository;	
	
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired(required=false)
	@Qualifier("customUserProfileService")
	private UserProfileService profileService; 
	
	@Autowired(required = false) 
	@Qualifier("userManager")
	private UserManager userManager;
	
	public ServicesProfileDataController() { 
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/config.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public ProfileServiceConfig getProfileServicesConfig(
    		NativeWebRequest request) throws NotFoundException, IOException {  		
		return getProfileServiceConfig(); 
    }  
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/save-or-update.json", method = { RequestMethod.POST })
	@ResponseBody
    public ProfileServiceConfig saveOrUpdate(
    		@RequestBody ProfileServiceConfig config, 
    		@RequestParam(value = "restart", defaultValue = "true", required = false) Boolean restart, 
    		NativeWebRequest request) throws NotFoundException, IOException {  
		configService.setApplicationProperty("services.user.profile.cacheable", Boolean.toString(config.cacheable));
		configService.setApplicationProperty("services.user.profile.enabled", Boolean.toString(config.enabled));
		log.debug("Restart profile service : {}", restart);
		profileService.refresh();
		return getProfileServiceConfig(); 
	}
	
	/**
	 * 파일 내용을 업데이트 한다. 동일경로에 파일이름 + .[yyyyMMddHHmmss] 형식으로 백업을 생성한 다음 저장한다.
	 *  
	 * @param file 파일 경로 및 내용을 포함하는 객제 
	 * @param request
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/script/save-or-update.json", method = { RequestMethod.POST })
    @ResponseBody
    public FileInfo saveOrUpdate(
    		@PathVariable String type, 
    		@RequestParam(value = "backup", defaultValue = "true", required = false) Boolean backup, 
    		@RequestBody FileInfo file, 
    		NativeWebRequest request) throws NotFoundException, IOException {
		
		File target =  getResourceByType(ResourceType.SCRIPT, file.getPath() ).getFile();
		// backup to filename + .yyyymmddhhmmss .
		if( backup ) {
			File backupFile = new File(target.getParentFile() , target.getName() + "." + DateUtils.toString(new Date()) );  
			FileUtils.copyFile(target, backupFile); 
		}
		
		FileUtils.writeStringToFile(target, file.getFileContent(), ServletUtils.DEFAULT_HTML_ENCODING , false);  
		FileInfo fileInfo = new FileInfo(target); 
		fileInfo.setFileContent(target.isDirectory() ? "" : FileUtils.readFileToString(target, "UTF-8")); 
		
		return fileInfo;
    } 	 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/test.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public Result testConnection(
    	@RequestBody DataSourceRequest dataSourceRequest,
    	NativeWebRequest request) {   
		
		long testUserId = dataSourceRequest.getDataAsLong("testUserId", 0L); 
		Result result = Result.newResult();
		log.debug("Profile Service Enabled  : {}", profileService.isEnabled() );
		if( profileService.isEnabled())
		{
			log.debug("Test .....");
			try {
				User testUser = userManager.getUser(testUserId);
				log.debug("get profile for  {}", testUser.getUsername() );
				UserProfile p = profileService.getUserProfile( testUser );
				result.getData().put("profile", p);
				log.debug("Test Connection Successed.");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				result.setError(e);
			}
		}
		return result; 
    } 
	
	private ProfileServiceConfig getProfileServiceConfig() { 
		
		ProfileServiceConfig config = new ProfileServiceConfig();
		config.cacheable = configService.getApplicationBooleanProperty("services.user.profile.cacheable", false);
		config.enabled = configService.getApplicationBooleanProperty("services.user.profile.enabled", false); 
		try {
			File targetFile = getResourceByType(ResourceType.SCRIPT, "/services/customUserProfileService.groovy").getFile();
			FileInfo fileInfo = new FileInfo(targetFile); 
			
			config.scriptSource = fileInfo;
			fileInfo.setFileContent(targetFile.isDirectory() ? "" : FileUtils.readFileToString(targetFile, "UTF-8")); 
			
		} catch (IOException e) { 
		}
		return config;
	}
	
	public static class ProfileServiceConfig implements java.io.Serializable {
		
		private boolean enabled;
		
		private boolean cacheable;
		
		private FileInfo scriptSource; 
		
		public ProfileServiceConfig() { 
			
		}
		
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public boolean isCacheable() {
			return cacheable;
		}
		public void setCacheable(boolean cacheable) {
			this.cacheable = cacheable;
		}
		
		public FileInfo getScriptSource() {
			return scriptSource;
		}
 
		public void setScriptSource(FileInfo scriptSource) {
			this.scriptSource = scriptSource;
		} 
 
	}
	
}
