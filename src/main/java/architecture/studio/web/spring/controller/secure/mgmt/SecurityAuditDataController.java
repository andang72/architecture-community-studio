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

import architecture.community.audit.event.AuditTrailsService;
import architecture.community.exception.NotFoundException;
import architecture.community.util.CommunityConstants;
import architecture.community.util.DateUtils;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.AbstractResourcesDataController;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;

@Controller("community-mgmt-security-audit-secure-data-controller")
@RequestMapping("/data/secure/mgmt/security/audit")
public class SecurityAuditDataController extends AbstractResourcesDataController { 
	
	protected Logger log = LoggerFactory.getLogger(getClass()); 
	
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;
	
	@Autowired(required=false)
	@Qualifier("auditTrailsService")
	private AuditTrailsService auditTrailsService;
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/enable.json", method = { RequestMethod.POST })
	@ResponseBody
    public Result enable(NativeWebRequest request) throws NotFoundException, IOException {  
		configService.setApplicationProperty(CommunityConstants.SERVICES_AUDIT_ENABLED_PROP_NAME, "true");
		log.debug("audit service : {}", true); 
		return Result.newResult();
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/config.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public AuditServiceConfig getProfileServicesConfig(NativeWebRequest request) throws NotFoundException, IOException {  		
		return getAuditServiceConfig(); 
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
	@RequestMapping(value = "/config/save-or-update.json", method = { RequestMethod.POST })
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
	
	private AuditServiceConfig getAuditServiceConfig() {  
		AuditServiceConfig config = new AuditServiceConfig(); 
		config.enabled = configService.getApplicationBooleanProperty(CommunityConstants.SERVICES_AUDIT_ENABLED_PROP_NAME, false); 
		try {
			File targetFile = getResourceByType(ResourceType.SCRIPT, "/services/customAuditTrailsService.groovy").getFile();
			FileInfo fileInfo = new FileInfo(targetFile);  
			config.scriptSource = fileInfo;
			fileInfo.setFileContent(targetFile.isDirectory() ? "" : FileUtils.readFileToString(targetFile, "UTF-8")); 
			
		} catch (IOException e) { 
		}
		return config;
	}
	
	public static class AuditServiceConfig implements java.io.Serializable {
		
		private boolean enabled; 
		
		private FileInfo scriptSource; 
		
		public AuditServiceConfig() {  
		}
		
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		} 
		
		public FileInfo getScriptSource() {
			return scriptSource;
		}
 
		public void setScriptSource(FileInfo scriptSource) {
			this.scriptSource = scriptSource;
		} 
 
	}	
}
