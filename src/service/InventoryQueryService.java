package service;

import vo.CategoryVO;
import vo.ProductInventoryCategoryVO;
import vo.ProductInventoryWarehouseVO;

import java.util.List;

/**
 * The interface Inventory query service.
 */
public interface InventoryQueryService {

    /**
     * Gets product inventory total by warehouse.
     *
     * @return the product inventory total by warehouse
     */
    List<ProductInventoryWarehouseVO> getProductInventoryTotalByWarehouse();

    /**
     * Gets main categories.
     *
     * @return the main categories
     */
    List<CategoryVO> getMainCategories();

    /**
     * Gets sub categories by main category.
     *
     * @param mainCategoryNumber the main category number
     * @return the sub categories by main category
     */
    List<CategoryVO> getSubCategoriesByMainCategory(int mainCategoryNumber);

    /**
     * Gets detail categories by sub category.
     *
     * @param mainCategoryNumber the main category number
     * @param subCategoryNumber  the sub category number
     * @return the detail categories by sub category
     */
    List<CategoryVO> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber);


    /**
     * Gets inventory by main category.
     *
     * @param categoryNumber the category number
     * @return the inventory by main category
     */
    List<ProductInventoryCategoryVO> getInventoryByMainCategory(int categoryNumber);

    /**
     * Gets inventory by sub category.
     *
     * @param mainCategoryNumber the main category number
     * @param subCategoryNumber  the sub category number
     * @return the inventory by sub category
     */
    List<ProductInventoryCategoryVO> getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber);

    /**
     * Gets inventory by detail category.
     *
     * @param mainCategoryNumber   the main category number
     * @param subCategoryNumber    the sub category number
     * @param detailCategoryNumber the detail category number
     * @return the inventory by detail category
     */
    List<ProductInventoryCategoryVO> getInventoryByDetailCategory(int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber);
}
