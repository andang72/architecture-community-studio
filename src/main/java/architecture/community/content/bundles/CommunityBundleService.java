package architecture.community.content.bundles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.attachment.Attachment;
import architecture.community.content.bundles.dao.BundleDao;
import architecture.community.exception.NotFoundException;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.util.SecurityHelper;
import architecture.ee.exception.RuntimeError;
import architecture.ee.service.Repository;

public class CommunityBundleService implements BundleService {

	@Inject
	@Qualifier("repository")
	private Repository repository;

	@Inject
	@Qualifier("userManager")
	private UserManager userManager;
	
	@Inject
	@Qualifier("bundleDao")
	private BundleDao bundleDao;
	
	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private ReentrantLock lock = new ReentrantLock();
	
	private File bundlesDir;
	
	private File cacheDir;
	
	private File extractDir;
	
	protected synchronized File getBundlesDir() { 
		if (bundlesDir == null) {
			bundlesDir = repository.getFile("bundles");
			if (!bundlesDir.exists()) {
				boolean result = bundlesDir.mkdir();
				if (!result)
					log.error((new StringBuilder()).append("Unable to create bundle directory: '").append(bundlesDir).append("'").toString());
				
				
			}
		}
		return bundlesDir;
	}
	
	protected File getBundleDir() {
		if( cacheDir == null ) {
			cacheDir = new File( getBundlesDir(), "cache");
			if (!cacheDir.exists()) {
				boolean result = cacheDir.mkdir();
				if (!result)
					log.error((new StringBuilder()).append("Unable to create bundle source cache directory: '").append(bundlesDir).append("'").toString());
			}
		}
		return cacheDir;
	}
	
	protected File getExtractBundleDir() {
		if( extractDir == null ) {
			extractDir = new File( getBundlesDir(), "extract");
			if (!extractDir.exists()) {
				boolean result = extractDir.mkdir();
				if (!result)
					log.error((new StringBuilder()).append("Unable to create bundle cache directory: '").append(bundlesDir).append("'").toString());
			}
		}
		return extractDir;
	}
	
	
	public Asset getAsset(long assetId) throws NotFoundException { 
		Asset asset = bundleDao.getAssetByAssetId(assetId);
		try {
			long creatroUserId = asset.getCreator().getUserId();
			if (creatroUserId > 0)
				asset.setCreator(userManager.getUser(creatroUserId));
			else
				asset.setCreator(SecurityHelper.ANONYMOUS);
		} catch (UserNotFoundException e) {}  
		return asset;
	}
	 

	public File getExtractBundleFile(Asset asset) {
		File outputDir = new File( getExtractBundleDir() , asset.getLinkId()); 
		return outputDir;
	}
	
	
	public File getBundleFile(Asset asset) throws IOException { 
		return getBundleFile(asset, true);
	}
	
	private File getBundleFile(Asset asset, boolean createIfNotExist) throws IOException { 
		File file = new File ( getBundleDir() , asset.getLinkId() );  
		if( asset.getAssetId() > 0 && !file.exists() && createIfNotExist ) {
			InputStream inputStream = bundleDao.getAssetData(asset); 
			FileUtils.copyInputStreamToFile(inputStream, file);
		}
		return file;
	}
	
