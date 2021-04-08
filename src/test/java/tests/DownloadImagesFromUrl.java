package tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.query.CustomQueryService;
import architecture.community.services.HttpClientService;
import architecture.community.services.support.PooledHttpClientSupport.ResponseCallBack;
import architecture.community.streams.StreamMessage;
import architecture.community.streams.StreamThread;
import architecture.community.streams.Streams;
import architecture.community.streams.StreamsService;
import architecture.community.user.User;
import architecture.community.user.UserTemplate;
import architecture.community.web.spring.controller.annotation.ScriptData;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;

public class DownloadImagesFromUrl {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;

	@Inject
	@Qualifier("communityHttpClientService")
	private HttpClientService communityHttpClientService;

	@Autowired
	@Qualifier("configService")
	private ConfigService configService;

	@Inject
	@Qualifier("repository")
	private Repository repository;

	@Autowired(required = false)
	@Qualifier("streamsService")
	private StreamsService streamsService;

	@Autowired
	@Qualifier("imageService")
	private ImageService imageService;

	static class Response {
		Document document;
		int statusCode;

		public Response(int statusCode) {
			this.statusCode = statusCode;
			this.document = null;
		}

		public Response(Document document, int statusCode) {
			this.document = document;
			this.statusCode = statusCode;
		}

	}

	@ScriptData
	public Object download(NativeWebRequest request) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 31; i <= 40; i++) {

			String serviceUrl = String.format("https://mangahentai.me/manga-hentai/lucky-guy/chapter-%s/", i);
			String downloadDir = String.format("/lucky-guy/chapter-%s", i);

			Response res = communityHttpClientService.get(serviceUrl, null, new ResponseCallBack<Response>() {
				public Response process(int statusCode, String responseString) {
					Response res = new Response(statusCode);
					res.document = Jsoup.parse(responseString);
					return res;
				}
			});

			if (res.statusCode == 200) {
				Document doc = res.document;
				sb.append(doc.title()).append(File.separator);

				File dir = getDownloadDir(downloadDir);
				log.debug("download {} images to {}", doc.title(), dir);

				// User user = SecurityHelper.getUser();
				User user = new UserTemplate(1L);
				Streams streams = streamsService.getStreamsById(5);
				StringBuilder body = new StringBuilder();

				Elements eles = doc.select(".reading-content .page-break img");
				for (Element ele : eles) {
					String source = ele.attr("src").trim();
					String filename = FilenameUtils.getName(source);
					try {
						URL url = new URL(source);
						File file = new File(dir, filename);
						log.debug("{} > {}", url, file);
						downloadFromUrl(url, file);
						ImageLink link = uploadImage(Models.STREAMS.getObjectType(), streams.getStreamId(), file, user);
						body.append("<figure class='image'>");
						body.append("<img src='/download/images/");
						body.append(link.getLinkId());
						body.append("'>");
						body.append("</figure>");

					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				StreamMessage rootMessage = streamsService.createMessage(Models.STREAMS.getObjectType(),
						streams.getStreamId(), user);
				rootMessage.setSubject(doc.title());
				rootMessage.setBody(body.toString());
				StreamThread thread = streamsService.createThread(rootMessage.getObjectType(), rootMessage.getObjectId(), rootMessage);
				streamsService.addThread(rootMessage.getObjectType(), rootMessage.getObjectId(), thread);
			}

		}

		// return rootMessage.getBody() ;
		return sb.toString();
	}

	protected synchronized File getDownloadDir(String prefix) {
		File downloadDir = repository.getFile("download");
		if (!StringUtils.isEmpty(prefix)) {
			downloadDir = new File(downloadDir, prefix);
		}
		if (!downloadDir.exists()) {
			downloadDir.mkdir();
		}
		return downloadDir;
	}

	private ImageLink uploadImage(int objectType, long objectId, File file, User user) throws Exception {
		Image image = imageService.createImage(objectType, objectId, file.getName(), getContentType(file), file);
		image.setUser(user);
		imageService.saveImage(image);
		imageService.getImageLink(image, true);
		ImageLink link = imageService.getImageLink(image);
		return link;
	}

	/**
	 * read file from url.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private void downloadFromUrl(URL url, File file) throws Exception {
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
