package architecture.community.content.bundles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import architecture.community.exception.NotFoundException;

public interface BundleService {
	
	public Asset getAsset(long assetId) throws NotFoundException ;
	
	public Asset createAsset (int objectType, long objectId, String filename, String description, InputStream file ) throws IOException ;
	
	public void saveAndExtract(Asset asset) throws IOException ;
	
	public void saveOrUpdate(Asset asset);

	public File getExtractBundleFile(Asset asset);
	
	public void remove(Asset asset) throws IOException;
	
	public void extract(Asset asset) throws IOException;
}
