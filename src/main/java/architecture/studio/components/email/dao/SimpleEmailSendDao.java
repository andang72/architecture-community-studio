package architecture.studio.components.email.dao;

import java.io.IOException;

import architecture.community.exception.NotFoundException;
import architecture.studio.components.email.SendBulkEmail;
import architecture.studio.components.email.SendEmail;

public interface SimpleEmailSendDao {
	
	public SendBulkEmail getSendBulkEmail(long sendEmailId) throws NotFoundException ;
	 
	public void saveOrUpdate(SendEmail sendEmail);
	
	public void remove(SendEmail sendEmail) throws IOException; 
	
}
