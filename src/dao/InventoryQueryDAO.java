package dao;

import vo.CategoryVO;
import vo.ProductInventoryCategoryVO;
import vo.ProductInventoryWarehouseVO;

import java.util.List;

public interface InventoryQueryDAO {

    //상품별 창고 재고 조회
    List<String> getWarehouseCode();
    List<ProductInventoryWarehouseVO> getInventoryTotalByWarehouse();

    //카테고리 정보 출력
    List<CategoryVO> getMainCategories();
    List<CategoryVO> getSubCategoriesByMainCategory(int mainCategoryNumber);
    List<CategoryVO> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber);


    //카테고리별 상품 및 재고 출력
    List<ProductInventoryCategoryVO> getInventoryByMainCategory(int categoryNumber);
    List<ProductInventoryCategoryVO> getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber);
    List<ProductInventoryCategoryVO> getInventoryByDetailCategory(int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber);
    
}
