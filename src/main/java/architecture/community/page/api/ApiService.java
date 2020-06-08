package architecture.community.page.api;

import java.util.List;

import architecture.community.page.PathPattern;

public interface ApiService {

	public Api getApiById( long apiId ) throws ApiNotFoundException; 
	
	public Api getApi(String name ) throws ApiNotFoundException ;
	
	public void deleteApi( Api api ) ;
	
	public void saveOrUpdate(Api api);
	
	public List<PathPattern> getPathPatterns(String prefix);
	
}
