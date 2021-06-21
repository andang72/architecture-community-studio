package tests;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;


public class ImageMetaTest {

	
	private Logger logger = LoggerFactory.getLogger(ImageMetaTest.class);
	
	@Test
	public void metaTest() throws Exception {

		File file = ResourceUtils.getFile("classpath:12d8ae2b583fd5.jpg");
		printMetadata(file);
		
		File file2 = ResourceUtils.getFile("classpath:64959cd222eb5c.jpg");
		printMetadata(file2);
		
		File file3 = ResourceUtils.getFile("classpath:12d24d14ffe937.jpg");
		printMetadata(file3);

	}
	
	private void printMetadata(File file) throws Exception  {
		Metadata metadata = ImageMetadataReader.readMetadata(file);
		
		logger.debug("START ---------------------------");
		ExifThumbnailDirectory tDir = metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);
		if( tDir != null )
		for (Tag tag : tDir.getTags()) {
		       // System.out.format("[%s] - %s = %s",
		       //     directory.getName(), tag.getTagName(), tag.getDescription());
		        
		        logger.debug("[{}] - {} = {}", tDir.getName(), tag.getTagName(), tag.getDescription() );
		}
		
		logger.debug("END ---------------------------");
		
		
		for (Directory directory : metadata.getDirectories()) {
		    for (Tag tag : directory.getTags()) {
		       // System.out.format("[%s] - %s = %s",
		       //     directory.getName(), tag.getTagName(), tag.getDescription());
		        
		        logger.debug("[{}] - {} = {}", directory.getName(), tag.getTagName(), tag.getDescription() );
		    }
		    if (directory.hasErrors()) {
		        for (String error : directory.getErrors()) {
		            System.err.format("ERROR: %s", error);
		        }
		    }
		}
	}
}
