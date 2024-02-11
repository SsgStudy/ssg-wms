package dao;

import vo.Category;

import java.util.List;

public interface ProductManagementDao {

    public void productRegistration();

    public List<Category> getMainCategories();

    public List<Category> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber);
}
