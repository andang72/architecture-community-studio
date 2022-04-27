package tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.scale.AWTUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mp4Test {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	private static final String IMAGE_PNG_FORMAT = "png";

	@Test
	public void testThumbnails() throws Exception{
		File dir = new File("/Users/donghyuck.son/Downloads");
		File src = new File(dir , "onlyfans-sexy-bunny-2.mp4");
		File dst = new File(dir , "onlyfans-sexy-bunny-2.png");
		getThumbnail( src, dst );
	}
	/**
	 * 
	 * 
	 * @param source mp4 file.
	 * @param thumbnail
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public File getThumbnail(File source, File thumbnail) throws Exception {

		log.debug("extracting thumbnail from video %s", source );
		double frameNumber = 0.0;
		//FileChannelWrapper fcw = NIOUtils.readableFileChannel(source.getAbsolutePath());
		//FrameGrab grab = FrameGrab.createFrameGrab( fcw ); 
		Picture picture = FrameGrab.getFrameFromFile(source, (int) frameNumber);
		
		BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
		ImageIO.write(bufferedImage, IMAGE_PNG_FORMAT, thumbnail);
		return thumbnail; 
	}
}
