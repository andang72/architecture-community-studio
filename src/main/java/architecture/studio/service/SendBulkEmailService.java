package architecture.studio.service;

import java.io.IOException;

import architecture.community.exception.NotFoundException;
import architecture.studio.component.email.SendBulkEmail;
import architecture.studio.component.email.SendEmail;

public interface SendBulkEmailService { 
	
	public SendBulkEmail getSendBulkEmail(long sendEmailId) throws NotFoundException ;
	
	public void saveOrUpdate( SendEmail sendBulkEmail );
	 
	public void remove(SendEmail sendEmail) throws IOException ;
	
}
