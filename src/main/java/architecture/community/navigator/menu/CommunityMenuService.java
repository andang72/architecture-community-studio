package architecture.community.navigator.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import architecture.community.navigator.menu.dao.MenuDao;

public class CommunityMenuService implements MenuService {

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private com.google.common.cache.LoadingCache<Long, Menu> menuCache = null;
	
	private com.google.common.cache.LoadingCache<String, Long> menuIdCache = null;

	private com.google.common.cache.LoadingCache<Long, MenuItem> menuItemCache = null;
	
	private com.google.common.cache.LoadingCache<Long, MenuItemTreeWalker> treewalkerCache = null;
		
	@Inject
	@Qualifier("menuDao")
	private MenuDao menuDao;
	
	public CommunityMenuService() {
	} 
	
	@PostConstruct
	public void initialize(){		
		logger.debug("creating cache ...");		
		menuCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(30, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, Menu>(){			
					public Menu load(Long menuId) throws Exception {
						logger.debug("loading menu by {}", menuId);
						return menuDao.getMenuById(menuId);
				}}
			);
		
		menuIdCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(30, TimeUnit.MINUTES).build(		
				new CacheLoader<String, Long>(){			
					public Long load(String name) throws Exception {	
						logger.debug("loading menu by {}", name);
						return menuDao.getMenuIdByName(name);
				}}
		);
		menuItemCache = CacheBuilder.newBuilder().maximumSize(200).expireAfterAccess(30, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, MenuItem>(){			
					public MenuItem load(Long menuItemId) throws Exception {
						logger.debug("loading menu item by {}", menuItemId);
						return menuDao.getMenuItemById(menuItemId);
				}}
			);	
		treewalkerCache = CacheBuilder.newBuilder().maximumSize(50).expireAfterAccess(30, TimeUnit.MINUTES).build(		
				new CacheLoader<Long, MenuItemTreeWalker>(){			
					public MenuItemTreeWalker load(Long menuId) throws Exception {
						logger.debug("loading menu treewalker by {}", menuId);
						return getTreeWalkerById(menuId);
				}}
			);			
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Menu createMenu(String name, String description) throws MenuAlreadyExistsException {
		Menu menu = new Menu();
		menu.setName(name);
		menu.setDescription(description);		
		
		try {
			getMenuByName(name);
			throw new MenuAlreadyExistsException();
		} catch (MenuNotFoundException e) {}
		menuDao.saveOrUpdate(menu);		
		return menu;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdateMenu(Menu menu) {		
		menuDao.saveOrUpdate(menu);
		invalidateMenuCache(menu);
	}
 
	public void deleteMenu(Menu menu) {
		invalidateMenuCache(menu);
	} 

	public MenuItemTreeWalker getTreeWalker(String name) throws MenuNotFoundException {
		Menu menu = getMenuByName(name);
		try {
			return treewalkerCache.get(menu.getMenuId());
		} catch (ExecutionException e) {
			throw new MenuNotFoundException(e);
		}
	}
	
	protected MenuItemTreeWalker getTreeWalkerById(long menuId) throws MenuNotFoundException {
		MenuItemTreeWalker walker = menuDao.getTreeWalkerById(menuId);		
		List<Long> ids = menuDao.getMenuItemIds(menuId);
		Map<Long, MenuItem> list = new HashMap<Long, MenuItem>();
		for( Long id: ids )
			try {
				list.put(id, getMenuItemById(id));
			} catch (MenuItemNotFoundException e) {
			}
		walker.setCache(list);
		return walker;
	}
	
	public Menu getMenuByName(String name) throws MenuNotFoundException {
		Long menuId;
		try {
			menuId = menuIdCache.get(name);
		} catch (Throwable e) {
			throw new MenuNotFoundException(e);
		}		
		return getMenuById(menuId);
	}

	
	public Menu getMenuById(long menuId) throws MenuNotFoundException {
		try {			
			Menu menu = menuCache.get(menuId);	
			if( menuIdCache.getIfPresent(menu.getName()) == null ){
				logger.debug("put ID:{}, NAME:{} into menuIdcache. ", menu.getMenuId(), menu.getName());
				menuIdCache.put(menu.getName(), menu.getMenuId());
			}			
			return menu;
		} catch (ExecutionException e) {
			throw new MenuNotFoundException(e);
		}
	}

	public MenuItem getMenuItemById(long menuItemId) throws MenuItemNotFoundException {
		try {			
			MenuItem item = menuItemCache.get(menuItemId);			
			return item;
		} catch (ExecutionException e) {
			throw new MenuItemNotFoundException(e);
		}
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdateMenuItem(MenuItem menuItem) {		
		
		if( menuItem.getParentMenuItemId() == null )
		{
			menuItem.setParentMenuItemId(-1L);
		}
		
		logger.debug("invalidate cache for {}", menuItem.getMenuItemId());
		
		menuDao.saveOrUpdate(menuItem); 
		
		menuItemCache.invalidate(menuItem.getMenuItemId());
		treewalkerCache.invalidate(menuItem.getMenuId()); 
		menuItemCache.asMap().remove(menuItem.getMenuItemId());
		treewalkerCache.asMap().remove(menuItem.getMenuId()); 
	}
 
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMenuItem(MenuItem item) {
		menuDao.delete(item);
		menuItemCache.invalidate(item.getMenuItemId());
		treewalkerCache.invalidate(item.getMenuId()); 
	}
	
	public List<Menu> getAllMenus() {
		List<Menu> menus = new ArrayList<Menu>();
		List<Long> menuIds = menuDao.getAllMenuIds();
		for (long menuId : menuIds) {
		    try {
		    	menus.add(getMenuById(menuId));
		    } catch (MenuNotFoundException e) {
		    }
		}
		return menus;
	}
	
	private void invalidateMenuCache(Menu menu){		
		menuCache.invalidate(menu.getMenuId());
		treewalkerCache.invalidate(menu.getMenuId());
		menuIdCache.invalidate(menu.getName());
	}


	@Override
	public void refresh(Menu menu) throws MenuNotFoundException {
		invalidateMenuCache(menu);
	}
	
	public boolean hasMenu(String name) {
		try {
			getMenuByName(name);
			return true;
		} catch (MenuNotFoundException e) {
			return false;
		}
		
	}
}
