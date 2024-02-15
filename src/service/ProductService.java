package service;

import vo.Category;
import vo.Product;

import java.util.List;

/**
 * The interface Product service.
 */
public interface ProductService {

    /**
     * Insert product.
     *
     * @param product the product
     */
    void insertProduct(Product product);

    /**
     * Gets main categories.
     *
     * @return the main categories
     */
    List<Category> getMainCategories();

    /**
     * Gets sub categories by main category.
     *
     * @param mainCategoryNumber the main category number
     * @return the sub categories by main category
     */
    List<Category> getSubCategoriesByMainCategory(int mainCategoryNumber);

    /**
     * Gets detail categories by sub category.
     *
     * @param mainCategoryNumber the main category number
     * @param subCategoryNumber  the sub category number
     * @return the detail categories by sub category
     */
    List<Category> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber);

    /**
     * Gets product list.
     *
     * @return the product list
     */
    List<Product> getProductList();

    /**
     * Gets product by product code.
     *
     * @param productcode the productcode
     * @return the product by product code
     */
    Product getProductByProductCode(String productcode);

    /**
     * Update product by product code int.
     *
     * @param productcode the productcode
     * @param product     the product
     * @return the int
     */
    int updateProductByProductCode(String productcode, Product product);
}
