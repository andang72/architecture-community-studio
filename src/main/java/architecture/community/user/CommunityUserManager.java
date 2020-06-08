package architecture.community.user;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.i18n.CommunityLogLocalizer;
import architecture.community.user.dao.UserDao;
import architecture.community.user.event.UserRemovedEvent;
import architecture.ee.spring.event.EventSupport;
import architecture.ee.util.StringUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element; 

public class CommunityUserManager extends EventSupport implements UserManager, MultiUserManager {

	private static final Logger log = LoggerFactory.getLogger(CommunityUserManager.class);

	@Inject
	@Qualifier("userDao")
	private UserDao userDao;

	@Inject
	@Qualifier("passwordEncoder")
	protected PasswordEncoder passwordEncoder;

	@Inject
	@Qualifier("userCache")
	private Cache userCache;

	@Inject
	@Qualifier("userIdCache")
	private Cache userIdCache;

	@Inject
	@Qualifier("userProviderCache")
	private Cache userProviderCache;
	
	protected List<UserProvider> providers;
	
	private boolean emailAddressCaseSensitive;
	
	private boolean allowApplicationUserCreation;
	
	public CommunityUserManager() {
		emailAddressCaseSensitive = true;
		providers = new ArrayList<UserProvider>();
		allowApplicationUserCreation = true;
	}
	
    public void setAllowApplicationUserCreation(boolean allow)
	{
    	allowApplicationUserCreation = allow;
	}

    public void addUserProvider(UserProvider provider)
    {
        synchronized(providers)
        {
            this.providers.add(provider);
            log.debug("User provider ({}) added.", provider.getName());
        }
    }
    
    public void setUserProviders(List<UserProvider> providers)
    {
        if(null == providers)
        {
            throw new NullPointerException( CommunityLogLocalizer.getMessage("010030") );
        } else
        {
            this.providers = providers;
            return;
        }
    } 
    
	public User getUser(User template) {
		return getUser(template, true);
	}

	public User getUser(User template, boolean caseSensitive) { 
		User user = null;
		if (template.getUserId() > 0L) {
			log.debug(CommunityLogLocalizer.format("010018", template.getUserId() ));
			user = getUserInCache(template.getUserId());
			if (user == null) {
				log.debug( CommunityLogLocalizer.format("010019", template.getUserId() ) );
				try {
					log.debug("Find user by ID {}.", template.getUserId() );
					user = userDao.getUserById(template.getUserId());
					updateCaches(user);
				} catch (Throwable e) {
					log.error(CommunityLogLocalizer.getMessage("010005"), e);
				} 
			}
		} 
		
		if (user == null && !StringUtils.isNullOrEmpty(template.getUsername())) { 
			String nameToUse = template.getUsername();
			long userIdToUse = getUserIdInCache(nameToUse);  
			if (userIdToUse > 0L) { 
				log.debug( CommunityLogLocalizer.format("010020", nameToUse, userIdToUse) );
				user = getUserInCache(userIdToUse);
			}
			if (user == null) {
				if (!caseSensitive) {
					nameToUse = nameToUse.toUpperCase();
				}
				try {
					log.debug("Find user by username {}.", nameToUse );
					user = userDao.getUserByUsername(nameToUse);
					updateCaches(user);
				} catch (Throwable e) {
					log.error(CommunityLogLocalizer.getMessage("010004"), e);
				}
			}
		}

		if (null == user && !StringUtils.isNullOrEmpty(template.getEmail())) {
			try {
				log.debug("Find user by email {}.", template.getEmail() );
				user = userDao.getUserByEmail(template.getEmail());
				updateCaches(user);
			} catch (Exception ex) {
				log.debug(CommunityLogLocalizer.getMessage("010006"), ex);
			}
		}
		
		if( null == user ) {
			log.debug("Using external providers({}).", providers.size() );
			user = sourceUserFromProvider(template);
			if(null !=  user) {
				//
				try {
					log.debug(CommunityLogLocalizer.format("010033", user.toString() ));
					user = createApplicationUser(user);
				} catch (UserAlreadyExistsException e) {
					
				}
			}
		}
		
		return user;
	}

	public User getUser(String username) throws UserNotFoundException {
		User user = getUser(((User) (new UserTemplate(username))));
		if (null == user) {
			UserNotFoundException e = new UserNotFoundException(CommunityLogLocalizer.format("010002", username));
			throw e;
		}
		return user;
	}

	public User getUser(long userId) throws UserNotFoundException {
		User user = getUser(((User) (new UserTemplate(userId))));
		if (null == user) {
			UserNotFoundException e = new UserNotFoundException(CommunityLogLocalizer.format("010002", userId));
			throw e;
		}
		return user;
	}
	
