package architecture.studio.web.spring.controller.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;

import architecture.community.exception.NotFoundException;
import architecture.studio.services.YoutubeService;

@Controller("services-youtube-data-controller")
@RequestMapping("/data/youtube") 
public class YoutubeDataController {

	private Logger log = LoggerFactory.getLogger(getClass());



	@Autowired
	@Qualifier("youtubeService")
	private YoutubeService youtubeService;


	@RequestMapping(value = {"/{videoId}", "/{videoId}/info.json" }, method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public VideoInfo getVideoInfo(@PathVariable("videoId") String videoId,
			HttpServletRequest request, HttpServletResponse response) throws NotFoundException { 
		log.info("service : {}", youtubeService);

		VideoInfo videoInfo = youtubeService.getVideoInfo(videoId);
		
		
		log.info("video : {} : {}", videoId, videoInfo);
		return videoInfo;
	}

	@RequestMapping(value = {"/{videoId}/format:{format}" }, method = { RequestMethod.GET }) 
	@ResponseBody
	public ResponseEntity<InputStreamResource> download( @PathVariable("videoId") String videoId, @PathVariable("format") String format, 
		HttpServletRequest request, HttpServletResponse response) throws IOException, NotFoundException {    
		
			VideoInfo videoInfo = youtubeService.getVideoInfo(videoId);

			log.debug("Youtube video : {} will download with format : {} ", videoId, format);
			
			String formatToUse = StringUtils.defaultString(format, Format.AUDIO).toLowerCase();

			Optional<Format> targetFormat = Optional.empty();
			if( StringUtils.equals(formatToUse, Format.AUDIO))
			{
				targetFormat = Optional.of ( videoInfo.bestAudioFormat() );
			}else if (StringUtils.equals(formatToUse, Format.VIDEO) ) {
				targetFormat = Optional.of ( videoInfo.bestVideoFormat() );
			}else if (StringUtils.equals(formatToUse, Format.AUDIO_VIDEO) ) {
				targetFormat = Optional.of ( videoInfo.bestVideoWithAudioFormat() );
			}

			Format saveFormat = targetFormat.orElseThrow(() -> new IllegalArgumentException() );  
			File dir = youtubeService.getYoutubeDownlaodDir();
			File videoDir = new File( dir, videoInfo.details().videoId() );
			File saveTo = new File (videoDir, formatToUse ); 
			if(!saveTo.exists()){
				saveTo.mkdirs();
			}  
			
			String filename = getFileName( videoInfo.details().title(), saveFormat );
			log.debug("MineType : {} , name : {} ", saveFormat.mimeType(), filename );

			File saveFile = null;
			for( File file : FileUtils.listFiles(saveTo, new String[]{ saveFormat.extension().value() }, false)){
				log.debug("exist file : {} ", file.getAbsolutePath() );
				saveFile = file;
			}
			
			if( saveFile == null ){
				saveFile = youtubeService.downloadVideoFile( saveFormat, saveTo);   
			}

			int fileSize = (int) FileUtils.sizeOf(saveFile);
			InputStream is = new BufferedInputStream(new FileInputStream(saveFile));
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Transfer-Encoding", "binary") 
				.header("Content-Disposition",  "attachment; fileName=\"" + getURLEncodedFileName( filename )  +"\";" )
				.contentLength(fileSize)
				.body(new InputStreamResource(is));
	}
 
	private String getFileName( String title, Format format ){
		StringBuilder sb = new StringBuilder();
		sb.append(sanitizeFilename(title));
		sb.append(".").append(format.extension().value());
		return sb.toString();
	}


	private String sanitizeFilename(String inputName) {
		return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}

	private String getURLEncodedFileName(String name) {
		try {
			return URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return name;
		}
	}

}
