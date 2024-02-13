package service;

import dao.InventoryQueryDAO;
import dao.InventoryQueryDAOImpl;
import vo.CategoryVO;
import vo.ProductInventoryCategoryVO;
import vo.ProductInventoryWarehouseVO;

import java.util.List;

public class InventoryQueryServiceImpl implements InventoryQueryService {
    private InventoryQueryDAO inventoryQueryDao;

    public InventoryQueryServiceImpl() {
        this.inventoryQueryDao = InventoryQueryDAOImpl.getInstance();
    }

    @Override
    public List<ProductInventoryWarehouseVO> getProductInventoryTotalByWarehouse() {
        return inventoryQueryDao.getInventoryTotalByWarehouse();
    }

    @Override
    public List<CategoryVO> getMainCategories() {
        return inventoryQueryDao.getMainCategories();
    }

    @Override
    public List<CategoryVO> getSubCategoriesByMainCategory(int mainCategoryNumber) {
        return inventoryQueryDao.getSubCategoriesByMainCategory(mainCategoryNumber);
    }

    @Override
    public List<CategoryVO> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        return inventoryQueryDao.getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
    }

    @Override
    public List<ProductInventoryCategoryVO> getInventoryByMainCategory(int categoryNumber) {
        return inventoryQueryDao.getInventoryByMainCategory(categoryNumber);
    }

    @Override
    public List<ProductInventoryCategoryVO> getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        return inventoryQueryDao.getInventoryBySubCategory(mainCategoryNumber, subCategoryNumber);
    }

    @Override
    public List<ProductInventoryCategoryVO> getInventoryByDetailCategory(int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber) {
        return inventoryQueryDao.getInventoryByDetailCategory(mainCategoryNumber, subCategoryNumber, detailCategoryNumber);
    }
}
