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

package architecture.community.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import architecture.community.model.PropertyModelObjectAware;
import architecture.community.user.User;

public interface Image extends PropertyModelObjectAware {
	
	public static final String DEFAULT_THUMBNAIL_CONTENT_TYPE = "image/png";
	
	public abstract long getImageId(); 
	
	public abstract String getName();	

    public Date getCreationDate();

    public void setCreationDate(Date creationDate);

    public Date getModifiedDate();

    public void setModifiedDate(Date modifiedDate);
	
	
	/**
	 * 이미지 ContentType 값
	 * @return
	 */
	public abstract String getContentType();
	
	/**
	 * 이미지의 바이트 크기 값
	 * @return
	 */
	public abstract int getSize();
	
	public abstract void setSize(int size);
	
	/**
	 * 이미지 데이터를 InputStream 형태로 리턴한다.
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getInputStream() throws IOException ;	
	
	public abstract Integer getThumbnailSize();
	
	public abstract void setThumbnailSize(Integer thumbnailSize);
	
	public abstract void setThumbnailContentType(String contentType);
	
	public abstract String getThumbnailContentType();
	
	public abstract User getUser();
	
	public abstract void setUser(User user);
	
	public abstract ImageLink getImageLink();
	
}
