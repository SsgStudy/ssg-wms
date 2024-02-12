package service;

import dao.ProductManagementDaoImpl;
import vo.Category;
import vo.Product;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

public class ProductServiceImpl implements ProductService {
    private ProductManagementDaoImpl productDao = new ProductManagementDaoImpl();

    public ProductServiceImpl() {
        this.productDao = new ProductManagementDaoImpl();
    }

    private static Logger logger = Logger.getLogger(ProductServiceImpl.class.getName());

    public void insertProduct(Product product) {
        try {
            // 데이터베이스에 상품 정보 등록
            productDao.insertProduct(product);

        } catch (Exception e) {
            logger.info("상품 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    public List<Category> getMainCategories() {
        return productDao.getMainCategories();
    }

    public List<Category> getSubCategoriesByMainCategory(int mainCategoryNumber) {
        return productDao.getSubCategoriesByMainCategory(mainCategoryNumber);
    }

    public List<Category> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        return productDao.getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
    }

    public List<Product> getProductList() {
        return productDao.getProductList();
    }

    public Product getProductByProductCode(String productCode) {
        return productDao.getProductByProductCode(productCode);
    }

    public int updateProductByProductCode(String productCode, Product product) {
        return productDao.updateProductByProductCode(productCode, product);
    }

}
