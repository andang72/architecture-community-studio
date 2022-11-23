package tests;

import java.util.Properties;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class JavaMailSendTest {
 
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties javaMailProperties = new Properties();
		mailSender.setUsername("hrmaster@cnp.re.kr");
		mailSender.setPassword("bizhr1446!");
		 
		javaMailProperties.setProperty("mail.debug", "true");
		javaMailProperties.setProperty("mail.smtp.port", "465");
		javaMailProperties.setProperty("mail.smtp.ssl.enable", "true");
	//	javaMailProperties.setProperty("mail.smtp.auth", "true");
		javaMailProperties.setProperty("mail.smtp.host", "smtp.worksmobile.com");
		
 
//		javaMailProperties.setProperty("mail.transport.protocol", "smtp");
//		javaMailProperties.setProperty("mail.debug", "true");
//		javaMailProperties.setProperty("mail.smtp.port", "587");
//		javaMailProperties.setProperty("mail.smtp.host", "smtp.office365.com");
//		javaMailProperties.setProperty("mail.smtp.auth", "true");      
//		javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");  
		
		/* 
		javaMailProperties.setProperty("mail.transport.protocol", "smtp");
		javaMailProperties.setProperty("mail.debug", "true");
		javaMailProperties.setProperty("mail.smtp.port", "587");
		javaMailProperties.setProperty("mail.smtp.host", "smtp.office365.com");
		javaMailProperties.setProperty("mail.smtp.auth", "true");      
		javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");  
		*/


		mailSender.setJavaMailProperties(javaMailProperties);  
    	MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
      //  helper.setFrom("edu@kisec.com", "시스템");
        helper.setFrom(" hrmaster@cnp.re.kr", "시스템");
        helper.setTo("dhson@podosw.com"); 
        helper.setSubject("test");
        helper.setText("test...", false);
        
		mailSender.send(message);
	}

}
