package architecture.community.web.spring.controller.data.model;

import java.util.List;

import architecture.community.user.User;

public class JwtResponse {
	
	private String jwtToken;
	private User user;		
	private List<String> roles;
	
	public JwtResponse () {
		
	}
	
	public JwtResponse(String token, User user, List<String> roles) { 
		this.jwtToken = token;
		this.user = user;
		this.roles = roles;
	} 
	
 
	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	
}
