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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAttachmentService {
	
	protected Logger log = LoggerFactory.getLogger(getClass().getName());
	
	protected ReentrantLock lock = new ReentrantLock();
	
	protected static final String IMAGE_PNG_FORMAT = "png";
	
	public AbstractAttachmentService() {
	}

	protected static String listToString(List<String> list)
    {
        StringBuilder sb = new StringBuilder();
        for( String element :  list ){
        	 sb.append(element).append(",");
        }
        return sb.toString();
    }

	protected static List<String> stringToList(String string)
    {
        List<String> list = new ArrayList<String>();
        if(string != null)
        {
            for(StringTokenizer tokens = new StringTokenizer(string, ","); tokens.hasMoreTokens(); list.add(tokens.nextToken()));
        }
        return list;
    }
	
}
