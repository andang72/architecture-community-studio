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

import java.util.Calendar;
import java.util.Date;

public class DefaultLogoImage extends DefaultImage implements LogoImage {

    private boolean primary;

    public DefaultLogoImage() {
    	super();
		this.primary = false;		
		Date now = Calendar.getInstance().getTime();
		this.setCreationDate(now);
		this.setModifiedDate(now);
    }

    public DefaultLogoImage(int objectType, long objectId, boolean primary) {
		super(objectType, objectId);
		Date now = Calendar.getInstance().getTime();
		this.primary = primary;
		this.setCreationDate(now);
		this.setModifiedDate(now);
	}

	/**
     * @param primary
     *            설정할 primary
     */
    public void setPrimary(boolean primary) {
	this.primary = primary;
    }

    public Boolean isPrimary() {
	return primary;
    }


}
