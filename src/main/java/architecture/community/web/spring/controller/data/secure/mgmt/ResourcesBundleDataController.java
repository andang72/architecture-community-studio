package architecture.community.web.spring.controller.data.secure.mgmt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import architecture.community.content.bundles.Asset;
import architecture.community.content.bundles.BundleService;
import architecture.community.content.bundles.DefaultAsset;
import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.query.CustomQueryService;
import architecture.community.user.User;
import architecture.community.util.DateUtils;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;
import architecture.community.web.model.Result;
import architecture.community.web.spring.controller.data.AbstractResourcesDataController.FileInfo;
import architecture.community.web.util.ServletUtils;
import architecture.studio.web.spring.controller.secure.mgmt.ResourcesAttachmentDataController;

@Controller("community-mgmt-resources-bundle-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class ResourcesBundleDataController {


	@Inject
	@Qualifier("bundleService")
	private BundleService bundleService;
	
	@Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	private Logger log = LoggerFactory.getLogger(ResourcesAttachmentDataController.class);
	
	/**
	 * BUNDLE UPLOAD API 
	******************************************/
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
    @RequestMapping(value = {"/bundles" , "/bundles/upload.json"}, method = RequestMethod.POST, consumes = {"multipart/form-data"} )
    @ResponseBody
    public List<Asset> upload ( 
    	@RequestParam(value = "objectType", defaultValue = "-1", required = false) Integer objectType,
    	@RequestParam(value = "objectId", defaultValue = "-1", required = false) Long objectId,
    	@RequestParam(value = "assetId", defaultValue = "-1", required = false) Long assetId,
    	@RequestParam(value = "description", required = false) String description,
    	MultipartHttpServletRequest request ) throws NotFoundException, IOException, UnAuthorizedException { 
		User user = SecurityHelper.getUser(); 
		List<Asset> list = new ArrayList<Asset>();
		Iterator<String> names = request.getFileNames();  
		while (names.hasNext()) {
		    String fileName = names.next();
		    MultipartFile mpf = request.getFile(fileName);
		    InputStream is = mpf.getInputStream();  
		    log.debug("upload - file:{}, size:{}, type:{} ", mpf.getOriginalFilename(), mpf.getSize() , mpf.getContentType() ); 
		    
		    Asset asset;
		    if( assetId > 0 ) {
		    	asset = bundleService.getAsset(assetId); 
		    	if( StringUtils.isNotBlank(description))
		    		((DefaultAsset)asset).setDescription(description);
		    	
		    	((DefaultAsset)asset).setFilename(mpf.getOriginalFilename());
		    	((DefaultAsset)asset).setInputStream(mpf.getInputStream());
		    }else {
		    	asset = bundleService.createAsset(objectType, objectId, mpf.getOriginalFilename(), description, is);
		    	asset.setCreator(user);
		    }  
		    asset.setFilesize((int)mpf.getSize());
		    bundleService.saveOrUpdate(asset);
		    list.add(asset);
		}			
		return list;
	}
	 
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = {"/bundles", "/bundles/list.json"}, method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ItemList getAssets(@RequestBody DataSourceRequest dataSourceRequest, NativeWebRequest request) {  
		dataSourceRequest.setStatement("COMMUNITY_RESOURCES.COUNT_ASSETS_BY_REQUEST");
		int totalCount = customQueryService.queryForObject(dataSourceRequest, Integer.class);
		dataSourceRequest.setStatement("COMMUNITY_RESOURCES.SELECT_ASSETS_IDS_BY_REQUEST"); 
		List<Long> IDs = customQueryService.list(dataSourceRequest, Long.class); 
		List<Asset> items = new ArrayList<Asset>(totalCount);
		for( Long assetId : IDs ) {
			try {
				Asset asset = bundleService.getAsset(assetId);
				items.add(asset); 
			} catch (NotFoundException e) {
			}
		} 
		return new ItemList(items, totalCount ); 
	}
	
	
	
	/**
	 * BUNDLE API 
	******************************************/
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/bundles/{assetId:[\\p{Digit}]+}", method = { RequestMethod.DELETE})
	@ResponseBody
	public Result removeAsset(
			@PathVariable Long assetId,   
			NativeWebRequest request) {  
		Result result = Result.newResult(); 
		
		try {
			log.debug("remove asset : {}", assetId );
			Asset asset = bundleService.getAsset(assetId);  
			bundleService.remove(asset);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.setError(e);
		} 
		return result;
	}
	
	
	/**
	 * BUNDLE INSIDE CONTENT API 
	******************************************/
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/bundles/{assetId:[\\p{Digit}]+}/files/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<FileInfo> getFiles(
			@PathVariable Long assetId, 
			@RequestParam(value = "path", defaultValue = "", required = false) String path,
			@RequestParam(value = "recursive", defaultValue = "true", required = false) Boolean recursive, 
			NativeWebRequest request)
		throws NotFoundException { 
		
		Asset asset = bundleService.getAsset(assetId);  
		List<FileInfo> list = new ArrayList<FileInfo>();   
		File bundle = bundleService.getExtractBundleFile(asset);
		if( !bundle.exists()) {
			try {
				bundleService.extract(asset);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		
		log.debug("file : {}", bundle.getPath()); 
		File fileToUse = bundle;
		if (StringUtils.isNotEmpty(path)) {
			fileToUse = new File(fileToUse, path);
		}  
		
		for (File file : fileToUse.listFiles() ) { 
			list.add(new FileInfo(bundle, file));
		} 
		return list;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/bundles/{assetId:[\\p{Digit}]+}/files/get.json", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public FileInfo getFile(
    		@PathVariable Long assetId,  
    		@RequestParam(value = "path", defaultValue = "", required = false) String path, 
    		NativeWebRequest request) throws NotFoundException, IOException {  
		Asset asset = bundleService.getAsset(assetId);  
		File bundle = bundleService.getExtractBundleFile(asset);
		File targetFile = new File( bundle , path );
		log.debug("target : {} ({})", targetFile.getAbsolutePath() , targetFile.isDirectory());
		FileInfo fileInfo = new FileInfo(targetFile); 
		fileInfo.setFileContent(targetFile.isDirectory() ? "" : FileUtils.readFileToString(targetFile, "UTF-8")); 
		return fileInfo;
    } 
	
	

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/bundles/{assetId:[\\p{Digit}]+}/files/download", method = { RequestMethod.POST, RequestMethod.GET })
    public void downloadContent(
    		@PathVariable Long assetId,  
    		@RequestParam(value = "path", defaultValue = "", required = false) String path, 
    		HttpServletRequest request, 
    		HttpServletResponse response) throws NotFoundException, IOException {  
		
		Asset asset = bundleService.getAsset(assetId);  
		File bundle = bundleService.getExtractBundleFile(asset);
		File targetFile = new File( bundle , path ); 
		
		long sizeOfFile = FileUtils.sizeOf(targetFile);
		InputStream input = FileUtils.openInputStream(targetFile);
		
		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setContentLength((int)sizeOfFile);
		response.setHeader("Content-Disposition", "attachment;filename=" + ServletUtils.getEncodedFileName(targetFile.getName()));
		IOUtils.copy(input, response.getOutputStream());
		response.flushBuffer();
		 
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
	@RequestMapping(value = "/bundles/{assetId:[\\p{Digit}]+}/files/save-or-update.json", method = { RequestMethod.POST })
    @ResponseBody
    public FileInfo saveOrUpdate(
    		@PathVariable Long assetId,  
    		@RequestParam(value = "create-new-file", defaultValue = "false", required = false) Boolean createNewFile,
    		@RequestParam(value = "backup", defaultValue = "true", required = false) Boolean backup, 
    		@RequestBody FileInfo file, 
    		NativeWebRequest request) throws NotFoundException, IOException {  
		
		 
		Asset asset = bundleService.getAsset(assetId);  
		File bundle = bundleService.getExtractBundleFile(asset);
		File targetFile = new File( bundle , file.getPath() ); 
		
		// backup to filename + .yyyymmddhhmmss .
		if( backup && targetFile.exists() ) {
			File backupFile = new File(targetFile.getParentFile() , targetFile.getName() + "." + DateUtils.toString(new Date()) );  
			FileUtils.copyFile(targetFile, backupFile); 
		} 
		if( createNewFile && !targetFile.exists() && targetFile.canWrite() )
		{
			targetFile.createNewFile();
		} 
		FileUtils.writeStringToFile(targetFile, file.getFileContent(), ServletUtils.DEFAULT_HTML_ENCODING , false);  
		FileInfo fileInfo = new FileInfo(targetFile); 
		fileInfo.setFileContent(targetFile.isDirectory() ? "" : FileUtils.readFileToString(targetFile, "UTF-8"));  
		return fileInfo;
    } 	 
	 
	
}
