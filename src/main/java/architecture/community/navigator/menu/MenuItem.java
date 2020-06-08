package architecture.community.navigator.menu;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyAwareSupport;
import architecture.community.model.json.JsonDateSerializer;

public class MenuItem extends PropertyAwareSupport implements Serializable {
	
	private Long menuId;
	
	private Long menuItemId;
	
	private Long parentMenuItemId;
	
	private String name;
	
	private String page;
	
	private String description;
	
	private String location;
	
	private int sortOrder;
	
	private String roles;
	
	private Date creationDate;

	private Date modifiedDate;
	
	public MenuItem() {
		this.menuId = -1L;
		this.parentMenuItemId = -1L;
		this.menuItemId = -1L;
		this.sortOrder = 0;
		this.location = null;
		this.roles = null;
		this.page = null;
		this.creationDate = new Date();
		this.modifiedDate = creationDate;
	}

	public MenuItem(long menuItemId) {
		this.menuId = -1L;
		this.parentMenuItemId = -1L;
		this.menuItemId = menuItemId ;
		this.sortOrder = 0;
		this.location = null;
		this.roles = null;
		this.creationDate = new Date();
		this.modifiedDate = creationDate;
	}
	
	public boolean isSetPage() {
		if(StringUtils.isNotEmpty(page))
			return true;
		return false;		
	}
	
	public boolean isSetLocation() {
		if(StringUtils.isNotEmpty(location))
			return true;
		return false;		
	}
	
	public boolean isSetRoles() {
		if(StringUtils.isNotEmpty(roles))
			return true;
		return false;		
	}
	
	
	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public Long getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(Long menuItemId) {
		this.menuItemId = menuItemId;
	}

	public Long getParentMenuItemId() {
		return parentMenuItemId;
	}

	public void setParentMenuItemId(Long parentMenuItemId) {
		this.parentMenuItemId = parentMenuItemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	/**
	 * @return creationDate
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            설정할 creationDate
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return modifiedDate
	 */
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            설정할 modifiedDate
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
}
