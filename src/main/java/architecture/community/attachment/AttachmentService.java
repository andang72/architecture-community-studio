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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import architecture.community.exception.NotFoundException;
import architecture.community.image.ThumbnailImage;

public interface AttachmentService {
	
	public abstract void refresh(Attachment attachment);
	
	public abstract Attachment getAttachment(long attachmentId) throws NotFoundException ;
	
	public abstract int getAttachmentCount(int objectType, long objectId);
	
	public abstract List<Attachment> getAttachments(int objectType, long objectId);
	
	public abstract List<Attachment> getAttachments(int objectType, long objectId, int startIndex, int maxResults );
	
	public abstract void move(int objectType, long objectId, int targetObjectType, long targetObjectId);
	
	public abstract Attachment createAttachment(int objectType, long objectId, String name, String contentType, File file);
	
	public abstract Attachment createAttachment(int objectType, long objectId, String name, String contentType, InputStream inputStream);	
	
	public abstract Attachment createAttachment(int objectType, long objectId, String name, String contentType, InputStream inputStream, int size);	
	
	public abstract Attachment saveAttachment( Attachment attachment );	

	public abstract void removeAttachment( Attachment attachment );
	
	public abstract InputStream getAttachmentInputStream(Attachment attachment);
	
	public abstract boolean hasThumbnail(Attachment attachment);
	
	public abstract InputStream getAttachmentThumbnailInputStream(Attachment image, ThumbnailImage thumbnail);
}
