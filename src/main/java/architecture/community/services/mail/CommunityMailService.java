package architecture.community.services.mail;

import java.io.File;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import architecture.community.services.CommunityAdminService;
import architecture.community.services.MailService;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;


public class CommunityMailService implements MailService {

	private Logger log = LoggerFactory.getLogger(CommunityMailService.class);
	
	public static String CONFIG_FILENAME = "mail-service-config.xml";
	
	@Inject
	@Qualifier("repository")
	private Repository repository;	
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;	
	
	@Autowired (required=false)
	private CommunityAdminService adminService;
	
	private MailServicesConfig config ;
	
	private JavaMailSender mailSender; 
	
	public void initialize() throws Exception {   
		try { 
			
			File file = repository.getConfigRoot().getFile(CONFIG_FILENAME);
			log.debug("Read from {}", file.getPath());
			
			MailServiceConfigEditor editor = new MailServiceConfigEditor(file); 
			editor.createIfNotExist(file);
			
			config = editor.getMailServicesConfig();
			log.debug( "Initialize Mail Service. enabled:{}", isEnabled() );
			
			if( config.getEnabled() ) { 
				
				adminService.addMailSender(config);
				log.debug("Register JavaMailSender Bean : {}.", config.getBeanName());
				mailSender = adminService.getComponent(config.getBeanName(), JavaMailSenderImpl.class);
			}
			
		} catch (Exception e) {
			log.error("Fail to read config.xml", e);
		}
	}
	
	public void refresh() {
		try {
			initialize();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void testConnection() throws Exception {
		 if(isEnabled()) {
			( (JavaMailSenderImpl)mailSender).testConnection();
		 }
	}
	
	public MailServicesConfig getMailServicesConfig() {
		if( this.config == null)
			try {
				initialize();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		return this.config;
	}
	
	public boolean isEnabled () {  
		return this.config.getEnabled();
	}
	
	@Async
	public void send(String fromUser, String toUser, String subject, String body, boolean html ) throws Exception {
        try {
        	
        	log.debug("mail service enabled : {}", isEnabled()); 
	        if(isEnabled()) {
	        	MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);            
	            helper.setFrom(fromUser);
	            helper.setTo(toUser); 
	            helper.setSubject(subject);
	            helper.setText(body, html);
	    		mailSender.send(message);
	        }
        } catch (MessagingException e) {
        	log.error(e.getMessage(), e);
        }
	}
	
	@Async
	public void send(String fromEmail, String fromName,  String toMail, String subject, String body, boolean html ) throws Exception {
        try {
        	log.debug("mail service enabled : {}", isEnabled()); 
	        if(isEnabled()) {
	        	MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);
	            helper.setFrom(fromEmail, fromName);
	            helper.setTo(toMail); 
	            helper.setSubject(subject);
	            helper.setText(body, html);
	    		mailSender.send(message);
	        }
        } catch (MessagingException e) {
        	log.error(e.getMessage(), e);
        }
	}
	
}
