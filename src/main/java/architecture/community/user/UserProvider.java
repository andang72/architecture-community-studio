package architecture.community.user;

import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.ItemList;

public interface UserProvider {
	
	enum Type { 
		NONE ,
		JDBC ;
	}
	
	public abstract Type getType();
	
	public abstract boolean isEnabled();
	
	public abstract User getUser(User user);

	public abstract Iterable<User> getUsers();

	public abstract Iterable<String> getUsernames();

	public abstract boolean supportsUpdate();

	public abstract void update(User user) throws UnsupportedOperationException;

	public abstract User create(User user) throws AlreadyExistsException, UnsupportedOperationException;

	public abstract void delete(User user);

	public abstract boolean supportsPagination();

	public abstract int getCount() throws UnsupportedOperationException;

	public abstract Iterable<User> getUsers(int i, int j);

	public abstract String getName();
	
	public abstract ItemList findUsers(DataSourceRequest request, UserManager userManager) ;
	
}
