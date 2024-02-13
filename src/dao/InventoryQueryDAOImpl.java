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

    private Connection conn;

    {
        try {
            conn = DbConnection.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement pstmt;
    private List<CategoryVO> categoryList = new ArrayList<>();
    private List<ProductInventoryCategoryVO> productInventoryCategoryList = new ArrayList<>();
    private List<String> warehouseCodeList = new ArrayList<>();
    private List<ProductInventoryWarehouseVO> productInventoryWarehouseList = new ArrayList<>();

    //상품별 창고 재고조회
    @Override
    public List<String> getWarehouseCode(){
        String sql = new StringBuilder().append("SELECT V_WAREHOUSE_CD FROM TB_WAREHOUSE").toString();

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
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT P.V_PRODUCT_CD, ");
        sqlBuilder.append("P.V_PRODUCT_NM, ");
        sqlBuilder.append("P.N_PRODUCT_PRICE, ");

        List<String> warehouseCodes = getWarehouseCode();
        for (String warehouseCode : warehouseCodes) {
            sqlBuilder.append("    SUM(CASE WHEN T.V_WAREHOUSE_CD = '")
                    .append(warehouseCode)
                    .append("' THEN T.N_INVENTORY_CNT ELSE 0 END) AS `")
                    .append(warehouseCode).append("`,");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" FROM TB_PRODUCT P LEFT JOIN ");
        sqlBuilder.append("TB_INVENTORY T ON P.V_PRODUCT_CD = T.V_PRODUCT_CD ");
        sqlBuilder.append("GROUP BY P.V_PRODUCT_CD");

        String sql = sqlBuilder.toString();

        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String productCode = rs.getString("P.V_PRODUCT_CD");
                String productName = rs.getString("P.V_PRODUCT_NM");

                ProductInventoryWarehouseVO productInventoryWarehouse = new ProductInventoryWarehouseVO();
                productInventoryWarehouse.setProductCode(productCode);
                productInventoryWarehouse.setProductName(productName);

                for (String warehouseCode : warehouseCodes) {
                    int inventoryCnt = rs.getInt(warehouseCode);
                    productInventoryWarehouse.setInventoryCnt(inventoryCnt);
                    productInventoryWarehouse.setWarehouseCode(warehouseCode);

                    ProductInventoryWarehouseVO newWarehouse = new ProductInventoryWarehouseVO();
                    newWarehouse.setProductCode(productCode);
                    newWarehouse.setProductName(productName);
                    newWarehouse.setInventoryCnt(inventoryCnt);
                    newWarehouse.setWarehouseCode(warehouseCode);
                    productInventoryWarehouseList.add(newWarehouse);
                }
            }

            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productInventoryWarehouseList;
    }

    //카테고리별 상품 재고조회
    @Override
    public List<CategoryVO> getMainCategories() {
        String sql = new StringBuilder().append("SELECT * FROM TB_CATEGORY WHERE V_CATEGORY_PARENT_CD IS NULL").toString();

        try {
            pstmt = conn.prepareStatement(sql);
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
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    @Override
    public List<CategoryVO> getSubCategoriesByMainCategory(int mainCategoryNumber) {
        categoryList.clear();

        String sql = new StringBuilder().append("SELECT * FROM TB_CATEGORY ")
                .append("WHERE V_CATEGORY_PARENT_CD LIKE ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__"+ mainCategoryNumber);
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
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    @Override
    public List<CategoryVO> getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        categoryList.clear();

        String sql = new StringBuilder().append("SELECT * FROM TB_CATEGORY ")
                .append("WHERE V_CATEGORY_PARENT_CD LIKE ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__"+ mainCategoryNumber + "___" + subCategoryNumber);
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
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    @Override
    public List<ProductInventoryCategoryVO> getInventoryByMainCategory(int mainCategoryNumber) {

        String sql = buildInventoryQuery();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__"+ mainCategoryNumber + "%");
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
            throw new RuntimeException(e);
        }
        return productInventoryCategoryList;
    }


    @Override
    public List<ProductInventoryCategoryVO> getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        productInventoryCategoryList.clear();

        String sql = buildInventoryQuery();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "__"+ mainCategoryNumber + "___" + subCategoryNumber + "%");
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
            throw new RuntimeException(e);
        }
        return productInventoryCategoryList;
    }

    @Override
    public List<ProductInventoryCategoryVO> getInventoryByDetailCategory(int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber) {
        productInventoryCategoryList.clear();

        String sql = buildInventoryQuery();

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
            throw new RuntimeException(e);
        }
        return productInventoryCategoryList;
    }

    private String buildInventoryQuery() {
        return new StringBuilder()
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
    }
}
