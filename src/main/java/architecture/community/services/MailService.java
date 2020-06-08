package architecture.community.services;

public interface MailService {

	public boolean isEnabled () ;
	
	/**
	 * 
	 * @param fromEmail 보내는 사람 메일 주소 
	 * @param toMail 받는 사람 메일 주소 
	 * @param subject 메일 제목 
	 * @param body 메일 내용 
	 * @param html html 여부 
	 * @throws Exception
	 */
	public void send(String fromUser, String toUser, String subject, String body, boolean html ) throws Exception;
	
	/**
	 * 
	 * @param fromEmail 보내는 사람 메일 주소
	 * @param fromName 보내는 사람 이름 
	 * @param toMail 받는 사람 메일 주소 
	 * @param subject 메일 제목 
	 * @param body 메일 내용 
	 * @param html html 여부 
	 * @throws Exception
	 */
	public void send(String fromEmail, String fromName,  String toMail, String subject, String body, boolean html ) throws Exception ;
	
}
