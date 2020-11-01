package architecture.community.web.spring.controller.data.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import architecture.community.user.User;
import architecture.community.user.model.json.JsonUserDeserializer;

public class PasswordUpdate implements Serializable {
	
	private User user ;
	
	private String verifyPassword ;
	
	private String newPassword ;
	
	private String confirmPassword ;
	 
	
	@JsonDeserialize(using = JsonUserDeserializer.class)
	public void setUser(User user) {
		this.user = user;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}


	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}


	public String getNewPassword() {
		return newPassword;
	}


	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}


	public User getUser() {
		return user;
	}
	
}
