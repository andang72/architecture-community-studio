package services.groovy.data;

import javax.inject.Inject

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.context.request.NativeWebRequest

import architecture.community.exception.NotFoundException
import architecture.community.image.ImageService
import architecture.community.query.CustomQueryService
import architecture.community.web.model.ItemList
import architecture.community.web.spring.controller.annotation.ScriptData

public class SelectWallpaperImages {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired(required = false)
	@Qualifier("imageService")
	private ImageService imageService;
	
	@Autowired(required = false)
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
		
	public SelectWallpaperImages() {
	
	}

	@ScriptData
	public ItemList select (NativeWebRequest request) throws NotFoundException {

		StringBuilder sql = new StringBuilder()		
		.append("SELECT * FROM AC_UI_IMAGE_LINK T1 RIGHT JOIN  AC_UI_IMAGE_PROPERTY T2")
		.append(" ON T1.IMAGE_ID = T2.IMAGE_ID ")
		.append(" WHERE T2.PROPERTY_NAME = 'wallpaper' ");
		
		// CASE 1 : select all 
		// List list = customQueryService.customQueryJdbcDao.jdbcTemplate.queryForList( sql.toString() );
		// CASE 2 : select 100 rows.
		
		List list = customQueryService.customQueryJdbcDao.extendedJdbcTemplate.query(sql.toString(), 0, 100);
        java.util.Collections.shuffle(list, new java.util.Random(System.nanoTime()));

		return new ItemList(list, list.size());
	}
}