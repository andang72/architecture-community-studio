package architecture.community.page;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.json.JsonDateSerializer;

public class Parameter implements Serializable {

	private int objectType ;
	private long objectId ;
	
	private boolean isHeader ;
	private boolean isRequestParam ;
	private boolean isPathVariable;
	
	private String key ;
	private String value ;
	private String defaultValue ;
	
	private Date creationDate;
	private Date modifiedDate;
	
	public Parameter() {
		super(); 
		this.objectType = -1;
		this.objectId = -1L;
		this.isHeader = false;
		this.isRequestParam = true;
		this.isPathVariable = false;
		this.key = null;
		this.value = null;
		this.defaultValue = null;
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
	}


	public Parameter(int objectType, long objectId, boolean isHeader, boolean isRequestParam, boolean isPathVariable, String key, String value, String defaultValue) {
		super();
		this.objectType = objectType;
		this.objectId = objectId;
		this.isHeader = isHeader;
		this.isRequestParam = isRequestParam;
		this.isPathVariable = isPathVariable;
		this.key = key;
		this.value = value;
		this.defaultValue = defaultValue;
		this.creationDate = Calendar.getInstance().getTime();
		this.modifiedDate = creationDate;
	}
	
	
	public int getObjectType() {
		return objectType;
	}
	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public boolean isHeader() {
		return isHeader;
	}
	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}
	public boolean isRequestParam() {
		return isRequestParam;
	}
	public void setRequestParam(boolean isRequestParam) {
		this.isRequestParam = isRequestParam;
	}
 
	public boolean isPathVariable() {
		return isPathVariable;
	}


	public void setPathVariable(boolean isPathVariable) {
		this.isPathVariable = isPathVariable;
	}


	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getModifiedDate() {
		return modifiedDate;
	}


	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


	@Override
	public String toString() {
		return "Parameter [creationDate=" + creationDate + ", defaultValue=" + defaultValue + ", isHeader=" + isHeader
				+ ", isPathVariable=" + isPathVariable + ", isRequestParam=" + isRequestParam + ", key=" + key
				+ ", modifiedDate=" + modifiedDate + ", objectId=" + objectId + ", objectType=" + objectType
				+ ", value=" + value + "]";
	} 
	
	
}
