package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.community.user.Company;
import architecture.community.user.CompanyAlreadyExistsException;
import architecture.community.user.CompanyNotFoundException;
import architecture.community.user.CompanyService;
import architecture.community.user.User;
import architecture.community.user.UserManager;
import architecture.community.user.UserNotFoundException;
import architecture.community.user.UserTemplate;
import architecture.ee.service.ConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = { "classpath:application-community-context.xml",
		"classpath:context/community-core-context.xml", "classpath:context/community-ehcache-context.xml",
		"classpath:context/community-utils-context.xml",
		"classpath:context/community-user-context.xml" })
public class UserTest {

	private static Logger log = LoggerFactory.getLogger(UserTest.class);
 
	@Autowired
	private UserManager userManager;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private ConfigService configService;
	
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Test
	public void testPassword () {
		String password = "admin";
		String encPassword = passwordEncoder.encode(password);
		log.debug( "PASSWORD : {} > {}", password, encPassword);
		

		log.debug( "PASSWORD VARIFY : {}", passwordEncoder.matches("admin", "$2a$10$FastSalTl2zbbrkHZNBKaeiBNosRyyHd2k4HVwzLqzTyH91k0dkrW") );
	}
	
	public UserTest() {
	}

	@Test
	public void testUserManager() {
		
	}
	
	public void testCreateUserIfNotExist() {
		
		if( configService.isSetDataSource() && configService.isDatabaseInitialized() && userManager.getUserCount() == 0 ) {
			User newUesr = new UserTemplate("admin", "admin", "관리자", false, "admin@demo.com", false);
			log.debug("---------------" + newUesr);
	
			
			User existUser = userManager.getUser(newUesr);
	
			log.debug("USER:" + existUser);
			if (existUser != null && existUser.getUserId() > 0) {
				log.debug("now remove : " + existUser);
				try {
					userManager.deleteUser(existUser);
				} catch (Exception e) {
					log.error("ERROR", e);
				}
			}
			log.debug("---------------");
			try {
				existUser = userManager.createUser(newUesr);
			} catch (Exception e) {
				log.error("ERROR", e);
			}
	
			if (existUser != null && existUser.getUserId() > 0) {
				log.debug("now remove : " + existUser);
				try {
					userManager.deleteUser(existUser);
				} catch (UserNotFoundException e) {
					log.error("ERROR", e);
				}
			}
		}
	}

	
	public void createCompany() {
		boolean exist = false;
		Company company = null;
		if( configService.isSetDataSource() && configService.isDatabaseInitialized() ) {
			try {
				company = companyService.getCompany("PODOSOFTWARE");
				exist = true;
			} catch (CompanyNotFoundException e1) {
				exist = false;
			}
			
			if (!exist)
				try {
					company = companyService.createCompany("PODOSOFTWARE", "포도소프트웨어", "");
				} catch (CompanyAlreadyExistsException e) {
				}
	
			if (company != null)
				try {
					companyService.deleteCompany(company);
				} catch (CompanyNotFoundException e) {
					e.printStackTrace();
				}
		}
	}
}
