package architecture.community.category;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.community.category.dao.CategoryDao;
import architecture.community.page.Page;
import architecture.community.query.CustomQueryService;
import architecture.community.security.spring.acls.CommunityAclService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element; 

public class CommunityCategoryService implements CategoryService {

	@Inject
	@Qualifier("categoryDao")
	private CategoryDao categoryDao;
    
	@Inject
	@Qualifier("categoryCache")
	private Cache categoryCache;


	@Inject
	@Qualifier("communityAclService")
	private CommunityAclService communityAclService;
	
	@Inject
	@Qualifier("customQueryService")
	private CustomQueryService customQueryService;
	
	public Category getCategory(long categoryId) throws CategoryNotFoundException {
		Category category = null;
		if (categoryCache.get(categoryId) != null) {
			category = (Category) categoryCache.get(categoryId).getObjectValue();
		}
		if (category == null) {
			category = categoryDao.load(categoryId);
			updateCache(category);
		}
		return category;
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveOrUpdate(Category category) {	 
		categoryDao.saveOrUpdate(category); 
		if (categoryCache.get(category.getCategoryId()) != null) {
			categoryCache.remove(category.getCategoryId());
		}
	}
	
	private void updateCache(Category category) {
		categoryCache.put( new Element(category.getCategoryId(), category) );
	}
 
	
	public Category getCategory(Page page) {
		Object val = page.getProperties().get("categoryId");
		long categoryId = 0 ;
		if( val != null ) {
			categoryId = Long.parseLong(val.toString());
		}
		if( categoryId > 0) {
			try {
				return getCategory(categoryId);
			} catch (CategoryNotFoundException e) {
			}
		}
		return null;
	}
}
