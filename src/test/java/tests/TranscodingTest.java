package tests;

import java.io.File;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.encode.enums.X264_PROFILE;
import ws.schild.jave.info.VideoSize;


public class TranscodingTest {

    private static Logger log = LoggerFactory.getLogger(TranscodingTest.class);

    @Test
    public void testGetSupportedEncodingFormats() throws Exception {
      System.out.println("getSupportedEncodingFormats");
      Encoder instance = new Encoder();
      String[] result = instance.getSupportedEncodingFormats();
      for( String format : result)
      System.out.println(format);
      assertTrue(result != null && result.length > 0, "No supported encoding formats found");
    }
    
    //@Test
    public void testTranscoding(){
        
        File source = new File("/Users/donghyuck.son/Downloads/IMG_5763.mov");
        File target = new File("/Users/donghyuck.son/Downloads/IMG_5763.mp4");

/*
        File source = new File("source.avi");
        FileOutputStream fileOutputStream = new FileOutputStream(source);
        byte[] bytes = video.getInputStream().readAllBytes();
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
*/
        //ACC
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");
        // here 64kbit/s is 64000
        audio.setBitRate(64000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        //H.264
        VideoAttributes video = new VideoAttributes();
        video.setCodec("h264");
        video.setX264Profile(X264_PROFILE.BASELINE);
        // Here 160 kbps video is 160000
        video.setBitRate(160000);
        // More the frames more quality and size, but keep it low based on devices like mobile
        video.setFrameRate(15);
        video.setSize(new VideoSize(720, 576));

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp4");
       // attrs.setFormat("mp4");
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);

        try {
            Encoder encoder = new Encoder();  
            encoder.encode( new MultimediaObject(source), target, attrs);
          } catch (Exception e) {  
             /*Handle here the video failure*/   
             e.printStackTrace();
          }

    }
}
