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

package architecture.community.image.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import architecture.community.image.Image;
import architecture.community.image.ImageNotFoundException;
import architecture.community.image.LogoImage;

public interface ImageDao {

    public void addLogoImage(LogoImage logoImage, File file);

    public void addLogoImage(LogoImage logoImage, InputStream is);

    public void updateLogoImage(LogoImage logoImage, File file);

    public void updateLogoImage(LogoImage logoImage, InputStream is);

    public void removeLogoImage(LogoImage logoImage);

    public InputStream getInputStream(LogoImage logoImage) throws IOException;

    public Long getPrimaryLogoImageId(int objectType, long objectId) throws ImageNotFoundException;

    public LogoImage getLogoImageById(long logoId) throws ImageNotFoundException;

    public List<Long> getLogoImageIds(int objectType, long objectId);

    public int getLogoImageCount(int objectType, long objectId);
    
    
	public abstract Image createImage(Image image);
	
	public abstract Image updateImage(Image image);
	
	public abstract void deleteImage(Image image);
	
	public abstract InputStream getImageInputStream(Image image);
	
	public abstract void saveImageInputStream(Image image , InputStream inputStream);

	public abstract Image getImageById(long imageId);
    
}
