package architecture.community.web.model;

public class Error implements java.io.Serializable {

	private String exceptionClassName;

	private String exceptionMessage;

	/**
	 * 서버에서 발생된 오류를 클라이언트에 전달하기 위한 오류 클래스 <br/>
	 * JSON 형식 변환을 고려하여 만들어짐.
	 * 
	 */
	public Error() {

	}

	public Error(Throwable e) {

		exceptionMessage = e.getMessage();
		exceptionClassName = e.getClass().getName();

		if (e.getCause() != null) {
			Throwable cause = e.getCause();
			exceptionMessage = cause.getMessage();
			exceptionClassName = cause.getClass().getName();
		}
	}

	/**
	 * @return exceptionClassName
	 */
	public String getException() {
		return exceptionClassName;
	}

	/**
	 * @param exceptionClassName
	 *            설정할 exceptionClassName
	 */
	public void setException(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;
	}

	/**
	 * @return exceptionMessage
	 */
	public String getMessage() {
		return exceptionMessage;
	}

	/**
	 * @param exceptionMessage
	 *            설정할 exceptionMessage
	 */
	public void setMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
}
