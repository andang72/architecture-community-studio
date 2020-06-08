package architecture.community.navigator.menu.dao;

import java.util.List;

import architecture.community.navigator.menu.Menu;
import architecture.community.navigator.menu.MenuItem;
import architecture.community.navigator.menu.MenuItemTreeWalker;
import architecture.community.navigator.menu.MenuNotFoundException;

public interface MenuDao {
	
	public void saveOrUpdate(Menu menu);
	
	public void saveOrUpdate(MenuItem item);
	
	public long getMenuIdByName(String name) throws MenuNotFoundException ;
	
	public Menu getMenuById(long menuId);
	
	public List<Long> getAllMenuIds();
	
	public MenuItem getMenuItemById(long menuItemId);
	
	public List<MenuItem> getMenuItemsByMenuId(long menuId);
	
	public List<Long> getMenuItemIds( long menuId );
	
	public MenuItemTreeWalker getTreeWalkerById(long menuId) ;
	
	public void delete(MenuItem item);
	
}
