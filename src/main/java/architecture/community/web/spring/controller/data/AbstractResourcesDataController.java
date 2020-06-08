package architecture.community.web.spring.controller.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.ServletContextResourceLoader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.attachment.Attachment;
import architecture.community.attachment.AttachmentService;
import architecture.community.attachment.DefaultAttachment;
import architecture.community.exception.NotFoundException;
import architecture.community.image.DefaultImage;
import architecture.community.image.Image;
import architecture.community.image.ImageLink;
import architecture.community.image.ImageService;
import architecture.community.model.Models;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.share.SharedLink;
import architecture.community.share.SharedLinkService;
import architecture.community.tag.TagService;
import architecture.community.util.CommunityConstants;
import architecture.community.web.spring.controller.data.secure.mgmt.ResourceType;
import architecture.ee.service.ConfigService;

public abstract class AbstractResourcesDataController {

	protected static final String[] FILE_EXTENSIONS = new String[] { ".ftl", ".jsp", ".xml", ".html", ".groovy" };

	@Autowired
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired
	protected ResourceLoader loader;

	@Autowired
	private ServletContext servletContext;

	@Inject
	@Qualifier("attachmentService")
	private AttachmentService attachmentService;
	
	
	@Autowired(required = false) 
	@Qualifier("sharedLinkService")
	private SharedLinkService sharedLinkService;	
	
	@Autowired
	@Qualifier("imageService") 
	private ImageService imageService;
	
	@Autowired( required = false) 
	@Qualifier("tagService")
	private TagService tagService;
	
	protected ResourceLoader getResourceLoader() {
		if (loader == null)
			loader = new ServletContextResourceLoader(servletContext);
		return loader;
	}
	
