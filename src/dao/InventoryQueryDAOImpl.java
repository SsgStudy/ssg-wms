package dao;

import util.DbConnection;
import vo.CategoryVO;
import vo.ProductInventoryCategoryVO;
import vo.ProductInventoryWarehouseVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryQueryDAOImpl implements InventoryQueryDAO {
    private static InventoryQueryDAOImpl instance;
    private static Connection conn;
    private PreparedStatement pstmt;

    private InventoryQueryDAOImpl() {
        try {
            conn = DbConnection.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized InventoryQueryDAOImpl getInstance() {
        if (instance == null) {
            instance = new InventoryQueryDAOImpl();
        }
        return instance;
    }

    @Override
    public List<String> getWarehouseCode() {
        List<String> warehouseCodeList = new ArrayList<>();
        String sql = "SELECT V_WAREHOUSE_CD FROM TB_WAREHOUSE";

        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                warehouseCodeList.add(rs.getString("V_WAREHOUSE_CD"));
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return warehouseCodeList;
    }


    @Override
    public List<ProductInventoryWarehouseVO> getInventoryTotalByWarehouse() {
        List<ProductInventoryWarehouseVO> productInventoryWarehouseList = new ArrayList<>();
        String sql = "SELECT P.V_PRODUCT_CD, P.V_PRODUCT_NM, SUM(I.N_INVENTORY_CNT) AS TOTAL_CNT, I.V_WAREHOUSE_CD " +
                "FROM TB_INVENTORY I INNER JOIN TB_PRODUCT P ON I.V_PRODUCT_CD = P.V_PRODUCT_CD " +
                "GROUP BY I.V_WAREHOUSE_CD, P.V_PRODUCT_CD";

        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProductInventoryWarehouseVO piw = new ProductInventoryWarehouseVO();
                piw.setProductCode(rs.getString("V_PRODUCT_CD"));
                piw.setProductName(rs.getString("V_PRODUCT_NM"));
                piw.setInventoryCnt(rs.getInt("TOTAL_CNT"));
                piw.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                productInventoryWarehouseList.add(piw);
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productInventoryWarehouseList;
    }

    @Override
    public List<CategoryVO> getMainCategories() {
        List<CategoryVO> categoryList = new ArrayList<>();
        String sql = "SELECT * FROM TB_CATEGORY WHERE V_CATEGORY_PARENT_CD IS NULL";
        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CategoryVO category = new CategoryVO();
                category.setCategoryCode(rs.getString("V_CATEGORY_CD"));
                category.setCategoryName(rs.getString("V_CATEGORY_NM"));
                categoryList.add(category);
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting main categories", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    @Override
    public List<CategoryVO> getSubCategoriesByMainCategory(int mainCategoryNumber) {
        List<CategoryVO> categoryList = new ArrayList<>();
        String sql = new StringBuilder().append("SELECT * FROM TB_CATEGORY ")
                .append("WHERE V_CATEGORY_PARENT_CD LIKE ?")
                .toString();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__" + mainCategoryNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CategoryVO category = new CategoryVO();
                category.setCategoryCode(rs.getString("V_CATEGORY_CD"));
                category.setCategoryName(rs.getString("V_CATEGORY_NM"));
                category.setCategoryParentCode(rs.getString("V_CATEGORY_PARENT_CD"));
                categoryList.add(category);
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting sub categories by main category", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    @Override
    public List<CategoryVO> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        List<CategoryVO> categoryList = new ArrayList<>();
        String sql = new StringBuilder().append("SELECT * FROM TB_CATEGORY ")
                .append("WHERE V_CATEGORY_PARENT_CD LIKE ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__" + mainCategoryNumber + "___" + subCategoryNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CategoryVO category = new CategoryVO();
                category.setCategoryCode(rs.getString("V_CATEGORY_CD"));
                category.setCategoryName(rs.getString("V_CATEGORY_NM"));
                category.setCategoryParentCode(rs.getString("V_CATEGORY_PARENT_CD"));
                categoryList.add(category);
            }

            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting detail categories by sub category", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    @Override
    public List<ProductInventoryCategoryVO> getInventoryByMainCategory(int mainCategoryNumber) {
        List<ProductInventoryCategoryVO> productInventoryCategoryList = new ArrayList<>();
        String sql = new StringBuilder()
                .append("SELECT P.V_PRODUCT_CD, ")
                .append("P.V_PRODUCT_NM, ")
                .append("P.N_PRODUCT_PRICE, ")
                .append("SUM(N_INVENTORY_CNT) AS N_TOTAL_INVENTORY_CNT, ")
                .append("P.V_CATEGORY_CD ")
                .append("FROM TB_PRODUCT P ")
                .append("LEFT JOIN TB_INVENTORY T ")
                .append("ON P.V_PRODUCT_CD = T.V_PRODUCT_CD ")
                .append("WHERE V_CATEGORY_CD LIKE ?")
                .append("GROUP BY V_PRODUCT_CD")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__" + mainCategoryNumber + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductInventoryCategoryVO productInventory = new ProductInventoryCategoryVO();
                productInventory.setProductCode(rs.getString("P.V_PRODUCT_CD"));
                productInventory.setProductName(rs.getString("P.V_PRODUCT_NM"));
                productInventory.setProductPrice(rs.getInt("P.N_PRODUCT_PRICE"));
                productInventory.setTotalInventoryCnt(rs.getInt("N_TOTAL_INVENTORY_CNT"));
                productInventory.setCategoryCode(rs.getString("P.V_CATEGORY_CD"));

                productInventoryCategoryList.add(productInventory);
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting inventory by main category", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productInventoryCategoryList;
    }

    @Override
    public List<ProductInventoryCategoryVO> getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        List<ProductInventoryCategoryVO> productInventoryCategoryList = new ArrayList<>();
        String sql = new StringBuilder()
                .append("SELECT P.V_PRODUCT_CD, ")
                .append("P.V_PRODUCT_NM, ")
                .append("P.N_PRODUCT_PRICE, ")
                .append("SUM(N_INVENTORY_CNT) AS N_TOTAL_INVENTORY_CNT, ")
                .append("P.V_CATEGORY_CD ")
                .append("FROM TB_PRODUCT P ")
                .append("LEFT JOIN TB_INVENTORY T ")
                .append("ON P.V_PRODUCT_CD = T.V_PRODUCT_CD ")
                .append("WHERE V_CATEGORY_CD LIKE ?")
                .append("GROUP BY V_PRODUCT_CD")
                .toString();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__" + mainCategoryNumber + "___" + subCategoryNumber + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductInventoryCategoryVO productInventory = new ProductInventoryCategoryVO();
                productInventory.setProductCode(rs.getString("P.V_PRODUCT_CD"));
                productInventory.setProductName(rs.getString("P.V_PRODUCT_NM"));
                productInventory.setProductPrice(rs.getInt("P.N_PRODUCT_PRICE"));
                productInventory.setTotalInventoryCnt(rs.getInt("N_TOTAL_INVENTORY_CNT"));
                productInventory.setCategoryCode(rs.getString("P.V_CATEGORY_CD"));

                productInventoryCategoryList.add(productInventory);
            }

            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting inventory by sub category", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productInventoryCategoryList;
    }

    @Override
    public List<ProductInventoryCategoryVO> getInventoryByDetailCategory(int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber) {
        List<ProductInventoryCategoryVO> productInventoryCategoryList = new ArrayList<>();
        String sql = new StringBuilder()
                .append("SELECT P.V_PRODUCT_CD, ")
                .append("P.V_PRODUCT_NM, ")
                .append("P.N_PRODUCT_PRICE, ")
                .append("SUM(N_INVENTORY_CNT) AS N_TOTAL_INVENTORY_CNT, ")
                .append("P.V_CATEGORY_CD ")
                .append("FROM TB_PRODUCT P ")
                .append("LEFT JOIN TB_INVENTORY T ")
                .append("ON P.V_PRODUCT_CD = T.V_PRODUCT_CD ")
                .append("WHERE V_CATEGORY_CD LIKE ?")
                .append("GROUP BY V_PRODUCT_CD")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__"+ mainCategoryNumber + "___" + subCategoryNumber + "___" + detailCategoryNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductInventoryCategoryVO productInventory = new ProductInventoryCategoryVO();
                productInventory.setProductCode(rs.getString("P.V_PRODUCT_CD"));
                productInventory.setProductName(rs.getString("P.V_PRODUCT_NM"));
                productInventory.setProductPrice(rs.getInt("P.N_PRODUCT_PRICE"));
                productInventory.setTotalInventoryCnt(rs.getInt("N_TOTAL_INVENTORY_CNT"));
                productInventory.setCategoryCode(rs.getString("P.V_CATEGORY_CD"));

                productInventoryCategoryList.add(productInventory);
            }

            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting inventory by detail category", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productInventoryCategoryList;
    }
}