	public InputStream getBundleInputStream(Asset asset) {
		try {
			File file = getBundleFile(asset);
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			throw new RuntimeError(e);
		}
	}
	
	
	public Asset createAsset(int objectType, long objectId, String filename, String description, InputStream inputStream) throws IOException { 
		DefaultAsset asset = new DefaultAsset(objectType, objectId);
		asset.setLinkId(RandomStringUtils.random(64, true, true));
		asset.setFilename(filename);
		if(StringUtils.isNotBlank(description))
			asset.setDescription(description);
		
		File bundle = getBundleFile(asset); 
		if( inputStream != null )
		try {  
			FileUtils.copyInputStreamToFile(inputStream, bundle);
			asset.setInputStream(FileUtils.openInputStream(bundle));
		} catch (Exception e) {
			if( bundle.exists())
				bundle.delete();
			throw new RuntimeError(e);
		}
		return asset;
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveAndExtract(Asset asset) throws IOException {   
		
		bundleDao.saveOrUpdate(asset);
		try { 
			if (asset.getInputStream() != null) {
				bundleDao.saveAssetData(asset, asset.getInputStream());
			}
		} catch (Exception e) {
			throw new RuntimeError(e);
		}
		
		File file = getBundleFile(asset); 
		if( file.exists() ) {
			File outputDir = getExtractBundleFile(asset); //new File( dir , asset.getLinkId()); 
			outputDir.mkdirs(); 
			extract(asset, outputDir); 
		} 
	}
	
	public void extract(Asset asset) throws IOException {
		File file = getBundleFile(asset); 
		if( file.exists() ) {
			File outputDir = getExtractBundleFile(asset); //new File( dir , asset.getLinkId()); 
			outputDir.mkdirs(); 
			extract(asset, outputDir); 
		} 
	}
	
	private void extract( Asset asset , File outputDir ) throws IOException {
		final byte[] buffer = new byte[1024]; 
		try (java.util.zip.ZipInputStream zis = new ZipInputStream(getBundleInputStream(asset))) { 
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
	            final File newFile = newFile(outputDir, zipEntry);
	            if (zipEntry.isDirectory()) {
	                if (!newFile.isDirectory() && !newFile.mkdirs()) {
	                    throw new IOException("Failed to create directory " + newFile);
	                }
	            } else {
	                File parent = newFile.getParentFile();
	                if (!parent.isDirectory() && !parent.mkdirs()) {
	                    throw new IOException("Failed to create directory " + parent);
	                } 
	                final FileOutputStream fos = new FileOutputStream(newFile);
	                int len;
	                while ((len = zis.read(buffer)) > 0) {
	                    fos.write(buffer, 0, len);
	                }
	                fos.close();
	            }
	            zipEntry = zis.getNextEntry();
	        }
		}	
	}

	private User getUser() {
		try {
			User user = SecurityHelper.getUser();
			return user;
		} catch (Exception ignore) {
		}
		return SecurityHelper.ANONYMOUS;
	}
	
	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        } 
        return destFile;
    }
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Asset asset) {  
		
		Date now = new Date();
		Asset assetToUse = asset;
		if (assetToUse.getAssetId() > 0) { 
			assetToUse.setModifiedDate(now);
			bundleDao.saveOrUpdate(assetToUse); 
		} else {
			User currentUser = getUser();
			if (assetToUse.getCreator() != null && assetToUse.getCreator().getUserId() != currentUser.getUserId()) {
				assetToUse.setCreator(currentUser);
			}
			assetToUse.setCreationDate(now);
			assetToUse.setModifiedDate(now);
			assetToUse = bundleDao.saveOrUpdate(assetToUse); 
		}
		
		try { 
			if (assetToUse.getInputStream() != null) {
				bundleDao.saveAssetData(assetToUse, assetToUse.getInputStream());
			}
		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}
	
	
	public void initialize() {
		log.debug("initializing bundle manager");
		getExtractBundleDir();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void remove(Asset asset) throws IOException { 
		
		Asset assetToUse = asset;
		if (assetToUse.getAssetId() > 0) { 
			bundleDao.deleteAsset(assetToUse);
			bundleDao.deleteAssetData(assetToUse);
		}  
		
		File bundle = getBundleFile(assetToUse, false);
		if(bundle.exists())
			FileUtils.deleteQuietly(bundle);
		
		File dir = getExtractBundleFile(asset); 
		if( dir.exists() )
			FileUtils.deleteDirectory(dir); 
		
	}
	
	public void destroy() {

	}
	
}
