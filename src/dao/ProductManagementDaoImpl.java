package dao;

import util.DbConnection;
import vo.Category;
import vo.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementDaoImpl {
    private static DbConnection instance;
    static Connection conn;
    private PreparedStatement pstmt;
    List<Category> categoryList = new ArrayList<>();

    public ProductManagementDaoImpl(){

        try{

            conn = DbConnection.getInstance().getConnection();

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    public void productRegistration(Product product){

        String sql = new StringBuilder()
                .append("INSERT INTO TB_PRODUCT (V_PRODUCT_CD, V_PRODUCT_NM, " +
                        "N_PRODUCT_PRICE,V_PRODUCT_BRAND, V_PRODUCT_ORIGIN, V_PRODUCT_MANUFACTOR, " +
                        "V_PRODUCT_STATUS, D_PRODUCT_MANUFACTOR_DATE, V_CATEGORY_CD)")
                .append("VALUES (?,?,?,?,?,?,?,?,?)").toString();


        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,product.getProductCode());
            pstmt.setString(2, product.getProductName());
            pstmt.setInt(3, product.getProductPrice());
            pstmt.setString(4,product.getProductBrand());
            pstmt.setString(5,product.getProductOrign());
            pstmt.setString(6,product.getManufactor());
            pstmt.setString(7, "ON_SALE");
            pstmt.setDate(8,new java.sql.Date(System.currentTimeMillis()));
            pstmt.setString(9,product.getCategoryCode());
            pstmt.executeUpdate();

        }catch (SQLException s){
            s.printStackTrace();
        }


    }

    //대분류
    public List<Category> getMainCategories() {
        List<Category> categoryList = new ArrayList<>();
        String sql = new StringBuilder()
                .append("SELECT * FROM TB_CATEGORY WHERE V_CATEGORY_PARENT_CD IS NULL").toString();

        try{
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Category category = new Category();
                category.setCategoryCode(rs.getString("V_CATEGORY_CD"));
                category.setCategoryName(rs.getString("V_CATEGORY_NM"));
                category.setCategoryParentCode(rs.getString("V_CATEGORY_PARENT_CD"));
                categoryList.add(category);
            }

            pstmt.close();

        }catch (SQLException s){
            s.printStackTrace();
        }finally {
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

    public List<Category> getSubCategoriesByMainCategory(int mainCategoryNumber){

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
        }catch (SQLException s){
            s.printStackTrace();
        }

        return categoryList;
    }

    public List<Category> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        categoryList.clear();
        String sql = new StringBuilder().append("SELECT * FROM TB_CATEGORY ")
                .append("WHERE V_CATEGORY_PARENT_CD LIKE ?")
                .toString();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__"+ mainCategoryNumber + "___" + subCategoryNumber);
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


}
