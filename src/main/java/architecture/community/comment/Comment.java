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

package architecture.community.comment;

import java.util.Date;

import architecture.community.model.PropertyModelObjectAware;
import architecture.community.user.User;

public interface Comment extends PropertyModelObjectAware {
	 
	public abstract Status getStatus();

    public abstract void setStatus(Comment.Status status);

    public abstract long getCommentId();

    public abstract long getParentObjectId();

    public abstract int getParentObjectType();
    
    public abstract long getParentCommentId();   

    public abstract User getUser();

    public abstract void setUser(User user);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getEmail();

    public abstract void setEmail(String email);

    public abstract String getURL();

    public abstract void setURL(String url);

    public abstract String getIPAddress();

    public abstract void setIPAddress(String address);

    public abstract boolean isAnonymous();

    public abstract String getBody();

    public abstract void setBody(String body);

    public abstract int getReplyCount ();
    
    public abstract Date getCreationDate();

    public abstract void setCreationDate(Date creationDate);

    public abstract Date getModifiedDate();

    public abstract void setModifiedDate(Date modifiedDate);
    
    
public enum Status {
		
		DRAFT("Draft", false, 1), 
		PUBLISHED("Published", true, 2), 
		SCHEDULED("Scheduled", false,	3), 
		AWAITING_MODERATION("Awaiting moderation", false, 4), 
		REJECTED("Rejected", false, 5), 
		ABUSE_HIDDEN("Abuse hidden", false, 6), 
		ABUSE_VISIBLE("Abuse visible", true, 7), 
		ARCHIVED("Archived", false, 8), 
		EXPIRED("Expired", false, 9), 
		PENDING_APPROVAL("Pending Approval", false, 10), 
		DELETED("Deleted", false, 11);

		private boolean isVisible;
		private String name;
		private int intValue;

		/**
		 * @param name
		 * @param isVisible
		 * @param intValue
		 */
		private Status(String name, boolean isVisible, int intValue) {
			this.name = name;
			this.isVisible = isVisible;
			this.intValue = intValue;
		}

		/**
		 * @return isVisible
		 */
		public boolean isVisible() {
			return isVisible;
		}

		/**
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return intValue
		 */
		public int getIntValue() {
			return intValue;
		}

		public static Status valueOf(Integer value) {
			if (value == null)
				return null;
			for (Status s : values()) {
				if (s.intValue == value)
					return s;
			}
			return null;
		}
	}
}