	/**
	 * return user provider .
	 * @param user
	 * @return
	 */
	protected UserProvider getUserProvider(User user) { 
		
		if( providers.size() == 0) {
			return null;
		}
		String providerName = null;
		if( userProviderCache.get(user.getUserId()) != null) {
			providerName = (String)userProviderCache.get(user.getUserId()).getObjectValue();
		}
		
		if( providerName != null ) {
			for( UserProvider provider : providers ) {
				if(provider.getName().equals(providerName)) {
					return provider;
				}
			}
		}
		
		for( UserProvider provider : providers ) {
			User sourceUser = provider.getUser(user);
			if( sourceUser != null) {
				// update cache ..
				userProviderCache.put(new Element(user.getUserId(), provider.getName()));
				updateCaches(sourceUser); 
				return provider;
			}
		}
		return null;
	}
	
	
	private static final SecureRandom secureRandom = new SecureRandom(); 
	
	protected User sourceUserFromProvider(User user ) {
		if( providers.size() == 0) 
			return null;  
		User sourceUser = null;
		UserProvider sourceProvider = null;
		for( UserProvider provider : providers ) {
			log.debug("Get user ({}) from provider ({})", user, provider.getName());
			sourceUser = provider.getUser(user);
			if(sourceUser != null){
				sourceProvider = provider ;
				break;
			}
		}
		
		validateProviderUser(sourceUser);
		
		if( sourceUser != null ) {
			// if provider does not support hashed password.
			// return random number (64) bytes ...
			if( StringUtils.isNullOrEmpty( sourceUser.getPasswordHash() ) ) { 
				UserTemplate sourceUserWithPassword = new UserTemplate(sourceUser);
				byte buffer [] = new byte[64];
				secureRandom.nextBytes(buffer);
				sourceUserWithPassword.setPasswordHash( new String(buffer) );
				sourceUser = sourceUserWithPassword;
			} 
			// application does not have ... then create new one.
			User appUser = userDao.getUserByUsername(sourceUser.getUsername());
			if( appUser == null) {
				// create user ...
				appUser = userDao.createUser(sourceUser);
			} 
			userProviderCache.put(new Element( appUser.getUserId(), sourceProvider.getName() ));
			return appUser;
		}
		return null;
	}
	
