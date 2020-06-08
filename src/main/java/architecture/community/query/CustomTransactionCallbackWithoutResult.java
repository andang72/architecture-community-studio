package architecture.community.query;

import javax.annotation.Nullable;

import architecture.community.query.dao.CustomQueryJdbcDao;

public abstract class CustomTransactionCallbackWithoutResult implements CustomTransactionCallback<Object> { 
	@Override
	@Nullable
	public final Object doInTransaction(CustomQueryJdbcDao dao) {
		doInTransactionWithoutResult(dao);
		return null;
	}
	
	protected abstract void doInTransactionWithoutResult(CustomQueryJdbcDao dao);
	
}

