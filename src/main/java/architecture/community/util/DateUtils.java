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

package architecture.community.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import architecture.ee.util.StringUtils;

public class DateUtils {

	public DateUtils() {
	}
	
    public static Date clone(Date date)
    {
        return date != null ? new Date(date.getTime()) : null;
    }
    

	/**
	 * Date format.
	 */
	public static final String FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mmZ";
	
	public static final String FORMAT_CUSTOM1 = "yyyyMMddHHmmss";

	/**
	 * Convert timestamp to {@link java.lang.String} representation in iso-8601 format.
	 *
	 * @param timestamp
	 *            timestamp
	 * @return {@link java.lang.String}
	 */
	public static String toISO8601String(final long timestamp) {
		return toISO8601String(new Date(timestamp));
	}

	/**
	 * Convert {@link java.util.Date} to {@link java.lang.String} representation in iso-8601 format.
	 *
	 * @param date
	 *            original {@link java.util.Date}
	 * @return {@link java.lang.String}
	 */
	public static String toISO8601String(final Date date) {
		if (date == null)
			return StringUtils.EMPTY;

		return new SimpleDateFormat(FORMAT_ISO_8601).format(date);
	}    

	
	public static String toString(final Date date) {
		if (date == null)
			return StringUtils.EMPTY; 
		return new SimpleDateFormat(FORMAT_CUSTOM1).format(date);
	} 
	
}
