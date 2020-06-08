package architecture.community.user.dao;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import architecture.community.user.AvatarImage;
import architecture.community.user.AvatarImageNotFoundException;

public interface UserAvatarDao {
	
	public void removeAvatarImage(AvatarImage image);

    public void addAvatarImage(AvatarImage image, File file);
    
    public void addAvatarImage(AvatarImage image, InputStream is);

    public AvatarImage getAvatarImageById(Long profileImageId) throws AvatarImageNotFoundException;

    public Long getPrimaryAvatarImageByUser(Long userId) throws AvatarImageNotFoundException;

    public List<Long> getAvatarImageIds(Long userId);

    public Integer getAvatarImageCount(Long userId);

    public InputStream getInputStream(AvatarImage image);
    
}
