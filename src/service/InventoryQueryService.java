package service;

public interface InventoryQueryService {
    void getProductInventoryTotalByWarehouse();

    //카테고리 정보 출력
    void getMainCategories();
    void getSubCategoriesByMainCategory(int mainCategoryNumber);
    void getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber);


    //카테고리별 상품 및 재고 출력
    void getInventoryByMainCategory(int categoryNumber, String categoryName);
    void getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber, String categoryName);
    void getInventoryByDetailCategory(int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber, String categoryName);
}
