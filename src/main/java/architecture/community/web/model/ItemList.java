package architecture.community.web.model;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author donghyuck
 *
 */
public class ItemList {

	private List<?> items;
	private int totalCount;

	public ItemList() {
	    this.items = Collections.EMPTY_LIST;
	    this.totalCount = 0;
	}
	
	/**
	 * @param items
	 * @param totalCount
	 */

	public ItemList(List<?> items, int totalCount) {
	    this.items = items;
	    this.totalCount = totalCount;
	}

	/**
	 * @return items
	 */
	public List<?> getItems() {
	    return items;
	}

	/**
	 * @param items
	 *            설정할 items
	 */
	public void setItems(List<?> items) {
	    this.items = items;
	}

	/**
	 * @return totalCount
	 */
	public int getTotalCount() {
	    return totalCount;
	}

	/**
	 * @param totalCount
	 *            설정할 totalCount
	 */
	public void setTotalCount(int totalCount) {
	    this.totalCount = totalCount;
	}

}
