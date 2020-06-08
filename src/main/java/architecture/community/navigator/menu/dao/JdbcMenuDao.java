package architecture.community.navigator.menu.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;

import architecture.community.model.Models;
import architecture.community.navigator.menu.Menu;
import architecture.community.navigator.menu.MenuItem;
import architecture.community.navigator.menu.MenuItemTreeWalker;
import architecture.community.navigator.menu.MenuNotFoundException;
import architecture.community.util.LongTree;
import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.jdbc.sequencer.SequencerFactory;
import architecture.ee.service.ConfigService;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;

public class JdbcMenuDao extends ExtendedJdbcDaoSupport implements MenuDao { 

	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	@Inject
	@Qualifier("sequencerFactory")
	private SequencerFactory sequencerFactory;

	@Inject
	@Qualifier("propertyDao")
	private PropertyDao propertyDao;

	private String menuPropertyTableName = "AC_UI_MENU_PROPERTY";
	private String menuPropertyPrimaryColumnName = "MENU_ID";
	
	private String menuItemPropertyTableName = "AC_UI_MENU_ITEM_PROPERTY";
	private String menuItemPropertyPrimaryColumnName = "MENU_ITEM_ID";
	
	private final RowMapper<Menu> menuMapper = new RowMapper<Menu>() {		
		public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {			
			Menu item = new Menu(rs.getLong("MENU_ID"));		
			item.setName(rs.getString("NAME"));
			item.setDescription(rs.getString("DESCRIPTION"));
			item.setCreationDate(rs.getDate("CREATION_DATE"));
			item.setModifiedDate(rs.getDate("MODIFIED_DATE"));		
			return item;
		}		
	};
	
	private final RowMapper<MenuItem> menuItemMapper = new RowMapper<MenuItem>() {		
		public MenuItem mapRow(ResultSet rs, int rowNum) throws SQLException {			
			MenuItem item = new MenuItem(rs.getLong("MENU_ITEM_ID"));		
			item.setMenuId(rs.getLong("MENU_ITEM_ID"));
			item.setParentMenuItemId(rs.getLong("PARENT_ID"));
			item.setName(rs.getString("NAME"));
			item.setPage(rs.getString("PAGE"));
			item.setLocation(rs.getString("LINK_URL"));
			item.setSortOrder(rs.getInt("SORT_ORDER"));
			item.setDescription(rs.getString("DESCRIPTION"));
			item.setRoles(rs.getString("ROLES"));
			item.setCreationDate(rs.getDate("CREATION_DATE"));
			item.setModifiedDate(rs.getDate("MODIFIED_DATE"));		
			return item;
		}		
	};
	
	public JdbcMenuDao() {
	} 

	public String getMenuPropertyTableName() {
		return menuPropertyTableName;
	} 

	public void setMenuPropertyTableName(String menuPropertyTableName) {
		this.menuPropertyTableName = menuPropertyTableName;
	} 

	public String getMenuPropertyPrimaryColumnName() {
		return menuPropertyPrimaryColumnName;
	}


	public void setMenuPropertyPrimaryColumnName(String menuPropertyPrimaryColumnName) {
		this.menuPropertyPrimaryColumnName = menuPropertyPrimaryColumnName;
	}


	public String getMenuItemPropertyTableName() {
		return menuItemPropertyTableName;
	}


	public void setMenuItemPropertyTableName(String menuItemPropertyTableName) {
		this.menuItemPropertyTableName = menuItemPropertyTableName;
	}


	public String getMenuItemPropertyPrimaryColumnName() {
		return menuItemPropertyPrimaryColumnName;
	}


	public void setMenuItemPropertyPrimaryColumnName(String menuItemPropertyPrimaryColumnName) {
		this.menuItemPropertyPrimaryColumnName = menuItemPropertyPrimaryColumnName;
	}

	public Map<String, String> getMenuItemProperties(long menuItemId) {
		return propertyDao.getProperties(menuItemPropertyTableName, menuItemPropertyPrimaryColumnName, menuItemId);
	}

	public void deleteMenuItemProperties(long menuItemId) {
		propertyDao.deleteProperties(menuItemPropertyTableName, menuItemPropertyPrimaryColumnName, menuItemId);
	}
	
	public void setMenuItemProperties(long menuItemId, Map<String, String> props) {
		propertyDao.updateProperties(menuItemPropertyTableName, menuItemPropertyPrimaryColumnName, menuItemId, props);
	}
	

	public Map<String, String> getMenuProperties(long menuItemId) {
		return propertyDao.getProperties(menuItemPropertyTableName, menuItemPropertyPrimaryColumnName, menuItemId);
	}

