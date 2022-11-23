package tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.page.api.Api;
import architecture.community.exception.NotFoundException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.services.CommunityHttpClientService;
import architecture.community.services.HttpClientService;
import architecture.community.services.support.PooledHttpClientSupport.ResponseCallBack;
import architecture.community.streams.StreamMessage;
import architecture.community.streams.StreamThread;
import architecture.community.streams.Streams;
import architecture.community.streams.StreamsService;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.community.util.SecurityHelper;
import architecture.community.web.gateway.ApiAware;
import architecture.community.web.gateway.Utils;
import architecture.community.web.gateway.annotation.ScriptData;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

public class ImageTest {

    private Logger log = LoggerFactory.getLogger(getClass());
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
    CommunityHttpClientService communityHttpClientService = new CommunityHttpClientService();

    private File getRoot() {
        File root = new File("/Users/donghyuck.son/git/architecture-community-studio/WebContent/WEB-INF/download");
        return root;

    }

    @Test
    public void downloadTestFromNudegirlsalert() throws Exception {

        String serviceUrl = "https://nudegirlsalert.com/dzhili-nudity-and-romance/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", HttpClientService.USER_AGENT);
        Document doc = communityHttpClientService.get(serviceUrl, headers, null, new ResponseCallBack<Document>() {
            public Document process(int statusCode, String responseString) {
                return Jsoup.parse(responseString);
            }
        });

        File dir = new File(getRoot(), java.util.UUID.randomUUID().toString() );
        dir.mkdir(); 

        log.debug("extract download url '{}' to {}", doc.title(), dir);

        headers.put("Referer", serviceUrl);
        Elements eles = doc.select("article .gallery a > img");
        for (Element ele : eles) {
            String source = ele.attr("src").trim();
           // source = StringUtils.remove(source, "thumbs/th_");
           log.info(source);
        }
    }

 
    public void downloadTestFromNudeGals() throws Exception {

        String serviceUrl = "https://nude-gals.com/photoshoot.php?photoshoot_id=26172";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", HttpClientService.USER_AGENT);
        Document doc = communityHttpClientService.get(serviceUrl, headers, null, new ResponseCallBack<Document>() {
            public Document process(int statusCode, String responseString) {
                return Jsoup.parse(responseString);
            }
        });

        File dir = new File(getRoot(), java.util.UUID.randomUUID().toString() );
        dir.mkdir(); 

        log.debug("extract download url '{}' to {}", doc.title(), dir);

        headers.put("Referer", serviceUrl);
        Elements eles = doc.select(".main-content a>img.thumbnail");
        for (Element ele : eles) {
            String source = ele.attr("src").trim();
            source = StringUtils.remove(source, "thumbs/th_");
            source = "https://nude-gals.com/" + source ;
            String filename = FilenameUtils.getName(source);
            try {
                URL url = new URL(source);
                File file = new File(dir, filename);
                downloadFromUrl(headers, url, file);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
 
    public void downloadTest() throws Exception {

        boolean usingDateSrc = true;
        String site = "bonmanga";
        String title = "keep-this-a-secret-from-mom";
        Integer start = 1;
        Integer end = 10;

        log.debug("download site : {} , title : {} , {} - {}", site, title, start, end);

        String formatString = null;

        if (StringUtils.containsIgnoreCase(site, "mangacave")) {
            formatString = "https://mangacave.com/manga/%s/chapter-%s/";
        }

        if (StringUtils.containsIgnoreCase(site, "bonmanga")) {
            formatString = "https://bonmanga.com/manga/%s/chapter-%s/";
        }

        File root = new File("/Users/donghyuck.son/git/architecture-community-studio/WebContent/WEB-INF/download");

        for (int i = start; i <= end; i++) {
            String serviceUrl = String.format(formatString, title, i + "");
            Document doc = communityHttpClientService.get(serviceUrl, null, new ResponseCallBack<Document>() {
                public Document process(int statusCode, String responseString) {
                    Document _document = Jsoup.parse(responseString);
                    return _document;
                }
            });

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("User-Agent", USER_AGENT);
            headers.put("Referer", serviceUrl);

            File dir = new File(root, String.format("chapter-%s", i));
            dir.mkdir();

            Elements eles = doc.select(".reading-content .page-break img");

            for (Element ele : eles) {
                String source = "";
                if (usingDateSrc)
                    source = ele.attr("data-src").trim();
                else
                    source = ele.attr("src").trim();
                String filename = FilenameUtils.getName(source);
                try {
                    URL url = new URL(source);
                    File file = new File(dir, filename);
                    log.debug("{} > {}", url, file);
                    downloadFromUrl(url, file);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private void downloadFromUrl(Map<String, String> headers, URL url, File file) throws Exception {
        InputStream inputStream = null;
        try {
            URLConnection con = url.openConnection();

            for (String name : headers.keySet()) {
                con.setRequestProperty(name, headers.get(name));
            }

            inputStream = con.getInputStream();
            FileUtils.copyToFile(inputStream, file);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * read file from url.
     * 
     * @param url
     * @return
     * @throws IOException
     */
    private void downloadFromUrl(URL url, File file) throws Exception {
        log.debug("downloading {}", url);
        InputStream inputStream = null;
        try {
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);
            inputStream = con.getInputStream();
            FileUtils.copyToFile(inputStream, file);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private String getContentType(File file) {
        String contentType = null;
        if (contentType == null) {
            Tika tika = new Tika();
            try {
                contentType = tika.detect(file);
            } catch (IOException e) {
                contentType = null;
            }
        }
        return contentType;
    }
}