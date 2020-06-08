package tests;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.farng.mp3.MP3File;
import org.farng.mp3.filename.FilenameTag;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.FrameBodyAPIC;
import org.farng.mp3.id3.ID3v2_3Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mp3Test {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	public void extractImage (File mp3File, File thumbnail) {
		
		try {
			MP3File mp3file = new MP3File(mp3File);
			FilenameTag fileNameTag = mp3file.getFilenameTag();
			AbstractID3v2 id3v2 = mp3file.getID3v2Tag(); 
			log.debug("fileNameTag: '{}'", fileNameTag);
			Iterator iter = id3v2.getFrameIterator();
			while(iter.hasNext())
			{
				ID3v2_3Frame frame =  (ID3v2_3Frame)iter.next(); 
				log.debug("Frame: '{}' {}", frame.getIdentifier(), frame.getClass().getName());
				if( frame.getBody() instanceof FrameBodyAPIC ) { 
					FrameBodyAPIC apicBody = (FrameBodyAPIC) frame.getBody();
					Object bytes = apicBody.getObject("Picture Data");
					log.debug("Found APIC Frame.");
					log.debug( "encoding : {}, mine type : {}, image : {}" , apicBody.getObject("Text Encoding"), apicBody.getObject("MIME Type"), bytes );
					if( bytes!= null)
						FileUtils.writeByteArrayToFile(thumbnail, (byte[]) bytes);
					break;
				}else {
					continue;
				}
			} 
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		
	}
}
