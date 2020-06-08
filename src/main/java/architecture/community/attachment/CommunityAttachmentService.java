/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.attachment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.farng.mp3.MP3File;
import org.farng.mp3.filename.FilenameTag;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.FrameBodyAPIC;
import org.farng.mp3.id3.ID3v2_3Frame;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.attachment.dao.AttachmentDao;
import architecture.community.exception.NotFoundException;
import architecture.community.image.ThumbnailImage;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.util.SecurityHelper;
import architecture.ee.exception.RuntimeError;
import architecture.ee.service.Repository;
import architecture.ee.util.StringUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class CommunityAttachmentService extends AbstractAttachmentService implements AttachmentService {

	@Inject
	@Qualifier("repository")
	private Repository repository;

	@Inject
	@Qualifier("userManager")
	private UserManager userManager;

	@Inject
	@Qualifier("attachmentDao")
	private AttachmentDao attachmentDao;

	@Inject
	@Qualifier("attachmentCache")
	private Cache attachmentCache;

	private File attachmentDir;

	private File attachmentCacheDir;

	public CommunityAttachmentService() {
	}

	protected synchronized File getAttachmentDir() {
		if (attachmentDir == null) {
			attachmentDir = repository.getFile("attachments");
			if (!attachmentDir.exists()) {
				boolean result = attachmentDir.mkdir();
				if (!result)
					log.error((new StringBuilder()).append("Unable to create attachment directory: '")
							.append(attachmentDir).append("'").toString());
			}
		}
		return attachmentDir;
	}

	public Attachment getAttachment(long attachmentId) throws NotFoundException {
		Attachment attachment = getAttachmentInCache(attachmentId);
		if (attachment == null) {
			attachment = attachmentDao.getByAttachmentId(attachmentId);
			try {
				long attachUserId = attachment.getUser().getUserId();
				if (attachUserId > 0)
					attachment.setUser(userManager.getUser(attachment.getUser().getUserId()));
				else
					attachment.setUser(SecurityHelper.ANONYMOUS);
			} catch (UserNotFoundException e) {

			}
			attachmentCache.put(new Element(attachmentId, attachment));
		}
		return attachment;
	}

	public List<Attachment> getAttachments(int objectType, long objectId) {
		List<Long> ids = attachmentDao.getAttachmentIds(objectType, objectId);
		List<Attachment> list = new ArrayList<Attachment>(ids.size());
		for (Long id : ids) {
			try {
				list.add(getAttachment(id));
			} catch (NotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}
		return list;
	}

	public List<Attachment> getAttachments(int objectType, long objectId, int startIndex, int maxResults) {
		List<Long> ids = attachmentDao.getAttachmentIds(objectType, objectId, startIndex, maxResults);
		List<Attachment> list = new ArrayList<Attachment>(ids.size());
		for (Long id : ids) {
			try {
				list.add(getAttachment(id));
			} catch (NotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}
		return list;
	}

	protected Attachment getAttachmentInCache(long attachmentId) {
		if (attachmentCache.get(attachmentId) != null && attachmentId > 0L)
			return (Attachment) attachmentCache.get(attachmentId).getObjectValue();
		else
			return null;
	}

	public Attachment createAttachment(int objectType, long objectId, String name, String contentType, File file) {

		DefaultAttachment attachment = new DefaultAttachment();
		attachment.setObjectType(objectType);
		attachment.setObjectId(objectId);
		attachment.setName(name);
		attachment.setContentType(contentType);
		attachment.setSize((int) FileUtils.sizeOf(file));
		try {
			attachment.setInputStream(FileUtils.openInputStream(file));
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		return attachment;
	}

	public Attachment createAttachment(int objectType, long objectId, String name, String contentType,
			InputStream inputStream) {

		DefaultAttachment attachment = new DefaultAttachment();
		attachment.setObjectType(objectType);
		attachment.setObjectId(objectId);
		attachment.setName(name);
		attachment.setContentType(contentType);
		attachment.setInputStream(inputStream);
		try {
			attachment.setSize(IOUtils.toByteArray(inputStream).length);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		return attachment;
	}

	public Attachment createAttachment(int objectType, long objectId, String name, String contentType,
			InputStream inputStream, int size) {

		DefaultAttachment attachment = new DefaultAttachment();
		attachment.setObjectType(objectType);
		attachment.setObjectId(objectId);
		attachment.setName(name);
		attachment.setContentType(contentType);
		attachment.setInputStream(inputStream);
		attachment.setSize(size);
		return attachment;
	}

	private User getUser() {
		try {
			User user = SecurityHelper.getUser();
			return user;
		} catch (Exception ignore) {
		}
		return SecurityHelper.ANONYMOUS;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Attachment saveAttachment(Attachment attachment) {

		Date now = new Date();
		Attachment attachmentToUse = attachment;
		if (attachmentToUse.getAttachmentId() > 0) {
			attachmentCache.remove(attachmentToUse.getAttachmentId());
			attachmentToUse.setModifiedDate(now);
			attachmentDao.updateAttachment(attachmentToUse);

			if (attachmentCache.get(attachmentToUse.getAttachmentId()) != null) {
				attachmentCache.remove(attachmentToUse.getAttachmentId());
			}
		} else {
			User currentUser = getUser();
			if (attachmentToUse.getUser() != null && attachmentToUse.getUser().getUserId() != currentUser.getUserId()) {
				attachmentToUse.setUser(currentUser);
			}
			attachmentToUse.setCreationDate(now);
			attachmentToUse.setModifiedDate(now);
			attachmentToUse = attachmentDao.createAttachment(attachmentToUse);
		}

		try {

			if (attachmentToUse.getInputStream() != null) {
				attachmentDao.saveAttachmentData(attachmentToUse, attachmentToUse.getInputStream());
				Collection<File> files = FileUtils.listFiles(getAttachmentCacheDir(),
						FileFilterUtils.prefixFileFilter(attachment.getAttachmentId() + ""), null);
				for (File file : files) {
					FileUtils.deleteQuietly(file);
				}
			}

			return getAttachment(attachment.getAttachmentId());

		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}

	public InputStream getAttachmentInputStream(Attachment attachment) {
		try {
			File file = getAttachmentFromCacheIfExist(attachment);
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			throw new RuntimeError(e);
		}
	}

	public void refresh(Attachment attachment) {

		File dir = getAttachmentCacheDir();
		StringBuilder sb = new StringBuilder();
		sb.append(attachment.getAttachmentId()).append(".bin");
		String cached = sb.toString();

		sb = new StringBuilder();
		sb.append(attachment.getAttachmentId()).append("_");
		String prefix = sb.toString();

		Collection<File> list = FileUtils.listFiles(dir, new IOFileFilter() {

			@Override
			public boolean accept(File file) {
				log.debug("check dir : {} , file : {} ", dir.getAbsolutePath(), file.getName());
				boolean accept = false;
				if (StringUtils.equals(file.getName(), cached))
					accept = true;
				if (StringUtils.startsWithIgnoreCase(file.getName(), prefix))
					accept = true;
				return accept;
			}

			@Override
			public boolean accept(File dir, String name) {
				log.debug("check dir : {} , file : {} ", dir.getAbsolutePath(), name);
				return false;
			}
		}, null);

		for (File file : list)
			FileUtils.deleteQuietly(file);

		attachmentCache.remove(attachment.getAttachmentId());
	}

	protected File getAttachmentFromCacheIfExist(Attachment attachment) throws IOException {

		File dir = getAttachmentCacheDir();

		StringBuilder sb = new StringBuilder();
		sb.append(attachment.getAttachmentId()).append(".bin");

		File file = new File(dir, sb.toString());
		if (file.exists()) {
			long size = FileUtils.sizeOf(file);
			if (size != attachment.getSize()) {
				// size different make cache new one....
				InputStream inputStream = attachmentDao.getAttachmentData(attachment);
				FileUtils.copyInputStreamToFile(inputStream, file);
			}
		} else {
			// doesn't exist, make new one ..
			InputStream inputStream = attachmentDao.getAttachmentData(attachment);
			FileUtils.copyInputStreamToFile(inputStream, file);
		}
		return file;
	}

	protected File getAttachmentCacheDir() {
		if (attachmentCacheDir == null) {
			attachmentCacheDir = new File(getAttachmentDir(), "cache");
			if (!attachmentCacheDir.exists()) {
				boolean result = attachmentCacheDir.mkdir();
				if (!result)
					log.error((new StringBuilder()).append("Unable to create attachment cache directory: '")
							.append(attachmentCacheDir).append("'").toString());
			}
		}
		return attachmentCacheDir;
	}

	public boolean hasThumbnail(Attachment attachment) {
		if (StringUtils.endsWithIgnoreCase(attachment.getContentType(), "pdf")
				|| StringUtils.endsWithIgnoreCase(attachment.getContentType(), "presentation")
				|| StringUtils.startsWithIgnoreCase(attachment.getContentType(), "video")
				|| StringUtils.startsWithIgnoreCase(attachment.getContentType(), "image")
				|| StringUtils.endsWithIgnoreCase(attachment.getContentType(), "mp3"))
			return true;
		return false;
	}

	public InputStream getAttachmentThumbnailInputStream(Attachment image, ThumbnailImage thumbnail) {
		try {
			File file = getThumbnailFromCacheIfExist(image, thumbnail);
			if( file == null )
				return null;
			return FileUtils.openInputStream(file);
		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}

	protected File getThumbnailFromCacheIfExist(Attachment attachment, ThumbnailImage thumbnail)
			throws IOException, InvalidFormatException, JCodecException {

		log.debug("thumbnail extracting for {} x {} ... ", thumbnail.getWidth(), thumbnail.getHeight());

		File dir = getAttachmentCacheDir();
		File attachmentFile = getAttachmentFromCacheIfExist(attachment);
		File thumbnailFile = new File(dir, toThumbnailFilename(attachment, thumbnail.getWidth(), thumbnail.getHeight())); 
		log.debug("attachment : {} ({}).", attachmentFile.getAbsoluteFile(), attachmentFile.length());
		log.debug("thumbnail : {}", thumbnailFile.getAbsoluteFile()); 
		if (thumbnailFile.exists() && thumbnailFile.length() > 0) {
			log.debug("thumbnail cache exist ({})", thumbnailFile.length());
			thumbnail.setSize(thumbnailFile.length());
			return thumbnailFile;
		} 
		lock.lock();
		try {

			log.debug("prepare for {}.", attachment.getContentType());
			if (StringUtils.endsWithIgnoreCase(attachment.getContentType(), "pdf")) {
				// PDF
				log.debug("extracting thumbnail from pdf");
				PDDocument document = PDDocument.load(attachmentFile);
				PDFRenderer pdfRenderer = new PDFRenderer(document);
				BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
				ImageIO.write(Thumbnails.of(image).size(thumbnail.getWidth(), thumbnail.getHeight()).asBufferedImage(),
						IMAGE_PNG_FORMAT, thumbnailFile);
				thumbnail.setSize(thumbnailFile.length());
				return thumbnailFile;
			} else if (attachment.getContentType().startsWith("video")) {
				log.debug("extracting thumbnail from video");
				Picture picture = FrameGrab.getFrameFromFile(attachmentFile, 0);
				// for JDK (jcodec-javase)
				BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
				ImageIO.write(bufferedImage, IMAGE_PNG_FORMAT, thumbnailFile);
				return thumbnailFile;
			} else if (StringUtils.endsWithIgnoreCase(attachment.getContentType(), "presentation")) {
				// PPT
				log.debug("extracting thumbnail from pptx");
				XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(attachmentFile));
				Dimension pgsize = ppt.getPageSize();
				log.debug("slide width x height : {}", pgsize.toString());
				// render
				java.util.List<XSLFSlide> slide = ppt.getSlides();
				log.debug("slide pages : {}", slide.size());
				BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = img.createGraphics();
				// clear the drawing area
				graphics.setPaint(Color.white);
				graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
				slide.get(0).draw(graphics);
				ImageIO.write(Thumbnails.of(img).size(thumbnail.getWidth(), thumbnail.getHeight()).asBufferedImage(), "png", thumbnailFile);
				log.debug("done.");
				return thumbnailFile;
			} else if (StringUtils.endsWithIgnoreCase(attachment.getContentType(), "mp3")) {
				// MP3
				log.debug("extracting thumbnail from mp3");
				try {
					MP3File mp3file = new MP3File(attachmentFile);
					FilenameTag fileNameTag = mp3file.getFilenameTag();
					AbstractID3v2 id3v2 = mp3file.getID3v2Tag();
					log.debug("fileNameTag: '{}'", fileNameTag);
					Iterator iter = id3v2.getFrameIterator();
					if( iter != null) {
						while (iter.hasNext()) {
							ID3v2_3Frame frame = (ID3v2_3Frame) iter.next();
							log.debug("Frame: '{}' {}", frame.getIdentifier(), frame.getClass().getName());
							if (frame.getBody() instanceof FrameBodyAPIC) {
								FrameBodyAPIC apicBody = (FrameBodyAPIC) frame.getBody();
								Object bytes = apicBody.getObject("Picture Data");
								log.debug("Found APIC Frame.");
								log.debug("encoding : {}, mine type : {}, image : {}", apicBody.getObject("Text Encoding"), apicBody.getObject("MIME Type"), bytes);
								if (bytes != null)
									FileUtils.writeByteArrayToFile(thumbnailFile, (byte[]) bytes);
								break;
							} else {
								continue;
							}
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			} else if (StringUtils.startsWithIgnoreCase(attachment.getContentType(), "image")) {
				// IMAGE
				BufferedImage originalImage = ImageIO.read(attachmentFile);
				if (originalImage.getHeight() < thumbnail.getHeight() || originalImage.getWidth() < thumbnail.getWidth()) {
					thumbnail.setSize(0);
					return attachmentFile;
				}
				BufferedImage image = Thumbnails.of(originalImage).size(thumbnail.getWidth(), thumbnail.getHeight()).asBufferedImage();
				ImageIO.write(image, "png", thumbnailFile);
				thumbnail.setSize(thumbnailFile.length());
				return thumbnailFile;
			}
		} finally {
			lock.unlock();
		}
		return null;
	}

	/**
	 * Return the file extension from the mime type (e.g. image/jpg will return jpg)
	 * 
	 * @param mimeType
	 * @return
	 */
	private String getFileExtensionFromMimeType(String mimeType) {
		String name = "unknown";
		if (mimeType != null) {
			int idx = mimeType.lastIndexOf('/');
			if (idx > 0)
				name = mimeType.substring(idx + 1, mimeType.length());
		}
		return name;
	}

	protected String toThumbnailFilename(Attachment image, int width, int height) {
		StringBuilder sb = new StringBuilder();
		sb.append(image.getAttachmentId()).append("_").append(width).append("_").append(height).append(".bin");
		return sb.toString();
	}

	public void initialize() {
		log.debug("initializing attachement manager");
		getAttachmentDir();
	}

	public void destroy() {

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeAttachment(Attachment attachment) {
		Attachment attachmentToUse = attachment;
		if (attachmentToUse.getAttachmentId() > 0) {
			attachmentCache.remove(attachmentToUse.getAttachmentId());
			attachmentDao.deleteAttachment(attachmentToUse);
			attachmentDao.deleteAttachmentData(attachmentToUse);
		}
	}

	public void move(int objectType, long objectId, int targetObjectType, long targetObjectId) {
		List<Long> ids = attachmentDao.getAttachmentIds(objectType, objectId);
		if (ids.size() > 0) {
			for (Long id : ids) {
				if (attachmentCache.get(id) != null)
					attachmentCache.remove(id);
			}
			attachmentDao.move(objectType, objectId, targetObjectType, targetObjectId);
		}
	}

	@Override
	public int getAttachmentCount(int objectType, long objectId) {
		return attachmentDao.getAttachmentCount(objectType, objectId);
	}

}
