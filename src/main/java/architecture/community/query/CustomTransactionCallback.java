package architecture.community.query;

import javax.annotation.Nullable;

import org.springframework.dao.DataAccessException;

import architecture.community.query.dao.CustomQueryJdbcDao;

/**
 * Callback interface for transactional code. it inspired by TransactionCallback.java
 * 
 * @author donghyuck
 * @since  2019.10.15
 * 
 * @param <T> the result type.
 */
public interface CustomTransactionCallback<T> {
	
	@Nullable
	T doInTransaction(CustomQueryJdbcDao dao) throws DataAccessException;

}

