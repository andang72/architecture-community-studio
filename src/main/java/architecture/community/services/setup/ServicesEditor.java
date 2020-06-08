package architecture.community.services.setup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import architecture.community.util.CommunityConstants;
import architecture.ee.util.xml.XmlProperties;

public class ServicesEditor {

	public static void createServicesConfigIfNotExist(File file) {
		try {
			if (!file.exists()) {
				ClassPathResource resource = new ClassPathResource(CommunityConstants.SERVICES_CONFIG_FILENAME);
				InputStream input = resource.getInputStream();
				FileUtils.copyInputStreamToFile(input, file);
			} 
		} catch (Exception e) {
		}
	}
	
	public static XmlProperties getServicesConfig(File file, boolean createIfNotExsit) throws IOException {
		if( createIfNotExsit )
			createServicesConfigIfNotExist(file);
		return new XmlProperties(file);
	}

	public static XmlProperties getServicesConfig(ServicesConfigCallback callback, File file, boolean createIfNotExsit) throws IOException {
		if( createIfNotExsit )
			createServicesConfigIfNotExist(file);
		XmlProperties properteis = new XmlProperties(file);
		callback.saveOrUpdate(properteis); 
		return properteis;
	}
	
	public static XmlProperties createServicesConfigIfNotExist(ServicesConfigCallback callback, File file ) throws IOException {
		boolean isNew = false;
		if (!file.exists()) {
			ClassPathResource resource = new ClassPathResource(CommunityConstants.SERVICES_CONFIG_FILENAME);
			InputStream input = resource.getInputStream();
			FileUtils.copyInputStreamToFile(input, file);
			isNew = true;
		} 
		XmlProperties properteis = new XmlProperties(file);
		if( isNew ) {
			callback.saveOrUpdate(properteis); 
		}
		return properteis;
	}
	
	public static interface ServicesConfigCallback {
		
		@Nullable
		public void saveOrUpdate ( XmlProperties properteis );
		
	}
}