	protected void validateProviderUser(User user) {
		log.debug("validate '{}' from external provider.", user);
		if( StringUtils.isNullOrEmpty(user.getName()))
			throw new InvalidProviderUserException("");
		if( StringUtils.isNullOrEmpty(user.getEmail()))
			throw new InvalidProviderUserException("");
		else 
			return; 
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public User createApplicationUser(User newUser) throws UserAlreadyExistsException {
		
		User user = getUser(newUser); 
		if (null != user) {
			UserAlreadyExistsException e = new UserAlreadyExistsException( CommunityLogLocalizer.format("010014", user.getUsername(), caseEmailAddress(user)));
			log.error(e.getMessage());
			throw e;
		} 
		UserTemplate userTemplate = new UserTemplate(newUser); 
		if( userTemplate.isExternal() ) { 
			byte buffer [] = new byte[64];
			secureRandom.nextBytes(buffer);
			userTemplate.setPassword( new String(buffer) );
			log.debug("passord word set for external user {}".concat( userTemplate.getPassword() ));
		}
		
		// userTemplate.setPassword(newUser.getPassword());
		userTemplate.setPasswordHash(getPasswordHash(newUser));
		userTemplate.setEmail(caseEmailAddress(newUser));
		
		setTemplateDates(userTemplate); 
		
		user = userDao.createUser(userTemplate); 
		userTemplate = new UserTemplate(user);
		userTemplate.setPassword(null);
		
		updateCaches(userTemplate); 
		return userTemplate;
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public User createUser(User newUser) throws UserAlreadyExistsException, EmailAlreadyExistsException { 
		if(!allowApplicationUserCreation)
			throw new UnsupportedOperationException(CommunityLogLocalizer.format("010022", allowApplicationUserCreation )); 
		
		User user = getUser(newUser); 
		if (null != user) {
			if (!user.getUsername().equals(newUser.getUsername())) {
				if (caseEmailAddress(user).equals(caseEmailAddress(newUser))) {
					EmailAlreadyExistsException e = new EmailAlreadyExistsException( CommunityLogLocalizer.format("010014", user.getUsername(), caseEmailAddress(user)));
					throw e;
				}
			} else {
				UserAlreadyExistsException e = new UserAlreadyExistsException( CommunityLogLocalizer.format("010014", user.getUsername(), caseEmailAddress(user)));
				log.info(e.getMessage());
				throw e;
			}
		} 
		
		UserTemplate userTemplate = new UserTemplate(newUser); 
		if( userTemplate.isExternal() ) {
			
		}
		
		userTemplate.setPassword(newUser.getPassword());
		userTemplate.setPasswordHash(getPasswordHash(newUser));
		userTemplate.setEmail(caseEmailAddress(newUser));
		setTemplateDates(userTemplate);  
		user = createApplicationUser(userTemplate); 
		
		for( UserProvider provider : providers) {
			if(provider.supportsUpdate()) {
				log.info(CommunityLogLocalizer.format("010031", provider.getName() ));
				Long systemId = user.getUserId();
				try {
					User result = provider.create(user);
				} catch (UnsupportedOperationException | AlreadyExistsException e) {
					log.error(CommunityLogLocalizer.format("010032", provider.getName() ));
				}
			}
		} 
		return user;
	}
	
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteUser(User user) throws UserNotFoundException {
		User existUser = getUser(user); 
		log.debug("Checing provider for {}", existUser.getUsername());
		UserProvider up = getUserProvider(existUser);
		try {
			if( up != null && up.supportsUpdate() )
			{
				log.debug("deleting user with provider '{}'.", up);
				up.delete(existUser);
				userProviderCache.remove(existUser.getUserId());
			}
		}catch(Exception e) {
			log.warn("deleting to user with provider '{}' failed. local user will removed...", up );
		}
		
		try {
			userDao.deleteUser(existUser);
			evictCaches(existUser); 
			UserRemovedEvent event = new UserRemovedEvent(this, existUser);
			fireEvent(event);
		} catch (DataAccessException ex) {
			String message = CommunityLogLocalizer.format("010016", user);
			log.error(message, ex);
			throw ex;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteUsers(List<User> users) throws UserNotFoundException {
		for( User u: users )
			deleteUser(u);
	}
	
	protected void evictCaches(User user) {
		userCache.remove(Long.valueOf(user.getUserId()));
		userIdCache.remove(user.getUsername());
	}

	protected long getUserIdInCache(String username) {
		if (userIdCache.get(username) != null) {
			return (Long) userIdCache.get(username).getObjectValue();
		}
		return -2L;
	}

	protected void updateCaches(User user) {
		if (user != null) {
			log.debug(  CommunityLogLocalizer.format("010021", user.getUsername(), user.getUserId()) );
			if (user.getUserId() > 0 && !StringUtils.isNullOrEmpty(user.getUsername())) { 
				if(userIdCache.get(user.getUsername()) != null)
					userIdCache.remove(user.getUsername());
				userIdCache.put( new Element( user.getUsername(), user.getUserId() ) );
				if(userCache.get(user.getUserId()) != null)
					userCache.remove(user.getUserId());				
				userCache.put( new Element(user.getUserId(), user) );
			}
		}
	}

	protected User getUserInCache(Long userId) {
		if (userCache.get(userId) != null) {
			log.debug(CommunityLogLocalizer.format("010017", userId));
			return (User) userCache.get(userId).getObjectValue();
		}
		return null;
	}

	/**
	 * 사용자 객체의 생성일 과 수정일이 널인 경우 현재 일자로 업데이트 한다.
	 * 
	 * @param ut
	 */
	private void setTemplateDates(UserTemplate ut) {
		if (null == ut)
			return;
		if (null == ut.getCreationDate())
			ut.setCreationDate(new Date());
		if (null == ut.getModifiedDate())
			ut.setModifiedDate(new Date());
	}


	private String caseEmailAddress(User user) {
		return emailAddressCaseSensitive || user.getEmail() == null ? user.getEmail() : user.getEmail().toLowerCase();
	}

	@Override
	public List<User> findUsers(String nameOrEmail) {
		List<Long> ids = userDao.findUserIds(nameOrEmail);
		List<User> users = new ArrayList<User>(ids.size());
		for (long id : ids)
			try {
				users.add(getUser(id));
			} catch (UserNotFoundException e) {
				log.warn(e.getMessage(), e);
			}
		return users;
	}

	@Override
	public List<User> findUsers(String nameOrEmail, int startIndex, int numResults) {
		List<Long> ids = userDao.findUserIds(nameOrEmail, startIndex, numResults);
		List<User> users = new ArrayList<User>(ids.size());
		for (long id : ids)
			try {
				users.add(getUser(id));
			} catch (UserNotFoundException e) {
				log.warn(e.getMessage(), e);
			}
		return users;
	}

	@Override
	public int getFoundUserCount(String nameOrEmail) {
		return userDao.getFoundUserCount(nameOrEmail);
	}

	public int getUserCount() {
		return userDao.getUserCount();
	}

	public List<User> getUsers() {
		List<Long> ids = userDao.getUserIds();
		List<User> users = new ArrayList<User>(ids.size());
		for (long id : ids)
			try {
				users.add(getUser(id));
			} catch (UserNotFoundException e) {
				log.warn(e.getMessage(), e);
			}
		return users;
	}
 
	public List<User> getUsers(int startIndex, int numResults) {
		List<Long> ids = userDao.getUserIds(startIndex, numResults);
		List<User> users = new ArrayList<User>(ids.size());
		for (long id : ids)
			try {
				users.add(getUser(id));
			} catch (UserNotFoundException e) {
				log.warn(e.getMessage(), e);
			}
		return users;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateUser(User user) throws UserNotFoundException, UserAlreadyExistsException {

		UserTemplate userToUse = new UserTemplate(getUser(user));
		
		if (null == userToUse) {
			throw new UserNotFoundException("User not found.");
		}
		
		String previousUsername = null;
		
		if (! userToUse.getUsername().equals(user.getUsername())) {
			log.debug("previous username is {}. new username is {}." , userToUse.getUsername() , user.getUsername());
			previousUsername = userToUse.getUsername();
			UserTemplate toCheck = new UserTemplate();
			toCheck.setUsername(user.getUsername());
			User match = getUser(toCheck);
			if (null != match && match.getUserId() != user.getUserId()) {
				throw new UserAlreadyExistsException();
			}
		}
		
		if(!StringUtils.isNullOrEmpty(user.getUsername()))
			userToUse.setUsername(user.getUsername());		
		if(!StringUtils.isNullOrEmpty(user.getEmail()))
			userToUse.setEmail( caseEmailAddress(user) );
		userToUse.setEmailVisible(user.isEmailVisible());
		if( !StringUtils.isNullOrEmpty(user.getName()) && !org.apache.commons.lang3.StringUtils.equals(user.getName(), userToUse.getName()))
			userToUse.setName( user.getName() );
		userToUse.setNameVisible( user.isNameVisible() );
		userToUse.setProperties( user.getProperties() );
		userToUse.setEnabled( user.isEnabled() );
		userToUse.setStatus( user.getStatus() );
		userToUse.setModifiedDate(new Date());		
		
		if( !StringUtils.isNullOrEmpty( user.getPassword() ) ){
			userToUse.setPasswordHash(getPasswordHash(user));	
		}else {
			userToUse.setPasswordHash(userToUse.getPassword());
		}
		wireTemplateDates(userToUse);
		log.debug("is this from external providers ?");
		UserProvider up = getUserProvider(userToUse);
		try {
			if( up != null && !up.supportsUpdate()) {
				log.debug("this provider does not support update.");
			} 
			if( up != null && up.supportsUpdate())
			{
				log.debug("attempting to update user with provider '{}'.", up);
				up.update(userToUse);
			} 
		}catch(Exception e) {
			log.warn("Error attempting to update user with provider '{}'. local user will updated with inconsistent state.", up );
		}
		try {
			userDao.updateUser(userToUse); 
			// cache 수정 ..
			userCache.remove(userToUse.getUserId());
			if (previousUsername != null)
				userIdCache.remove(previousUsername);
			/**
			userCache.put(new Element(userToUse.getUserId(), userToUse));
			if (previousUsername != null)
				userIdCache.remove(previousUsername);
			userIdCache.put(new Element(userToUse.getUsername(), userToUse.getUserId())); 
			**/
		} catch (DataAccessException ex) { 
			throw ex;
		}
	}
	
	public void disableUser(User user) {
		
	}
	
	public boolean verifyPassword(User user, String password) {
		if( user.getPassword() == null || StringUtils.isNullOrEmpty(password))
			return false;
		return passwordEncoder.matches(password, user.getPassword());
	}	

	/**
	 * encode password.
	 * @param user
	 * @return
	 */
	private String getPasswordHash(User user) {
		String passwd;
		passwd = user.getPassword();
		if (StringUtils.isNullOrEmpty(passwd))
			return null;
		try {
			return passwordEncoder.encode(passwd);
		} catch (Exception ex) {
			log.warn(CommunityLogLocalizer.getMessage("010001"), ex);
		}
		return null;
	}
	
	private void wireTemplateDates(UserTemplate ut) {
		if (null == ut)
			return;
		if (null == ut.getCreationDate())
			ut.setCreationDate(new Date());
		if (null == ut.getModifiedDate())
			ut.setModifiedDate(new Date());
	}
	
}
