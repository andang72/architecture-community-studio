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

package architecture.community.attachment.dao;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import architecture.community.attachment.Attachment;
import architecture.community.util.DateUtils;

public interface AttachmentDao {
	
	public static class AttachmentDownloadItem
    {
        private long attachmentId;
        private Date downloadDate;
        private boolean downloadCompleted;

        public AttachmentDownloadItem(long attachmentId, Date downloadDate, boolean downloadCompleted)
        {
            this.attachmentId = attachmentId;
            this.downloadDate = DateUtils.clone(downloadDate);
            this.downloadCompleted = downloadCompleted;
        }
        
        public long getAttachmentId()
        {
            return attachmentId;
        }

        public Date getDownloadDate()
        {
            return DateUtils.clone(downloadDate);
        }

        public boolean isDownloadCompleted()
        {
            return downloadCompleted;
        }
    }    
	
    public abstract void insertAttachmentDownloads(List<AttachmentDownloadItem> list);

    public abstract List<Long> getAllAttachmentIds();

    public abstract void deleteAttachmentData();

    public abstract void deleteAttachmentData(Attachment attachment);
    
    public abstract Attachment createAttachment(Attachment attachment);

    public abstract void updateAttachment(Attachment attachment);

    public abstract void deleteAttachment(Attachment attachment);

    public abstract InputStream getAttachmentData(Attachment attachment);

    public abstract void saveAttachmentData(Attachment attachment, InputStream inputstream);

    public abstract Attachment getByAttachmentId(long attachmentId);

    public abstract List<Long> getAttachmentIds(int objectType, long objectId);
    
    public abstract List<Long> getAttachmentIds(int objectType, long objectId, int startIndex, int maxResults);
    
    public abstract int getAttachmentCount(int objectType, long objectId);
    
    public abstract void move(int objectType, long objectId, int targetObjectType, long targetObjectId);
    
    public abstract List<Attachment> getByObjectTypeAndObjectId(int objectType, long objectId);
}
