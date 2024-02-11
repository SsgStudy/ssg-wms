package service;

import vo.Category;
import vo.Product;
import java.util.List;

public interface ProductService {

    public void productRegistration(Product product);

    List<Category> getMainCategories();

    List<Category> getSubCategoriesByMainCategory(int mainCategoryNumber);

    public List<Category> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber);


}
