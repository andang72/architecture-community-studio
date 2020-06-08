package architecture.community.page.api.dao;

import java.util.List;

import architecture.community.page.api.Api;

public interface ApiDao {
	
	public abstract void saveOrUpdate(Api api);
	
	public abstract Api getApiById(long apiId);
	
	public abstract Long getApiIdByName(String name);
	
	public abstract void deleteApi(Api api);
	
	public abstract List<Api> getAllApiHasPatterns ();
	
}
