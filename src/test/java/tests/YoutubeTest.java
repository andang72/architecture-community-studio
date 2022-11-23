package tests;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.subtitles.SubtitlesInfo;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations = { "classpath:youtube-subsystem-context.xml" })
public class YoutubeTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
	private YoutubeDownloader youtubeDownloader;
    	
	@BeforeClass
    public static void setupLogger() throws MalformedURLException
    {
     //   System.setProperty(ConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "log4j2.xml");
    }
    
    @Test
    public void youtubeDowmloadTest(){
        log.debug("=======================");
        String videoId = "8Acx4qioj64";
        //YoutubeDownloader downloader = new YoutubeDownloader();
        Response<VideoInfo> response = youtubeDownloader.getVideoInfo(
            new RequestVideoInfo(videoId)
        ); 
       
        VideoInfo video = response.data(); 
        log.debug("VideoInfo:{} (download : {})", video.details().description() , video.details().isDownloadable()  );

        List<SubtitlesInfo> subtitlesInfos = video.subtitlesInfo();
        for( SubtitlesInfo subtitlesInfo : subtitlesInfos)
            log.debug("Subtitle: {}, {}", subtitlesInfo.getLanguage(),  subtitlesInfo.getUrl());
       
 
        //Format format = video.findFormatByItag(18);    
        Format format = video.bestAudioFormat();//video.bestVideoWithAudioFormat();
        File outDir = new File("/Users/donghyuck.son/git/architecture-community-studio");
        Response<File> responseFile = youtubeDownloader.downloadVideoFile(new RequestVideoFileDownload(format).saveTo(outDir));

        
        
    }
}
