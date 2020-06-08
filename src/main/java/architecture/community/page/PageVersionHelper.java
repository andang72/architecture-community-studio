package architecture.community.page;

import java.util.List;

import architecture.community.util.CommunityContextHelper;

public class PageVersionHelper {

	public PageVersionHelper() {
	}

	public static List<PageVersion> getPageVersions(long pageId) {
		return CommunityContextHelper.getComponent(PageService.class).getPageVersions(pageId);
	}

	public static PageVersion getPublishedPageVersion(long pageId) {
		List<PageVersion> list = getPageVersions(pageId);
		for (PageVersion pv : list) {
			if (pv.getPageState() == PageState.PUBLISHED)
				return pv;
		}
		return null;
	}

	public static PageVersion getNewestPageVersion(long pageId) {
		List<PageVersion> list = getPageVersions(pageId);
		if (list.size() == 0)
			return null;
		return list.get(0);
	}

	public static PageVersion getDeletedPageVersion(long pageId) {
		List<PageVersion> list = getPageVersions(pageId);
		for (PageVersion pv : list) {
			if (pv.getPageState() == PageState.DELETED)
				return pv;
		}
		return null;
	}
}
