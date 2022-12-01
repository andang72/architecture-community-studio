package architecture.studio.services;

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;

import architecture.community.exception.NotFoundException;
import architecture.ee.service.Repository;

public class YoutubeService {

    @Inject
    @Qualifier("repository")
    private Repository repository;

    private com.github.kiulian.downloader.YoutubeDownloader downloader = new com.github.kiulian.downloader.YoutubeDownloader();

    protected Logger log = LoggerFactory.getLogger(getClass().getName());

    private File downloadDir;

    public void initialize() throws Exception {

        getYoutubeDownlaodDir();

    }

    public File downloadVideoFile(VideoInfo video) {
        Format format = video.bestAudioFormat();// video.bestVideoWithAudioFormat();
        Response<File> responseFile = downloader
                .downloadVideoFile(new RequestVideoFileDownload(format).saveTo(getYoutubeDownlaodDir()));
        return responseFile.data();
    }

    public VideoInfo getVideoInfo(String videoId) throws NotFoundException {
        Response<VideoInfo> response = downloader.getVideoInfo(
                new RequestVideoInfo(videoId));
        return response.data();
    }

    public File getYoutubeDownlaodDir() {
        File dir = new File(getDownloadDir(), "youtube");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    protected synchronized File getDownloadDir() {
        if (downloadDir == null) {
            downloadDir = repository.getFile("download");
            if (!downloadDir.exists()) {
                boolean result = downloadDir.mkdir();
                if (!result)
                    log.error((new StringBuilder()).append("Unable to create download directory: '").append(downloadDir)
                            .append("'").toString());

            }
        }
        return downloadDir;
    }

}
