package architecture.studio.service;

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.image.ImageService;
import architecture.ee.service.Repository;

public abstract class AbstractMediaEditorService implements MediaEditorService {

    @Inject
	@Qualifier("repository")
	private Repository repository;

    private File imageDir;

    protected Logger log = LoggerFactory.getLogger(getClass().getName());
	 
    protected synchronized File getImageDir() {
		if (imageDir == null) {
			imageDir = repository.getFile(ImageService.IMAGE_DIR);
			if (!imageDir.exists()) {
				boolean result = imageDir.mkdir();
				if (!result)
					log.error((new StringBuilder()).append("Unable to create image directory: '").append(imageDir).append("'").toString());
				getImageEffectDir();
			}
		}
		return imageDir;
	}

    public File getImageEffectDir() {
		File dir = new File(getImageDir(), IMAGE_EFFECT_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

}