	public void deleteMenuProperties(long menuItemId) {
		propertyDao.deleteProperties(menuItemPropertyTableName, menuItemPropertyPrimaryColumnName, menuItemId);
	}
	
	public void setMenuProperties(long menuItemId, Map<String, String> props) {
		propertyDao.updateProperties(menuItemPropertyTableName, menuItemPropertyPrimaryColumnName, menuItemId, props);
	}	
		
	public long getNextMenuItemId(){		
		return sequencerFactory.getNextValue(Models.MENU_ITEM.getObjectType(), Models.MENU_ITEM.name());
	}
 
	public long getNextMenuId(){		
		return sequencerFactory.getNextValue(Models.MENU.getObjectType(), Models.MENU.name());
	} 
 
	public void saveOrUpdate(Menu menu) {		
		if( menu.getMenuId() < 1) {
			
			// insert case 
			if (menu.getName() == null)
				throw new IllegalArgumentException();		

			if ("".equals(menu.getDescription()))
				menu.setDescription(null);
			menu.setMenuId(getNextMenuId());
			
			try {
				Date now = new Date();
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_MENU").getSql(),
						new SqlParameterValue(Types.NUMERIC, menu.getMenuId()),
						new SqlParameterValue(Types.VARCHAR, menu.getName()),
						new SqlParameterValue(Types.VARCHAR, menu.getDescription()),
						new SqlParameterValue(Types.TIMESTAMP, menu.getCreationDate() != null ? menu.getCreationDate() : now ),
						new SqlParameterValue(Types.TIMESTAMP, menu.getModifiedDate() != null ? menu.getModifiedDate() : now )
				);
				
				if( menu.getProperties().size() > 0)
					setMenuProperties(menu.getMenuId(), menu.getProperties());
				
			} catch (DataAccessException e) {
				//logger.error(CommunityLogLocalizer.getMessage("010013"), e);
				throw e;
			}					
			
		}else {
			// update case 
			Date now = new Date();
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_MENU").getSql(),
					new SqlParameterValue(Types.VARCHAR, menu.getName()),
					new SqlParameterValue(Types.VARCHAR, menu.getDescription()),
					new SqlParameterValue(Types.TIMESTAMP, menu.getModifiedDate() != null ? menu.getModifiedDate() : now ),
					new SqlParameterValue(Types.NUMERIC, menu.getMenuId()));
			
			deleteMenuProperties(menu.getMenuId());
			
