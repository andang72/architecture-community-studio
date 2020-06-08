package architecture.community.tag;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;

public class DefaultContentTag implements ContentTag {
	
	private long id;
	private String name;
	private Date creationDate;
	private String filteredName;

	public DefaultContentTag() {

	}

	/**
	 * @param tagId
	 * @param name
	 * @param creationDate
	 */
	public DefaultContentTag(long tagId, String name, Date creationDate) {
		this.id = tagId;
		this.name = name;
		this.creationDate = creationDate;
	}

	/**
	 * @return id
	 */
	public long getTagId() {
		return id;
	}

	/**
	 * @param id
	 *            설정할 id
	 */
	public void setTagId(long id) {
		this.id = id;
	}

	public String getUnfilteredName() {
		return name;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            설정할 name
	 */
	public void setName(String name) {
		this.name = name;
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
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return filteredName
	 */
	public String getFilteredName() {
		return filteredName;
	}

	/**
	 * @param filteredName
	 *            설정할 filteredName
	 */
	public void setFilteredName(String filteredName) {
		this.filteredName = filteredName;
	}

	public String toString() {
		return (new StringBuilder()).append("[").append(id).append("] ").append(name).toString();
	}

}