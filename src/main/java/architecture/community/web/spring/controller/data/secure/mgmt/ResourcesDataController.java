package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
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
import architecture.community.util.DateUtils;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.AbstractResourcesDataController;
import architecture.community.web.util.ServletUtils;
import architecture.ee.service.Repository;

@Controller("community-mgmt-resources-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class ResourcesDataController extends AbstractResourcesDataController {
	
	private Logger log = LoggerFactory.getLogger(getClass()); 

	
	@Autowired
	@Qualifier("repository")
	private Repository repository;

	public ResourcesDataController() {
		
	}  
	
	private ResourceType getResourceType(String name ) {
		return ResourceType.valueOf(name.toUpperCase());
	}

	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/resources/0/get.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public FileInfo getContentByPath(
    		@PathVariable String type, 
    		@RequestParam(value = "path", defaultValue = "", required = false) String path, 
    		NativeWebRequest request) throws NotFoundException, IOException {  
		 
		
		Resource resouce = loader.getResource(path); 
		File targetFile = resouce.getFile(); 
		FileInfo fileInfo = new FileInfo(targetFile); 
		fileInfo.setFileContent(targetFile.isDirectory() ? "" : FileUtils.readFileToString(targetFile, "UTF-8"));
		return fileInfo;
    } 	
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/resources/{type}/create.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result newFolder(
			@PathVariable String type, 
			@RequestParam(value = "path", defaultValue = "", required = false) String path,
			@RequestParam(value = "filename", defaultValue = "false", required = true) String filename,
			@RequestParam(value = "directory", defaultValue = "false", required = false) boolean directory,
			NativeWebRequest request)
		throws NotFoundException {
		
		Result result = Result.newResult() ;
		log.debug("create new folder for '{}', '{}', '{}'", type,  path, filename);
		if (!isValid(type)) {
			throw new IllegalArgumentException();
		} 
		
		ResourceType resourceType = getResourceType(type);
		Resource root = getResourceByType( resourceType , null); 
		 
		try { 
			File fileToUse = root.getFile(); 
			if (StringUtils.isEmpty(path)) { 
				fileToUse = root.getFile();
			}else { 
				fileToUse = new File(root.getFile(), path);
			}
			File newFile = new File( fileToUse, filename);
			if( directory )
				newFile.mkdir();
			else
				newFile.createNewFile();
			
		} catch (IOException e) {
			result.setError(e);
			log.error(e.getMessage());
		}
		
		return result;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/resources/{type}/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<FileInfo> getResources(
			@PathVariable String type, 
			@RequestParam(value = "path", defaultValue = "", required = false) String path, 
			@RequestParam(value = "recursive", defaultValue = "true", required = false) Boolean recursive, 
			NativeWebRequest request)
		throws NotFoundException {
		
		log.debug("get resources by type '{}' ({})", type,  path);
		if (!isValid(type)) {
			throw new IllegalArgumentException();
		} 
		ResourceType resourceType = getResourceType(type);
		Resource root = getResourceByType( resourceType , null); 
		List<FileInfo> list = new ArrayList<FileInfo>();  
		log.debug("selected resources : {}", root );
		try { 
			File fileToUse = root.getFile(); 
			if (StringUtils.isEmpty(path)) { 
				fileToUse = root.getFile();
			}else { 
				fileToUse = new File(root.getFile(), path);
			}
			
			File[] files = fileToUse.listFiles(new FileFilter() {  
					private IOCase caseSensitivity = IOCase.SENSITIVE; 
					public boolean accept(File file) {  
						final String name = file.getName();
						if( file.isDirectory() ) {
							if( name.startsWith("."))
								return false;
							return true;
						}
				        for (final String suffix : FILE_EXTENSIONS ) {
				            if (caseSensitivity.checkEndsWith(name, suffix)) {
				                return true;
				            }
				        }
				        return false;
					} 
			}); 
			for (File f : files ) { 
				list.add(new FileInfo(root.getFile(), f));
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return list;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/resources/{type}/get.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public FileInfo getContent(
    		@PathVariable String type, 
    		@RequestParam(value = "path", defaultValue = "", required = false) String path, 
    		NativeWebRequest request) throws NotFoundException, IOException {  
		
		ResourceType resourceType = getResourceType(type);
		File targetFile = getResourceByType(resourceType, path).getFile();
		FileInfo fileInfo = new FileInfo(targetFile); 
		fileInfo.setFileContent(targetFile.isDirectory() ? "" : FileUtils.readFileToString(targetFile, "UTF-8")); 
		return fileInfo;
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
	@RequestMapping(value = "/resources/{type}/save-or-update.json", method = { RequestMethod.POST })
    @ResponseBody
    public FileInfo saveOrUpdate(
    		@PathVariable String type, 
    		@RequestParam(value = "backup", defaultValue = "true", required = false) Boolean backup, 
    		@RequestBody FileInfo file, 
    		NativeWebRequest request) throws NotFoundException, IOException {  
		
		ResourceType resourceType = getResourceType(type);
		File target =  getResourceByType(resourceType, file.getPath() ).getFile();
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
	 


}
