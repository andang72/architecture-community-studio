package architecture.community.user;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.model.PropertyAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.model.json.JsonUserStatusDeserializer;
import architecture.ee.util.StringUtils;

public class UserTemplate extends PropertyAwareSupport implements User, Serializable {

	private long userId;

	private String username;

	private String name;

	private Status status;

	private String email;

	private String firstName;

	private String lastName;

	private String password;

	private String passwordHash;

	private boolean enabled;

	private boolean nameVisible;

	private boolean emailVisible;

	private Date creationDate;

	private Date modifiedDate;
	
	private boolean external;
	
	public UserTemplate(String username, String password, String name, boolean nameVisible, String email,  boolean emailVisible) {
		
		this.userId = -2L;
		if(StringUtils.isNullOrEmpty(username))
			throw new NullPointerException(CommunityLogLocalizer.getMessage("010015"));
		
		this.username = username;
		this.name = name;
		this.email = email;
		this.password = password;
		this.enabled = true;
		this.nameVisible = nameVisible;
		this.emailVisible = emailVisible;
		this.enabled = true;
		this.external = false;
	}

	public UserTemplate(User user) {
		if (null == user)
			return;
		this.userId = user.getUserId();
		
		if(StringUtils.isNullOrEmpty(user.getUsername()))
			throw new NullPointerException(CommunityLogLocalizer.getMessage("010015"));
		
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.name = user.getName();
		this.email = user.getEmail();
		this.nameVisible = user.isNameVisible();
		this.emailVisible = user.isEmailVisible();
		this.creationDate = user.getCreationDate();
		this.modifiedDate = user.getModifiedDate();
		this.status = user.getStatus();
		this.password = user.getPassword();
		this.passwordHash = user.getPasswordHash();
		this.enabled = user.isEnabled();
		this.status = Status.NONE;
		
		if( user.getStatus() != Status.NONE)
			this.status = user.getStatus();
		
		this.external = user.isExternal();
	
	}

	public UserTemplate() {
		this.userId = -2L;
		this.status = Status.NONE;
		this.external = false;
	}

	public UserTemplate(String username) {
		this.userId = -2L;
		this.username = username;
		this.enabled = true;
		this.status = Status.NONE;
		this.external = false;
	}
	
	public UserTemplate(long userId, String username) {
		this.userId = userId;
		this.username = username;
		this.password = "";
		this.external = false;
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

	public UserTemplate(long userId) {
		this.userId = userId;
	}


	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonGetter
	public String getName() {
		if (lastName != null && firstName != null) {
			StringBuilder builder = new StringBuilder(firstName);
			builder.append(" ").append(lastName);
			return builder.toString();
		} else {
			return name;
		}
	}

	@JsonSetter
	public void setName(String name) {
		if (lastName != null && firstName != null && name != null) {
			name = name.trim();
			int index = name.indexOf(" ");
			if (index > -1) {
				firstName = name.substring(0, index);
				lastName = name.substring(index + 1, name.length());
				lastName = lastName.trim();
				this.name = null;
			} else {
				firstName = null;
				lastName = null;
				this.name = name;
			}
		} else {
			this.name = name;
		}
	}

	public Status getStatus() {
		return status;
	}

	@JsonDeserialize(using = JsonUserStatusDeserializer.class)
	public void setStatus(Status status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonIgnore
	public String getPasswordHash() {
		return passwordHash;
	}

	@JsonIgnore
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isNameVisible() {
		return nameVisible;
	}

	public void setNameVisible(boolean nameVisible) {
		this.nameVisible = nameVisible;
	}

	public boolean isEmailVisible() {
		return emailVisible;
	}

	public void setEmailVisible(boolean emailVisible) {
		this.emailVisible = emailVisible;
	}

	
	
	
	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	public boolean isAnonymous() {
		if (this.userId == -1L)
			return true;
		else
			return false;
	}
 
	public String toString() {
		return "UserTemplate [userId=" + userId + ", username=" + username + "]";
	}

}
