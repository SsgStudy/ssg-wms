package service;

import dao.ProductManagementDaoImpl;
import vo.Category;
import vo.Product;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductManagementDaoImpl productDao = new ProductManagementDaoImpl();

    public ProductServiceImpl() {
        this.productDao = new ProductManagementDaoImpl();
    }

    public void productRegistration(Product product) {
        try {
            // 데이터베이스에 상품 정보 등록
            productDao.productRegistration(product);
            System.out.println("상품 등록이 완료되었습니다.");
        } catch (Exception e) {
            System.out.println("상품 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    public List<Category> getMainCategories() {
        return productDao.getMainCategories();
    }

    public List<Category> getSubCategoriesByMainCategory(int mainCategoryNumber){
        return productDao.getSubCategoriesByMainCategory(mainCategoryNumber);
    }

    public List<Category> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber){
        return productDao.getDetailCategoriesBySubCategory(mainCategoryNumber,subCategoryNumber);
    }

}
