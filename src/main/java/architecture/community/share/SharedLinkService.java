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

package architecture.community.share;

import architecture.community.exception.NotFoundException;

public interface SharedLinkService {
	
	public SharedLink getSharedLink(String linkId) throws NotFoundException;
	
	public SharedLink getSharedLink(int objectType, long objectId ) throws NotFoundException ;
	
	public SharedLink getSharedLink(int objectType, long objectId, boolean createIfNotExist ) throws NotFoundException ;
	
	public void removeSharedLink(String linkId);
	
	public void saveOrUpdate(SharedLink link);
	
}
