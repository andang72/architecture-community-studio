package architecture.community.navigator.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import architecture.community.util.LongTree;

public class MenuItemTreeWalker {
	
	private Long menuId;
	private LongTree tree;
	private Map<Long, MenuItem> cache;
	
	public MenuItemTreeWalker( Long menuId , LongTree tree ) { 
		this.menuId = menuId;
		this.tree = tree;
	}

	public Map<Long, MenuItem> getCache() {
		return cache;
	}

	public void setCache(Map<Long, MenuItem> cache) {
		this.cache = cache;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	protected LongTree getTree() {
		return tree;
	}
	
	public boolean isLeaf(long objectId) {
		return tree.isLeaf(objectId);
	}
	
	public int getChildCount(long objectId) {
		tree.getChildren(-1L);
		return tree.getChildCount(objectId);
	}
	
	public boolean isLeaf(MenuItem item) {
		return tree.isLeaf(item.getMenuItemId());
	}
	
	public int getChildCount(MenuItem item) {
		return tree.getChildCount(item.getMenuItemId());
	}
	
	
	public MenuItem getParent(MenuItem item) {
		long parentId = tree.getParent(item.getMenuItemId());
		if (parentId == -1L) {
			return null;
		} else {
			return cache.get(parentId);
		}
	}
	
	public List<MenuItem> getChildren() {
		long[] ids = tree.getChildren(-1L);
		int count = tree.getChildCount(-1L);
		List<MenuItem> list = new ArrayList<MenuItem>(count);
		for( long id : ids ) {
			list.add(cache.get(id));
		}
		return list;		
	}
	
	public List<MenuItem> getChildren(MenuItem item) {
		long[] ids = tree.getChildren(item.getMenuItemId());
		int count = tree.getChildCount(item.getMenuItemId());
		List<MenuItem> list = new ArrayList<MenuItem>(count);
		for( long id : ids ) {
			list.add(cache.get(id));
		}
		return list;	
	}
	
}
