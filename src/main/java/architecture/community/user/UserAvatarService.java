package architecture.community.user;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface UserAvatarService {

	public List<AvatarImage> getAvatarImages(User user);
	
	public Integer getAvatarImageCount(User user);

	public void addAvatarImage(AvatarImage image, InputStream is, User user);
	
    public void addAvatarImage(AvatarImage image, File file, User user);

    public void removeAvatarImage(AvatarImage image);

    public AvatarImage getAvatarImageById(Long profileImageId) throws AvatarImageNotFoundException;

    public AvatarImage getAvatarImage(User user) throws AvatarImageNotFoundException;

    public AvatarImage getAvatareImageByUsername(String username) throws AvatarImageNotFoundException, UserNotFoundException;

    public InputStream getImageInputStream(AvatarImage image);

    public InputStream getImageThumbnailInputStream(AvatarImage image, int width, int height);
    
}
