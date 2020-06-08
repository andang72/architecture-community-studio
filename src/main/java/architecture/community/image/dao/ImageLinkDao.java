package architecture.community.image.dao;

import architecture.community.image.Image;
import architecture.community.image.ImageLink;

public interface ImageLinkDao {
	
	public ImageLink getImageLinkByImageId(Long imageId);
	
	public ImageLink getImageLink(String linkId) ;
	
	public void saveImageLink(ImageLink link);
	
	public void removeImageLink(ImageLink link);
	
	public void removeImageLink(Image image);
	
	public void update( ImageLink link );
	
}
