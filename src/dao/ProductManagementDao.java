package dao;

import vo.Category;
import vo.Product;

import java.util.List;

public interface ProductManagementDao {

    public void insertProduct(Product product);

    public List<Category> getMainCategories();

    public List<Category> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber);

    public List<Product> getProductList();

    public Product getProductByProductCode(String V_PRODUCT_CD);

    public int updateProductByProductCode(String productcode, Product product);

}
