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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import architecture.community.model.PropertyModelObjectAware;
import architecture.community.user.User;

public interface Attachment extends PropertyModelObjectAware {
	
	public long getAttachmentId();

	public void setAttachmentId(long attachementId);
 
	public String getName();

	public void setName(String name);

	public int getSize();

	public void setSize(int size);

	public String getContentType();

	public void setContentType(String contentType);

	public Map<String, String> getProperties();

	public void setProperties(Map<String, String> properties);

	public int getDownloadCount();

	public void setDownloadCount(int downloadCount);
	
	public void setInputStream(InputStream inputStream) ;
	
	public InputStream getInputStream() throws IOException ;
	
    public abstract User getUser();

    public abstract void setUser(User user);

    public Date getCreationDate();

    public void setCreationDate(Date creationDate);

    public Date getModifiedDate();

    public void setModifiedDate(Date modifiedDate);
	
}
