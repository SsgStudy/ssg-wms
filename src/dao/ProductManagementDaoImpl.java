package dao;

import util.DbConnection;
import util.enumcollect.SalesStatus;
import vo.Category;
import vo.Member;
import vo.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProductManagementDaoImpl implements ProductManagementDao {

    private PreparedStatement pstmt;
    List<Category> categoryList = new ArrayList<>();

    private static ProductManagementDaoImpl instance;
    private static Connection conn;

    private ProductManagementDaoImpl() {
        try {
            conn = DbConnection.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized ProductManagementDaoImpl getInstance() {
        if (instance == null) {
            instance = new ProductManagementDaoImpl();
        }
        return instance;
    }


    private static Logger logger = Logger.getLogger(ProductManagementDaoImpl.class.getName());

    @Override
    public void insertProduct(Product product) {

        String sql = new StringBuilder()
                .append("INSERT INTO TB_PRODUCT (V_PRODUCT_CD, V_PRODUCT_NM, " +
                        "N_PRODUCT_PRICE,V_PRODUCT_BRAND, V_PRODUCT_ORIGIN, V_PRODUCT_MANUFACTOR, " +
                        "V_PRODUCT_STATUS, D_PRODUCT_MANUFACTOR_DATE, V_CATEGORY_CD)")
                .append("VALUES (?,?,?,?,?,?,?,?,?)").toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, product.getProductCode());
            pstmt.setString(2, product.getProductName());
            pstmt.setInt(3, product.getProductPrice());
            pstmt.setString(4, product.getProductBrand());
            pstmt.setString(5, product.getProductOrign());
            pstmt.setString(6, product.getManufactor());
            pstmt.setString(7, "ON_SALE");
            pstmt.setDate(8, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setString(9, product.getCategoryCode());
            pstmt.executeUpdate();
            System.out.println("상품등록이 완료되었습니다.");

        } catch (SQLIntegrityConstraintViolationException si) {
            logger.warning("상품코드가 이미 생성되어 있습니다.");
            si.printStackTrace();
        } catch (SQLException s) {
            s.printStackTrace();
        }


    }

    @Override
    public List<Category> getMainCategories() {
        List<Category> categoryList = new ArrayList<>();
        String sql = new StringBuilder()
                .append("SELECT * FROM TB_CATEGORY WHERE V_CATEGORY_PARENT_CD IS NULL").toString();

        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Category category = new Category();
                category.setCategoryCode(rs.getString("V_CATEGORY_CD"));
                category.setCategoryName(rs.getString("V_CATEGORY_NM"));
                category.setCategoryParentCode(rs.getString("V_CATEGORY_PARENT_CD"));
                categoryList.add(category);
            }

            pstmt.close();

        } catch (SQLException s) {
            s.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return categoryList;
    }

    public List<Category> getSubCategoriesByMainCategory(int mainCategoryNumber) {

        String sql = new StringBuilder().append("SELECT * FROM TB_CATEGORY ")
                .append("WHERE V_CATEGORY_PARENT_CD LIKE ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__" + mainCategoryNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryCode(rs.getString("V_CATEGORY_CD"));
                category.setCategoryName(rs.getString("V_CATEGORY_NM"));
                category.setCategoryParentCode(rs.getString("V_CATEGORY_PARENT_CD"));
                categoryList.add(category);
            }
            pstmt.close();
        } catch (SQLException s) {
            s.printStackTrace();
        }

        return categoryList;
    }

    @Override
    public List<Category> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        categoryList.clear();
        String sql = new StringBuilder().append("SELECT * FROM TB_CATEGORY ")
                .append("WHERE V_CATEGORY_PARENT_CD LIKE ?")
                .toString();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__" + mainCategoryNumber + "___" + subCategoryNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryCode(rs.getString("V_CATEGORY_CD"));
                category.setCategoryName(rs.getString("V_CATEGORY_NM"));
                category.setCategoryParentCode(rs.getString("V_CATEGORY_PARENT_CD"));
                categoryList.add(category);
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    @Override
    public List<Product> getProductList() {
        List<Product> productList = new ArrayList<>();

        String sql = new StringBuilder().append("SELECT * FROM TB_PRODUCT").toString();

        try {

            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductCode(rs.getString("V_PRODUCT_CD"));
                product.setProductName(rs.getString("V_PRODUCT_NM"));
                product.setProductPrice(rs.getInt("N_PRODUCT_PRICE"));
                productList.add(product);

            }
            rs.close();
            pstmt.close();

        } catch (SQLException s) {
            s.printStackTrace();
        }

        return productList;

    }

    @Override
    public Product getProductByProductCode(String productcode) {
        Product product = new Product();

        String sql = new StringBuilder().append("SELECT * FROM TB_PRODUCT ")
                .append("WHERE V_PRODUCT_CD = ?").toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productcode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                product.setProductCode(rs.getString("V_PRODUCT_CD"));
                product.setProductName(rs.getString("V_PRODUCT_NM"));
                product.setProductPrice(rs.getInt("N_PRODUCT_PRICE"));
                product.setProductBrand(rs.getString("V_PRODUCT_BRAND"));
                product.setProductOrign(rs.getString("V_PRODUCT_ORIGIN"));
                product.setManufactor(rs.getString("V_PRODUCT_MANUFACTOR"));
                product.setPrductStatus(SalesStatus.valueOf(rs.getString("V_PRODUCT_STATUS")));
                product.setProductManufactorsDate(rs.getDate("D_PRODUCT_MANUFACTOR_DATE"));
                product.setCategoryCode(rs.getString("V_CATEGORY_CD"));
            } else {
                return null;
            }


        } catch (SQLException s) {
            s.printStackTrace();
        }

        return product;

    }

    @Override
    public int updateProductByProductCode(String productCode, Product product) {

        int row = 0;

        String sql = new StringBuilder().append("UPDATE TB_PRODUCT SET ")
                .append("V_PRODUCT_NM=?, ")
                .append("N_PRODUCT_PRICE=?,")
                .append("V_PRODUCT_BRAND=?,")
                .append("V_PRODUCT_ORIGIN=?,")
                .append("V_PRODUCT_MANUFACTOR=?,")
                .append("V_PRODUCT_STATUS=? ")
                .append("WHERE V_PRODUCT_CD=? ").toString();

        try {

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getProductPrice());
            pstmt.setString(3, product.getProductBrand());
            pstmt.setString(4, product.getProductOrign());
            pstmt.setString(5, product.getManufactor());
            pstmt.setString(6, product.getPrductStatus().toString());
            pstmt.setString(7, productCode);

            row = pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException s) {
            s.printStackTrace();
        }

        return row;

    }


}
