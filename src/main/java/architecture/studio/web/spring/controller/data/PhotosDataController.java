package architecture.studio.web.spring.controller.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import architecture.community.image.ImageService;
import architecture.community.query.CustomQueryService;
import architecture.community.tag.TagService;

@Controller("studio-media-photos-data-controller")
@RequestMapping("/data/photos")
public class PhotosDataController {

    private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;

    @Autowired(required = false) 
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

    @Autowired( required = false) 
	@Qualifier("tagService")
	private TagService tagService;
    

	
	public void a(){}
}
