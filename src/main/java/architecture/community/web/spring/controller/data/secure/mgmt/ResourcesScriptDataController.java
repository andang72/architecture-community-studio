package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.support.ServletContextResourceLoader;

import com.google.common.base.CaseFormat;

import architecture.community.exception.NotFoundException;
import architecture.community.util.DateUtils;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.secure.mgmt.GroovyForm.ClassInfo;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

@Controller("community-mgmt-resources-script-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class ResourcesScriptDataController {

	private Logger log = LoggerFactory.getLogger(getClass());
 
	@Autowired
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	@Qualifier("repository")
	private Repository repository;

	
	private ServletContextResourceLoader loader = null;

	public ResourcesScriptDataController() {
		
	} 
	
	protected ResourceLoader getResourceLoader () {
		if( loader == null )
			loader = new ServletContextResourceLoader(servletContext);
		return loader;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/scripts/0/prepare.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public GroovyForm prepareGroovyClass(
    	@RequestBody GroovyForm data,
    	NativeWebRequest request) throws NotFoundException, IOException {  
 		GroovyForm formToUse = data;
		if( StringUtils.isEmpty(formToUse.getClassName()) && StringUtils.isNotBlank( formToUse.getFilename() ) ) 
		{
			
			String classnameToUse = formToUse.getFilename(); 
			classnameToUse = StringUtils.remove(classnameToUse, ".json"); 
			classnameToUse = StringUtils.removeEndIgnoreCase(classnameToUse, ".groovy"); 
			String upperstr = classnameToUse.toUpperCase();
			String camelstr = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, upperstr);
			formToUse.setClassName(camelstr);
			removeEmptyStringFormRoles(formToUse);
			setContentIfExist(formToUse);
			
		}
		return formToUse;
    } 
	
	private void setContentIfExist( GroovyForm formToUse ) {
		File root = new File(formToUse.getLocation());
		File file = new File(root, formToUse.getFilename());
		if( file.exists() ) {
			try {
				formToUse.setContent(FileUtils.readFileToString(file, ServletUtils.DEFAULT_HTML_ENCODING ));
				formToUse.setExist(true);
			} catch (IOException e) {
				log.error("Fail to read from file", e);
			}
		}
	}
	
	private void removeEmptyStringFormRoles(GroovyForm formToUse ) {
		List<String> list = new ArrayList<String>();
		for( String name : formToUse.getRequiredRoles()) {
			if(StringUtils.isNotBlank(name))
				list.add(name);
		}
		formToUse.setRequiredRoles(list);
	}
	 
	private void setDependencies(GroovyForm formToUse ) {
		List<String> list = new ArrayList<String>();
		
		if( formToUse.isSetMultipart() ) {
			list.add( org.springframework.web.multipart.MultipartHttpServletRequest.class.getName() );
		}
		if(formToUse.isSetUser())
		{
			
		}
		
		for( ClassInfo item : formToUse.getServices()) {
			for( String dependency : item.getDependencies() ) {
				if( !StringUtils.contains(list.toString(), dependency) )
					list.add( dependency );
			}
		} 
		formToUse.setDependencies(list); 
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/scripts/0/create.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public GroovyForm getGroovyContent(
    	@RequestBody GroovyForm data,
    	NativeWebRequest request) throws NotFoundException, IOException {  
 		GroovyForm formToUse = data; 
 		
 		removeEmptyStringFormRoles(formToUse);
 		setDependencies(formToUse);
 		
 		GroovyClassBuilder builder = new GroovyClassBuilder();
 		builder.setRepository(repository);
 		StringWriter writer = new StringWriter();
 		try {
			builder.build(formToUse, writer);
			
			formToUse.setContent(writer.toString());
			formToUse.setExist(true);
			
		} catch (Exception e) {
			log.error( e.getMessage(), e);
		}
		return formToUse;
    }  

	/**
	 * 파일 내용을 업데이트 한다. 동일경로에 파일이름 + .[yyyyMMddHHmmss] 형식으로 백업을 생성한 다음 저장한다.
	 * 
	 * @param type template or script 
	 * @param file 파일 경로 및 내용을 포함하는 객제 
	 * @param request
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/scripts/0/save-or-update.json", method = { RequestMethod.POST })
    @ResponseBody
    public Result  saveOrUpdate(
    		@RequestBody GroovyForm data,
    		@RequestParam(value = "backup", defaultValue = "true", required = false) Boolean doBackup, 
    		NativeWebRequest request) throws NotFoundException, IOException { 
		Result result = Result.newResult();
		GroovyForm formToUse = data;
		File root = new File(formToUse.getLocation());
		File file = new File(root, formToUse.getFilename());
		if( file.exists() ) {
			if( doBackup ) {
				File backup = new File(file.getParentFile() , file.getName() + "." + DateUtils.toString(new Date()) ); 
				FileUtils.copyFile(file, backup);
				result.getData().put("backup", backup.getName());
			}
		}else {
			file.createNewFile();
		}
		
		
		FileUtils.writeStringToFile(file, formToUse.getContent(), ServletUtils.DEFAULT_HTML_ENCODING , false);  
		
		return result;
    } 
	
}