			if( menu.getProperties().size() > 0)
			setMenuItemProperties(menu.getMenuId(), menu.getProperties());
			
		}

	}

	public List<Long> getMenuItemIds( long menuId ){
		return getExtendedJdbcTemplate().queryForList( getBoundSql("COMMUNITY_WEB.SELECT_MENU_ITEM_IDS_BY_MENU_ID_2").getSql(),  Long.class,  new SqlParameterValue(Types.NUMERIC, menuId));
	}
	public long getMenuIdByName(String name) throws MenuNotFoundException {
		return getExtendedJdbcTemplate().queryForObject( getBoundSql("COMMUNITY_WEB.SELECT_MENU_ID_BY_NAME").getSql(),  Long.class,  new SqlParameterValue(Types.VARCHAR, name));
	}
	
	public Menu getMenuById(long menuId) {
		Menu menu = getExtendedJdbcTemplate().queryForObject( getBoundSql("COMMUNITY_WEB.SELECT_MENU_BY_ID").getSql(),  menuMapper,  new SqlParameterValue(Types.NUMERIC, menuId));
		menu.setProperties(getMenuProperties(menuId));
		return menu;
	}	

	public List<MenuItem> getMenuItemsByMenuId(long menuId) {
		List<MenuItem> items = getExtendedJdbcTemplate().query(
			getBoundSql("COMMUNITY_WEB.SELECT_MENU_ITEMS_BY_MENU_ID").getSql(), 
			menuItemMapper,
			new SqlParameterValue(Types.NUMERIC, menuId )
		);
		
		for( MenuItem item : items )
			item.setProperties(getMenuProperties(item.getMenuItemId()));
		
		return items;
	}

	public MenuItemTreeWalker getTreeWalkerById(long menuId) {		
		int totalCount = getExtendedJdbcTemplate().queryForObject(getBoundSql("COMMUNITY_WEB.COUNT_MENU_ITEM_IDS_BY_MENU_ID").getSql(), Integer.class, new SqlParameterValue(Types.NUMERIC, menuId ));
		totalCount++;
		final LongTree tree = new LongTree(-1L, totalCount);
		getExtendedJdbcTemplate().query(getBoundSql("COMMUNITY_WEB.SELECT_MENU_ITEM_IDS_BY_MENU_ID").getSql(),
			new RowCallbackHandler() {
				public void processRow(ResultSet rs) throws SQLException {
					long parentId = rs.getLong(1);
					if( parentId < 1 )
						parentId = -1L;						
					long itemId = rs.getLong(2);
					tree.addChild(parentId, itemId);
				}
			}, new SqlParameterValue(Types.NUMERIC, menuId ));
		return new MenuItemTreeWalker(menuId, tree);
	}
	
	public List<Long> getAllMenuIds() {
		return getExtendedJdbcTemplate().queryForList( getBoundSql("COMMUNITY_WEB.SELECT_ALL_MENU_IDS").getSql(),  Long.class );
	}

	@Override
	public MenuItem getMenuItemById(long menuItemId) {
		MenuItem item = getExtendedJdbcTemplate().queryForObject( getBoundSql("COMMUNITY_WEB.SELECT_MENU_ITEM_BY_ID").getSql(),  menuItemMapper,  new SqlParameterValue(Types.NUMERIC, menuItemId));
		item.setProperties(getMenuProperties(menuItemId));
		return item ;
	}

	public void saveOrUpdate(MenuItem menu) {		
		if( menu.getMenuItemId() < 1) {
			
			// insert case 
			if (menu.getName() == null || menu.getMenuId() < 1)
				throw new IllegalArgumentException();		

			if ("".equals(menu.getDescription()))
				menu.setDescription(null);
			
			menu.setMenuItemId(getNextMenuItemId());
			
			try {
				Date now = new Date();
				getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.CREATE_MENU_ITEM").getSql(),
						new SqlParameterValue(Types.NUMERIC, menu.getMenuId()),
						new SqlParameterValue(Types.NUMERIC, menu.getParentMenuItemId()),
						new SqlParameterValue(Types.NUMERIC, menu.getMenuItemId()),
						new SqlParameterValue(Types.INTEGER, menu.getSortOrder()),
						new SqlParameterValue(Types.VARCHAR, menu.getName()),
						new SqlParameterValue(Types.VARCHAR, menu.getDescription()),
						new SqlParameterValue(Types.VARCHAR, menu.getPage()),
						new SqlParameterValue(Types.VARCHAR, menu.getLocation()),
						new SqlParameterValue(Types.VARCHAR, menu.getRoles()),
						new SqlParameterValue(Types.TIMESTAMP, menu.getCreationDate() != null ? menu.getCreationDate() : now ),
						new SqlParameterValue(Types.TIMESTAMP, menu.getModifiedDate() != null ? menu.getModifiedDate() : now )
				);
				
				if( menu.getProperties().size() > 0)
					setMenuItemProperties(menu.getMenuItemId(), menu.getProperties());
				
			} catch (DataAccessException e) {
				//logger.error(CommunityLogLocalizer.getMessage("010013"), e);
				throw e;
			}					
 
		}else {
			// update case 
			Date now = new Date();
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.UPDATE_MENU_ITEM").getSql(),
					new SqlParameterValue(Types.NUMERIC, menu.getParentMenuItemId()),
					new SqlParameterValue(Types.INTEGER, menu.getSortOrder()),
					new SqlParameterValue(Types.VARCHAR, menu.getName()),
					new SqlParameterValue(Types.VARCHAR, menu.getDescription()),
					new SqlParameterValue(Types.VARCHAR, menu.getPage()),
					new SqlParameterValue(Types.VARCHAR, menu.getLocation()),
					new SqlParameterValue(Types.VARCHAR, menu.getRoles()),
					new SqlParameterValue(Types.TIMESTAMP, menu.getModifiedDate() != null ? menu.getModifiedDate() : now ),
					new SqlParameterValue(Types.NUMERIC, menu.getMenuItemId()));
			
			deleteMenuItemProperties(menu.getMenuItemId());
			
			if( menu.getProperties().size() > 0)
				setMenuItemProperties(menu.getMenuItemId(), menu.getProperties());
		}

	}

 
	public void delete(MenuItem item) {
		if( item.getMenuItemId() > 0 ) {
			getExtendedJdbcTemplate().update(getBoundSql("COMMUNITY_WEB.DELETE_MENU_ITEM").getSql(), new SqlParameterValue(Types.NUMERIC, item.getMenuItemId() ));
			if( item.getProperties().size() > 0 )
				deleteMenuItemProperties(item.getMenuItemId());
		} 
	}
	
}
