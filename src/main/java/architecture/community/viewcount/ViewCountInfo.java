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

package architecture.community.viewcount;

public class ViewCountInfo {
	
	private int entityType;
	private long entityId;
	private int count;

	ViewCountInfo(int entityType, long entityId, int totalCount) {
	    count = 0;
	    this.entityType = entityType;
	    this.entityId = entityId;
	    count = totalCount;
	}


	public int getEntityType() {
		return entityType;
	}


	public long getEntityId() {
		return entityId;
	}


	public int getCount() {
	    return count;
	}

	public void incrementCount() {
	    count++;
	}

	public void incrementCount(int amount) {
	    count += amount;
	}

	public String toString() {
	    return (new StringBuilder()).append("ViewCountInfo(type: ").append(String.valueOf(entityType))
		    .append(", id: ").append(String.valueOf(entityId))
		    .append(", count: ").append(count).append(")").toString();
    }

}
