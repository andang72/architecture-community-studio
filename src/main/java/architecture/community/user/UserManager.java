package architecture.community.user;

import java.util.List;

public interface UserManager {
	
	public abstract User getUser(User template );
	
	public abstract User getUser(User template , boolean caseSensitive );
	
	public abstract User getUser(String username) throws UserNotFoundException;
	
	public abstract User getUser(long userId) throws UserNotFoundException;
	
	public abstract User createUser(User newUser) throws UserAlreadyExistsException, EmailAlreadyExistsException;
	
	public abstract void deleteUser(User user) throws UserNotFoundException ;
	
	public abstract void deleteUsers(List<User> users) throws UserNotFoundException;
	
	public abstract void updateUser(User user)  throws UserNotFoundException, UserAlreadyExistsException ;
	
    /**
     * 
     * @param nameOrEmail
     * @return
     */
    public abstract List<User> findUsers(String nameOrEmail);

    /**
     * 
     * @param nameOrEmail
     * @param startIndex
     * @param numResults
     * @return
     */
    public abstract List<User> findUsers(String nameOrEmail, int startIndex, int numResults);

    public abstract int getFoundUserCount(String nameOrEmail);
    
    
    public abstract int getUserCount();
    
    public abstract List<User> getUsers();

    public abstract List<User> getUsers(int startIndex, int numResults);
    
    /**
     * 인자로 전달된 평문 비밀번호를 사용자 정보에 저장된 암호화된 값과 비교 검사하고 그결과를 불값으로 리턴한다.
     * @param user
     * @param password
     * @return
     */
    public abstract boolean verifyPassword(User user, String password);
}
