package architecture.community.user.profile;

import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.UserProfile;

public class CustomUserProfile implements UserProfile {

	private long userId;
	
	private HashMap<String, Object> data = null;

	private Date creationDate;
	
	private Date modifiedDate;
	
	public CustomUserProfile(long userId) { 
		this.userId = userId;
		this.data = new HashMap<String, Object>();
	}


	@JsonAnySetter
	public void handleUnknown(String key, Object value) {
		data.put(key, value);
	}
	
	@JsonIgnore
	public void setData(String key, Object value) {
		data.put(key, value);
	}

	public HashMap<String, Object> getData() {
		return data;
	}
	
	public String getDataAsString(String key, String defaultValue) {
		if (data.containsKey(key)) {
			try {
				return data.get(key).toString();
			} catch (Exception ignore) {
			}
		}
		return defaultValue;
	}

	@Override
	public long getUserId() {
		return userId;
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
