package architecture.studio.web.spring.controller.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import architecture.community.exception.NotFoundException;
import architecture.community.exception.UnAuthorizedException;
import architecture.community.image.Image;
import architecture.community.image.ImageService;
import architecture.community.user.User;
import architecture.community.util.SecurityHelper;

@Controller("studio-chunk-upload-secure-data-controller")
@RequestMapping("/data/secure/")
public class ChunkUploadDataController {
    
    private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;


    private class FileResult
    {
        public boolean uploaded;
        public String fileUid;
    }

	/**
	 * Upload images by chunk...
     * This upload usefull big size file...
     * 
	 * @param objectType
	 * @param objectId
	 * @param imageId
	 * @param request
	 * @return
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws UnAuthorizedException
	 */
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER", "ROLE_USER"})
    @RequestMapping(value = "/images/{imageId:[\\p{Digit}]+}/files/chunk", method = { RequestMethod.POST, RequestMethod.PUT } )
    @ResponseBody
    public String upload(
    		@RequestParam(value = "objectType", defaultValue = "-1", required = false) Integer objectType,
    		@RequestParam(value = "objectId", defaultValue = "-1", required = false) Long objectId,
    		@PathVariable Long imageId, 
            String metadata ,
            MultipartHttpServletRequest request ) throws NotFoundException, IOException, UnAuthorizedException {

		User user = SecurityHelper.getUser();

        ObjectMapper mapper = new ObjectMapper();
        long totalChunks = 0;
        long chunkIndex = 0;
        String uploadUid = "";
        String fileName = ""; 

        if(metadata == null){
           return "";
        }

        JsonNode rootNode = mapper.readTree(metadata);
        totalChunks = rootNode.path("totalChunks").longValue();
        chunkIndex = rootNode.path("chunkIndex").longValue();
        uploadUid = rootNode.path("uploadUid").textValue();
        fileName = rootNode.path("fileName").textValue(); 
        
        log.debug("chunk total : {} , index : {} , uid : {}, name : {}", totalChunks, chunkIndex, uploadUid, fileName); 

        File serverFile = getFile(uploadUid,fileName);

        Iterator<String> names = request.getFileNames();		
		while (names.hasNext()) {
		    String name = names.next();
		    MultipartFile file = request.getFile(name);  
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(serverFile,true));  
            StreamUtils.copy(file.getBytes(), output);
            output.close();  
        } 

        FileResult fileBlob = new FileResult();
        fileBlob.uploaded = totalChunks - 1 <= chunkIndex;
        fileBlob.fileUid = uploadUid; 

        if(fileBlob.uploaded){
            Image imageToSave = imageService.createImage(objectType, objectId, fileName, getContentType(serverFile), serverFile);
            imageToSave.setUser(user);
            imageService.saveOrUpdate(imageToSave);
            FileUtils.deleteDirectory(serverFile.getParentFile());
        }
        return mapper.writeValueAsString(fileBlob);
    }


    protected String getContentType(File file) {
    	String contentType = null;
	    if (contentType == null) {
			Tika tika = new Tika();
			try {
			    contentType = tika.detect(file);
			} catch (IOException e) {
			    contentType = null;
			}
	    }
	    return contentType;
	}

    protected File getFile(String uploadUid, String fileName){
        File root = imageService.getImageTempDir();
        File dir = new File( root, uploadUid );
        if( !dir.exists() )
            dir.mkdirs(); 
        return new File(dir, fileName); 
    }
}
