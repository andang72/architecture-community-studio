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

import architecture.community.page.Page;

public interface ViewCountService {
	
	public abstract void addViewCount(Page page);

	public abstract int getViewCount(Page page);
	
	public abstract void clearCount(Page page);
	
	
	/**
	 * objectType , objectId 객체에 대한 뷰 카운트를 증가시킨다.
	 *  
	 * @param objectType
	 * @param objectId
	 */
	public abstract void addViewCount(int objectType, long objectId );

	
	/**
	 * objectType , objectId 객체에 대한 뷰 카운트 값을 리턴한다. 
	 *  
	 * @param objectType
	 * @param objectId
	 */
	public abstract int getViewCount(int objectType, long objectId );
	
	/**
	 * objectType , objectId 객체에 대한 뷰 카운트 값을 초기화 한다. (초기화 값: 0)
	 * 
	 * @param objectType
	 * @param objectId
	 */
	public abstract void clearCount(int objectType, long objectId );
	
	
	/**
	 * 메모리 상의 뷰 카운터 값을 데이터베이스에 반영한다.
	 * 이 함수는 스케줄러에의하여 주기적으로 실행된다.
	 */
	public abstract void updateViewCounts();
	
	/**
	 * 뷰 카운터 서비스가 활성 여부를 리턴한다.
	 * @return
	 */
	public abstract boolean isViewCountsEnabled() ;
}
