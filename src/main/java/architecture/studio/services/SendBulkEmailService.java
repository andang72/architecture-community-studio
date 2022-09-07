package architecture.studio.services;

import java.io.IOException;

import architecture.community.exception.NotFoundException;
import architecture.studio.components.email.SendBulkEmail;
import architecture.studio.components.email.SendEmail;

public interface SendBulkEmailService { 
	
	public SendBulkEmail getSendBulkEmail(long sendEmailId) throws NotFoundException ;
	
	public void saveOrUpdate( SendEmail sendBulkEmail );
	 
	public void remove(SendEmail sendEmail) throws IOException ;
	
}
