package architecture.community.user;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import architecture.community.model.PropertyAwareSupport;
import architecture.community.model.json.JsonDateDeserializer;
import architecture.community.model.json.JsonDateSerializer;
import architecture.community.user.model.json.JsonUserStatusDeserializer;

public class CommunityUser extends PropertyAwareSupport implements User , Serializable {

	private long userId;
	
	private String username;
	
	private String name;
	
	private Status status;
	
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	private String password;
	
	@JsonIgnore
	private String passwordHash;
	
	private boolean enabled;
	
	private boolean nameVisible;
	
	private boolean emailVisible;
	 	
	private Date creationDate;

	private Date modifiedDate;
	
	private boolean external;
	
	public CommunityUser() {
		userId = -1L;
		username = null;
		name = null;
		email = null;
		firstName = null;
		lastName = null;
		password = null;
		passwordHash = null;
		enabled = false;
		nameVisible = false;
		emailVisible = false;
		creationDate = null;
		modifiedDate = null;
		status = Status.NONE;
		external = false;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

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
	
	public boolean isAnonymous() {
		if( userId > 0)
			return false;
		else 
			return true;
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


	@Override
	public boolean isExternal() { 
		return external;
	}
	
}
