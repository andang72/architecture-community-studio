package architecture.community.navigator.menu;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyAwareSupport;
import architecture.community.model.json.JsonDateSerializer;

public class Menu extends PropertyAwareSupport implements Serializable {

	private Long menuId;

	private String name;

	private String description;

	private Date creationDate;

	private Date modifiedDate;
	
	public Menu() {
		this.menuId = -1L;
		this.name = null;
		this.description = null;
		this.creationDate = new Date();
		this.modifiedDate = creationDate;
	}

	public Menu(long menuId) {
		this.menuId = menuId;
		this.name = null;
		this.description = null;
		this.creationDate = new Date();
		this.modifiedDate = creationDate;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
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
