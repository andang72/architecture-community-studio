package architecture.studio.web.spring.controller.secure;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import architecture.community.image.Image;
import architecture.community.image.ImageService;
import architecture.community.web.spring.controller.data.ResourceUtils;
import architecture.studio.service.ImageEffectsService;
import architecture.studio.service.VisionService;

@Controller("studio-vision-data-controller")
@RequestMapping("/data/images")
public class VisionDataController {
    
    private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;

    @Autowired( required = false)
	@Qualifier("imageEffectsService") 
	private ImageEffectsService imageEffectsService;

    @Autowired( required = false)
	@Qualifier("visionService") 
	private VisionService visionService;

    @Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/{imageId:[\\p{Digit}]+}/detect:{classifier}", method = { RequestMethod.GET })
	@ResponseBody
	public void face(
			@PathVariable("imageId") Long imageId,
			@PathVariable("classifier") String classifier,
			HttpServletRequest request, HttpServletResponse response) throws IOException { 
        try { 

			VisionService.Classifier classifierToUse = VisionService.Classifier.valueOf(classifier.toUpperCase());

            Image image = imageService.getImage(imageId);
            File source = imageService.getImageFile(image); 
            
            File dir = imageEffectsService.getImageAnnotationDir();
            StringBuilder sb = new StringBuilder();
            sb.append(image.getImageId()).append("_").append(classifierToUse.name().toLowerCase()).append(".jpeg");
            File target = new File( dir , sb.toString() ); 
            if( !target.exists() ) {
                visionService.detect(source, target, classifierToUse );
            }
			response.setContentType("image/jpeg");
			response.setContentLength((int)FileUtils.sizeOf(target)); 
			IOUtils.copy(target, response.getOutputStream());
			response.flushBuffer();
       } catch (Throwable e) {
			log.warn(e.getMessage(), e);
			response.setStatus(HttpStatus.NOT_FOUND.value());
			ResourceUtils.noThumbnails(request, response);
		}
	}

}
