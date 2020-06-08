package architecture.community.page.dao;

import java.util.List;

import architecture.community.page.PageVersion;

public interface PageVersionDao {

    public abstract void update(PageVersion pageVersion);

    public abstract void delete(PageVersion pageVersion);

    public abstract PageVersion getPageVersion(long pageId, int versionNumber);

    public abstract List<PageVersion> getPageVersions(long pageId);

    public abstract List<Integer> getPageVersionIds(long pageId);

}