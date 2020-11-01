package architecture.community.web.spring.controller.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.exception.UnAuthorizedException;
import architecture.community.security.spring.userdetails.CommuintyUserDetails;
import architecture.community.security.spring.userdetails.SystemUser;
import architecture.community.user.CommunityUser;
import architecture.community.user.User;
import architecture.community.user.UserAlreadyExistsException;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.util.SecurityHelper;
import architecture.community.web.model.Result;

@Controller("community-me-data-controller")
public class MeDataController {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	@Qualifier("userManager")
	private UserManager userManager;

	public MeDataController() {
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(value = { "/data/users/me", "/data/users/me/save-or-update.json" }, method = { RequestMethod.POST })
	@ResponseBody
	public Result saveOrUpdateUser(@RequestBody CommunityUser user, NativeWebRequest request)
			throws UnAuthorizedException, UserNotFoundException, UserAlreadyExistsException {
		log.debug("Save or update user {} ", user.toString());
		User userToUse = user;
		if (userToUse.getUserId() > 0 && userToUse.getUserId() == SecurityHelper.getUser().getUserId()) {
			userManager.updateUser(userToUse);
		} else {
			throw new UnAuthorizedException();
		}
		return Result.newResult("item", userToUse);
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(value = { "/data/users/me.json" }, method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public UserDetails getUserDetails(Authentication authentication, NativeWebRequest request) {
		// Authentication authentication = SecurityHelper.getAuthentication();
		UserDetails userDetails = null;
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof CommuintyUserDetails) {
				CommuintyUserDetails details = (CommuintyUserDetails) principal;
				userDetails = new UserDetails(details.getUser(), getRoles(details.getAuthorities()));
			} else if (principal instanceof SystemUser) {
				SystemUser details = (SystemUser) principal;
				userDetails = new UserDetails(details, getRoles(details.getAuthorities()));
			}
		}

		if (userDetails == null) {
			userDetails = new UserDetails(SecurityHelper.ANONYMOUS, Collections.EMPTY_LIST);
		}
		return userDetails;
	}

	protected List<String> getRoles(Collection<GrantedAuthority> authorities) {
		List<String> list = new ArrayList<String>();
		for (GrantedAuthority auth : authorities) {
			list.add(auth.getAuthority());
		}
		return list;
	}

	public static class UserDetails {
		private User user;
		private List<String> roles;

		public UserDetails() {
		}

		public UserDetails(User user, List<String> roles) {
			this.user = user;
			this.roles = roles;
		}

		/**
		 * @return user
		 */
		public User getUser() {
			return user;
		}

		/**
		 * @param user 설정할 user
		 */
		public void setUser(User user) {
			this.user = user;
		}

		/**
		 * @return roles
		 */
		public List<String> getRoles() {
			return roles;
		}

		/**
		 * @param roles 설정할 roles
		 */
		public void setRoles(List<String> roles) {
			this.roles = roles;
		}
	}
}
