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

package architecture.community.viewcount.dao;

import java.util.List;

import architecture.community.viewcount.ViewCountInfo;

public interface ViewCountDao {
	
	public int getViewCount(int entityType, long entityId);

    public void updateViewCounts(final List<ViewCountInfo> views);

    public void deleteViewCount(int entityType, long entityId);

    public void insertInitialViewCount(int entityType, long entityId, int count);
    
}
