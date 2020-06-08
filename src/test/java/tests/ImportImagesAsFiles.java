package tests;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.image.Image;
import architecture.community.image.ImageService;
import architecture.community.query.CustomQueryService;
import architecture.community.query.dao.CustomQueryJdbcDao;
import architecture.community.services.database.CommunityExportService;
import architecture.community.services.database.ImageDef;
import architecture.community.web.spring.view.script.AbstractDataView;
import architecture.ee.util.StringUtils;

public class ImportImagesAsFiles extends AbstractDataView {

	@Inject
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Inject
	@Qualifier("communityExportService")
	private CommunityExportService communityExportService;

	@Inject
	@Qualifier("imageService")
	private ImageService imageService;
	
	public Object handle(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// using export service
		// List<ImageDef> list = getImages();
		//return list;
		importFromFiles();
		
		return "hello";
	}

	private void importFromFiles() {
		File file = new File("/Users/donghyuck/git/architecture-community-studio/WebContent/WEB-INF/exports/2");
		for(File f : file.listFiles())
		{
			log.debug(file.getName());
			Image image = imageService.createImage(40, 1, f.getName(), getContentType(f), f);
			imageService.saveOrUpdate(image);
		}
	}
	
	public List<ImageDef> getImages() throws Exception {
		// step 1 : get dao.
		CustomQueryJdbcDao dao = communityExportService.getCustomQueryJdbcDao("externalImageProviderPool");
		// step 2 : select images from .
		List<ImageDef> list = communityExportService.list(dao, "select image_id, file_name from v2_image where object_type = 31");
		for (ImageDef img : list) {
			InputStream input = communityExportService.getInputStream(dao, "select image_data from v2_image_data where image_id = ?", img);
			communityExportService.saveAsFile(img, input, null);
		}
		return list;
	}
	
	public String getContentType(File file) {
		StringBuilder sb = new StringBuilder("image");
		if(StringUtils.endsWithIgnoreCase(file.getName(), "jpeg")) {
			sb.append("/").append("jpeg");
		}else if (StringUtils.endsWithIgnoreCase(file.getName(), "gif")) {
			sb.append("/").append("gif");
		}else if (StringUtils.endsWithIgnoreCase(file.getName(), "png")) {
			sb.append("/").append("png");
		}else if (StringUtils.endsWithIgnoreCase(file.getName(), "bmp")) {
			sb.append("/").append("bmp");
		}
		return sb.toString();
	}

}