    public String detectContentType(File file) {
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
    
    
    protected void setImageLink(Image image, boolean createIfNotExist) {
		try {
			ImageLink link = imageService.getImageLink(image, createIfNotExist);
			((DefaultImage)image).setImageLink( link );
		} catch (Exception ignore) { 
		}
    }
    
	protected File readFileFromUrl(URL url) throws Exception {
		// This will get input data from the server
		InputStream inputStream = null;
		try {
			// This user agent is for if the server wants real humans to visit
			String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
			// This socket type will allow to set user_agent
			URLConnection con = url.openConnection();
			// Setting the user agent
			con.setRequestProperty("User-Agent", USER_AGENT);
			// Requesting input data from server
			inputStream = con.getInputStream();
			File temp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
			FileUtils.copyToFile( inputStream, temp );
			return temp;
		}finally {
			IOUtils.closeQuietly(inputStream);
		}
	} 
	
	protected void deleteAttachment(Attachment attachment) throws NotFoundException {  
		attachmentService.removeAttachment(attachment);
		try {
			SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
			sharedLinkService.removeSharedLink(link.getLinkId());
		} catch (Exception e) { 
		}
	}
	
	protected List<Attachment> getAttachments (List<Long> attachmentIDs, boolean includeLink, boolean includeTags ){ 
		List<Attachment> attachments = new ArrayList<Attachment>();		
		for( Long id : attachmentIDs ) {
			try {
				Attachment attachment = attachmentService.getAttachment(id); 
				if( includeLink && sharedLinkService != null) {
					try {
						SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
						((DefaultAttachment) attachment ).setSharedLink(link);
					} catch (Exception ignore) {}	
				}
				
				if( includeTags && tagService!= null ) {
					String tags = tagService.getTagsAsString(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
					((DefaultAttachment)attachment).setTags( tags );
				} 
				attachments.add(attachment); 
			} catch (NotFoundException e) {
			}
		}
		return attachments;
	}
	
	protected List<Image> getImages (List<Long> imageIDs, boolean includeImageLink, boolean includeTags ){
		List<Image> images = new ArrayList<Image>(imageIDs.size());
		for( Long id : imageIDs ) {
			try {
				Image image = imageService.getImage(id);
				if( includeImageLink ) {
					setImageLink(image, false);
				}
				if( includeTags && tagService!= null ) {
					String tags = tagService.getTagsAsString(Models.IMAGE.getObjectType(), image.getImageId());
					((DefaultImage)image).setTags( tags );
				}
				images.add(image);
				
			} catch (NotFoundException e) {
			}
		}
		return images;
	}
	
	protected Attachment getAttachmentById(Long attachmentId)
			throws NotFoundException { 
		Attachment attachment = attachmentService.getAttachment(attachmentId);
		try {
			SharedLink link = sharedLinkService.getSharedLink(Models.ATTACHMENT.getObjectType(), attachment.getAttachmentId());
			((DefaultAttachment) attachment).setSharedLink(link);
		} catch (Exception ignore) {
		}
		return attachment;
	}

	protected String getResourcePathByType(ResourceType type) {
		String path = null;
		if (ResourceType.TEMPLATE == type) {
			StringBuilder sb = new StringBuilder().append(File.separatorChar).append("WEB-INF").append(File.separatorChar).append("template").append(File.separatorChar).append("ftl");
			path = configService.getApplicationProperty(CommunityConstants.VIEW_RENDER_FREEMARKER_TEMPLATE_LOCATION_PROP_NAME, sb.toString());
		} else if (ResourceType.SQL == type) {
			StringBuilder sb = new StringBuilder().append(File.separatorChar).append("WEB-INF").append(File.separatorChar).append("sql");
			path = configService.getApplicationProperty(CommunityConstants.RESOURCES_SQL_LOCATION_PROP_NAME,sb.toString());
		} else if (ResourceType.SCRIPT == type) {
			StringBuilder sb = new StringBuilder().append(File.separatorChar).append("WEB-INF").append(File.separatorChar).append("groovy-script");
			path = configService.getApplicationProperty(CommunityConstants.RESOURCES_GROOVY_LOCATION_PROP_NAME, sb.toString());
		} else if (ResourceType.JSP == type) {
			StringBuilder sb = new StringBuilder().append(File.separatorChar).append("WEB-INF").append(File.separatorChar).append("jsp");
			path = configService.getApplicationProperty(CommunityConstants.VIEW_RENDER_JSP_LOCATION_PROP_NAME, sb.toString());
		} else if (ResourceType.DECORATOR == type) {
			StringBuilder sb = new StringBuilder().append(File.separatorChar).append("decorators");
			path = configService.getApplicationProperty(CommunityConstants.RESOURCES_DECORATOR_LOCATION_PROP_NAME, sb.toString());
		}
		return path;
	}

	protected Resource getResourceByType(ResourceType type, String filename) {
		String path = getResourcePathByType(type);
		StringBuilder sb = new StringBuilder(path);
		if (StringUtils.isNotEmpty(filename)) {
			String filenameToUse = StringUtils.removeStart(filename, "/");
			filenameToUse = StringUtils.removeStart(filename, "\\");
			sb.append(File.separatorChar).append(filenameToUse);
		}
		return loader.getResource(sb.toString());
	}

	protected boolean isValid(String type) {
		if (ResourceType.valueOf(type.toUpperCase()) != null)
			return true;
		return false;
	}

	/**
	 * 
	 * @author donghyuck
	 *
	 */
	public static class FileInfo {

		private boolean directory;
		private String path;
		private String relativePath;
		private String absolutePath;
		private String name;
		private long size;
		private Date lastModifiedDate;
		private String fileContent;

		public FileInfo() {
			this.directory = false;
		}

		public FileInfo(File file) {
			this.lastModifiedDate = new Date(file.lastModified());
			this.name = file.getName();
			this.path = file.getPath();
			this.absolutePath = file.getAbsolutePath();
			this.directory = file.isDirectory();
			if (this.directory) {
				this.size = FileUtils.sizeOfDirectory(file);
			} else {
				this.size = FileUtils.sizeOf(file);
			}
		}

		public FileInfo(File root, File file) {
			this.lastModifiedDate = new Date(file.lastModified());
			this.name = file.getName();
			this.path = StringUtils.removeStart(file.getAbsolutePath(), root.getAbsolutePath());
			this.absolutePath = file.getAbsolutePath();
			this.directory = file.isDirectory();
			if (this.directory) {
				this.size = FileUtils.sizeOfDirectory(file);
			} else {
				this.size = FileUtils.sizeOf(file);
			}
		}

		/**
		 * @return fileContent
		 */
		public String getFileContent() {
			return fileContent;
		}

		/**
		 * @param fileContent
		 *            설정할 fileContent
		 */
		public void setFileContent(String fileContent) {
			this.fileContent = fileContent;
		}

		/**
		 * @return directory
		 */
		public boolean isDirectory() {
			return directory;
		}

		/**
		 * @param directory
		 *            설정할 directory
		 */
		public void setDirectory(boolean directory) {
			this.directory = directory;
		}

		/**
		 * @return path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @param path
		 *            설정할 path
		 */
		public void setPath(String path) {
			this.path = path;
		}

		/**
		 * @return absolutePath
		 */
		@JsonIgnore
		public String getAbsolutePath() {
			return absolutePath;
		}

		/**
		 * @param absolutePath
		 *            설정할 absolutePath
		 */
		public void setAbsolutePath(String absolutePath) {
			this.absolutePath = absolutePath;
		}

		/**
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            설정할 name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return size
		 */
		public long getSize() {
			return size;
		}

		/**
		 * @param size
		 *            설정할 size
		 */
		public void setSize(long size) {
			this.size = size;
		}

		/**
		 * @return lastModifiedDate
		 */
		@JsonSerialize(using = JsonDateSerializer.class)
		public Date getLastModifiedDate() {
			return lastModifiedDate;
		}

		/**
		 * @param lastModifiedDate
		 *            설정할 lastModifiedDate
		 */
		@JsonDeserialize(using = JsonDateDeserializer.class)
		public void setLastModifiedDate(Date lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}
	}
}
