package architecture.community.category.dao;

import architecture.community.category.Category;
import architecture.community.category.CategoryNotFoundException;

public interface CategoryDao {

    public abstract Category load(long categoryId) throws CategoryNotFoundException;

    public abstract long getNextCategoryId();

    public void saveOrUpdate(Category category);
    
    public abstract void update(Category category);

    public abstract void insert(Category category);

    public abstract void delete(Category category);
    
}
