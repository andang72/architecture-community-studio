package architecture.community.i18n;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.ModelObject;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;

public class I18nText implements ModelObject {

	private Integer objectType;
	
	private Long objectId;
	
    private Long textId;
    
    private String key;
    
    private String text;
    
    private String locale ; 
    
	private Date creationDate;

	private Date modifiedDate;
	
	public I18nText() { 
		this.objectId = UNKNOWN_OBJECT_ID;
		this.objectType = UNKNOWN_OBJECT_TYPE;
		this.textId = UNKNOWN_OBJECT_ID;
	}

	public I18nText(Long textId) { 
		this.objectId = UNKNOWN_OBJECT_ID;
		this.objectType = UNKNOWN_OBJECT_TYPE;
		this.textId = textId;
	}
	
	@Override
	public int getObjectType() { 
		return objectType;
	}

	@Override
	public long getObjectId() { 
		return objectId;
	}

	public Long getTextId() {
		return textId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

 

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setTextId(Long textId) {
		this.textId = textId;
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreationDate() {
		return creationDate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	} 
}
