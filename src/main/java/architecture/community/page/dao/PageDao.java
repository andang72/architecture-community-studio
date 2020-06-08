package architecture.community.page.dao;

import java.util.List;

import architecture.community.page.Page;
import architecture.community.page.PageState;

public interface PageDao {
	
	public abstract void create(Page page);

	public abstract void update(Page page, boolean flag);

	public abstract void delete(Page page);

	public abstract Page getPageById(long pageId);

	public abstract Page getPageById(long pageId, int versionNumber);

	public abstract Page getPageByName(String name);

	public abstract Page getPageByName(String name, int versionNumber);

	public abstract Page getPageByTitle(int objectType, long objectId, String title);

	public abstract int getPageCount(int objectType, long objectId);

	public abstract List<Long> getPageIds(int objectType, long objectId);

	public abstract List<Long> getPageIds(int objectType, long objectId, int startIndex, int maxResults);

	public abstract List<Long> getPageIds(int objectType, PageState state);

	public abstract List<Long> getPageIds(int objectType, PageState state, int startIndex, int maxResults);

	public abstract int getPageCount(int objectType, PageState state);

	public abstract List<Long> getPageIds(int objectType, long objectId, PageState state);

	public abstract List<Long> getPageIds(int objectType, long objectId, PageState state, int startIndex, int maxResults);

	public abstract int getPageCount(int objectType, long objectId, PageState state);
	
	public abstract List<Page> getAllPageHasPatterns ();
}
