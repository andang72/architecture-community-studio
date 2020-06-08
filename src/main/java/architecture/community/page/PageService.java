package architecture.community.page;

import java.util.List;

import architecture.community.user.User;

public interface PageService {

	public abstract Page createPage(User user, BodyType bodyType, String name, String title, String body);

	public abstract void deletePage(Page page) ;
	
	public abstract void saveOrUpdatePage(Page page);

	public abstract void saveOrUpdatePage(Page page, boolean forceNewVersion);

	public abstract Page getPage(long pageId) throws PageNotFoundException;

	public abstract Page getPage(long pageId, int versionId) throws PageNotFoundException;

	public abstract Page getPage(String name) throws PageNotFoundException;

	public abstract Page getPage(String name, int versionId) throws PageNotFoundException;

	public abstract List<Page> getPages(int objectType);

	public abstract List<Page> getPages(int objectType, long objectId);

	public abstract List<Page> getPages(int objectType, long objectId, int startIndex, int maxResults);

	public abstract int getPageCount(int objectType);

	public abstract int getPageCount(int objectType, long objectId);

	public abstract List<PageVersion> getPageVersions(long pageId);

	public abstract List<Page> getPages(int objectType, PageState state);

	public abstract List<Page> getPages(int objectType, PageState state, int startIndex, int maxResults);

	public abstract int getPageCount(int objectType, PageState state);

	public abstract int getPageCount(int objectType, long objectId, PageState state);

	public abstract List<Page> getPages(int objectType, long objectId, PageState state);

	public abstract List<Page> getPages(int objectType, long objectId, PageState state, int startIndex, int maxResults);

	public abstract List<PathPattern> getPathPatterns(String prefix);
}
