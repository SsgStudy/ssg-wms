package service;

import dao.ProductManagementDaoImpl;
import vo.Category;
import vo.Product;

import java.util.List;
import java.util.logging.Logger;

/**
 * The type Product service.
 */
public class ProductServiceImpl implements ProductService {
    private static ProductServiceImpl instance;
    private ProductManagementDaoImpl productDao;

    /**
     * Instantiates a new Product service.
     */
    public ProductServiceImpl() {
        this.productDao = ProductManagementDaoImpl.getInstance();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized ProductServiceImpl getInstance() {
        if (instance == null) {
            instance = new ProductServiceImpl();
        }
        return instance;
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
