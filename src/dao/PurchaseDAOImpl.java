package dao;

import util.DbConnection;
import vo.OrderVO;
import vo.PurchaseVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAOImpl implements PurchaseDAO{

    @Override
    public List<PurchaseVO> findByDateAndShop(String startDate, String endDate, List<String> shopName) {
        List<PurchaseVO> purchaseList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            StringBuilder sql =
                    new StringBuilder("SELECT sp.V_SHOP_PURCHASE_STATUS, sp.DT_SHOP_PURCHASE_DATE, sp.PK_SHOP_PURCHASE_SEQ," +
                            "spd.PK_SHOP_PURCHASE_DETAIL_SEQ, " +
                            "s.V_SHOP_NM, p.V_PRODUCT_NM, p.V_PRODUCT_BRAND, sp.V_SHOP_PURCHASE_NM, sp.V_SHOP_PURCHASE_TEL, " +
                            "spd.N_PRODUCT_CNT, p.N_PRODUCT_PRICE ")
                            .append("FROM TB_SHOP_PURCHASE sp " +
                                    "JOIN TB_SHOP s ON sp.PK_SHOP_CD = s.PK_SHOP_CD\n" +
                                    "JOIN TB_SHOP_PURCHASE_DETAIL spd ON sp.PK_SHOP_PURCHASE_SEQ = spd.PK_SHOP_PURCHASE_SEQ\n" +
                                    "JOIN TB_PRODUCT p ON spd.V_PRODUCT_CD = p.V_PRODUCT_CD ")
                            .append("WHERE sp.DT_SHOP_PURCHASE_DATE BETWEEN ? AND ? ")
                            .append("AND s.V_SHOP_NM IN ( ");


            for (int i=0; i<shopName.size(); i++) {
                sql.append("?");
                if (i<shopName.size()-1)
                    sql.append(", ");
            }
            sql.append(") ");
            sql.append("ORDER BY sp.DT_SHOP_PURCHASE_DATE");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            int idx = 3;
            for (String name: shopName) {
                pstmt.setString(idx, name);
                idx++;
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                PurchaseVO purchase = new PurchaseVO();
                purchase.setShopPurchaseStatus(rs.getString("V_SHOP_PURCHASE_STATUS"));
                purchase.setShopPurchaseDate(rs.getDate("DT_SHOP_PURCHASE_DATE"));
                purchase.setShopPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ")); // 수정: PK_SHOP_PURCHASE_DETAIL_SEQ -> PK_SHOP_PURCHASE_SEQ
                purchase.setShopName(rs.getString("V_SHOP_NM"));
                purchase.setShopPurchaseName(rs.getString("V_SHOP_PURCHASE_NM"));
                purchase.setShopPurchaseTel(rs.getString("V_SHOP_PURCHASE_TEL"));
                purchase.setShopPurchaseDetailSeq(rs.getLong("PK_SHOP_PURCHASE_DETAIL_SEQ"));
                purchase.setProductCnt(rs.getInt("N_PRODUCT_CNT"));
                purchase.setProductName(rs.getString("V_PRODUCT_NM"));
                purchase.setProductBrand(rs.getString("V_PRODUCT_BRAND"));
                purchase.setProductPrice(rs.getInt("N_PRODUCT_PRICE"));
                purchaseList.add(purchase);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return purchaseList;
    }

    @Override
    public void updatePurchaseStatus() {

    }

    @Override
    public List<String> findShopName() {
        List<String> shopNameList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM TB_SHOP";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("V_SHOP_NM");
                shopNameList.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return shopNameList;
    }
}
