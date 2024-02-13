package service;

import vo.CategoryVO;
import vo.ProductInventoryCategoryVO;
import vo.ProductInventoryWarehouseVO;

import java.util.List;

public interface InventoryQueryService {
    //창고별 상품 및 재고 출력
    List<ProductInventoryWarehouseVO> getProductInventoryTotalByWarehouse();

    //카테고리 정보 출력
    List<CategoryVO> getMainCategories();
    List<CategoryVO>  getSubCategoriesByMainCategory(int mainCategoryNumber);
    List<CategoryVO> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber);


    //카테고리별 상품 및 재고 출력
    List<ProductInventoryCategoryVO> getInventoryByMainCategory(int categoryNumber);
    List<ProductInventoryCategoryVO> getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber);
    List<ProductInventoryCategoryVO> getInventoryByDetailCategory (int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber);
}
