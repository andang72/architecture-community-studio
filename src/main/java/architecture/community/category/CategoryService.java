package architecture.community.category;

import architecture.community.page.Page;

public interface CategoryService {
 
	public Category getCategory(long categoryId) throws CategoryNotFoundException ; 
	
	public Category getCategory(Page page) ; 
	
	public void saveOrUpdate(Category category);
	 
	
}
