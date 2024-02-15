package dao;

import util.DbConnection;
import util.enumcollect.PurchaseClaimEnum;
import util.enumcollect.PurchaseEnum;
import vo.PurchaseVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAOImpl implements PurchaseDAO{

    private static PurchaseDAOImpl instance;

    public PurchaseDAOImpl() {
    }

    public static synchronized PurchaseDAOImpl getInstance() {
        if (instance == null) {
            instance = new PurchaseDAOImpl();
        }
        return instance;
    }

    @Override
    public List<Long> getPurchaseByDateAndShopName(String startDate, String endDate, List<String> shopName) {
        List<Long> purchaseSeqList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            StringBuilder sql =
                    new StringBuilder("SELECT DISTINCT sp.PK_SHOP_PURCHASE_SEQ ")
                            .append("FROM TB_SHOP_PURCHASE sp " +
                                    "JOIN TB_SHOP s ON sp.PK_SHOP_CD = s.PK_SHOP_CD ")
                            .append("WHERE sp.DT_SHOP_PURCHASE_DATE BETWEEN ? AND ? ")
                            .append("AND (sp.V_SHOP_PURCHASE_STATUS IS NULL OR ")
                            .append("sp.V_SHOP_PURCHASE_STATUS='' OR ")
                            .append("sp.V_SHOP_PURCHASE_STATUS='신규등록') ")
                            .append("AND s.V_SHOP_NM IN (");

            for (int i=0; i<shopName.size(); i++) {
                sql.append("?");
                if (i<shopName.size()-1)
                    sql.append(", ");
            }
            sql.append(") ");
            sql.append("ORDER BY sp.PK_SHOP_PURCHASE_SEQ");

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
                purchase.setShopPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ"));
                purchaseSeqList.add(purchase.getShopPurchaseSeq());
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
        return purchaseSeqList;
    }

    @Override
    public List<Long> getClaimByDateAndShopName(String startDate, String endDate, List<String> shopName) {
        List<Long> purchaseClaimSeqList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            StringBuilder sql =
                    new StringBuilder("SELECT sp.PK_SHOP_PURCHASE_SEQ ")
                            .append("FROM TB_SHOP_PURCHASE sp " +
                                    "JOIN TB_SHOP s ON sp.PK_SHOP_CD = s.PK_SHOP_CD ")
                            .append("WHERE sp.DT_SHOP_PURCHASE_DATE BETWEEN ? AND ? ")
                            .append("AND sp.V_SHOP_PURCHASE_CLAIM IS NOT NULL ")
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
                purchase.setShopPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ"));
                purchaseClaimSeqList.add(purchase.getShopPurchaseSeq());
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
        return purchaseClaimSeqList;
    }

    @Override
    public int updatePurchaseStatus(List<Long> purchaseDetailSeq, PurchaseEnum purchaseStatus) {
        Connection conn;
        PreparedStatement pstmt = null;
        int updatedRows = 0;

        try {
            conn = DbConnection.getInstance().getConnection();
            StringBuilder sql = new StringBuilder("UPDATE TB_SHOP_PURCHASE ")
                    .append("SET V_SHOP_PURCHASE_STATUS = ? ")
                    .append("WHERE V_SHOP_PURCHASE_CLAIM IS NULL " )
                    .append("AND PK_SHOP_PURCHASE_SEQ IN ( ");
            for (int i = 0; i < purchaseDetailSeq.size(); i++) {
                sql.append("?");
                if (i < purchaseDetailSeq.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(" ) ");

            pstmt = conn.prepareStatement(sql.toString());

            pstmt.setString(1, purchaseStatus.name());

            int idx = 2;
            for (Long seq : purchaseDetailSeq) {
                pstmt.setLong(idx++, seq);
            }

            updatedRows = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return updatedRows;
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

    public List<PurchaseVO> getPurchaseListByPurchaseSeq(List<Long> purchaseSeq) {
        List<PurchaseVO> purchaseList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            StringBuilder sql =
                    new StringBuilder("SELECT sp.V_SHOP_PURCHASE_STATUS, sp.DT_SHOP_PURCHASE_DATE, sp.PK_SHOP_PURCHASE_SEQ," +
                            "sp.V_SHOP_PURCHASE_CLAIM, " +
                            "s.V_SHOP_NM, sp.V_SHOP_PURCHASE_NM, sp.V_SHOP_PURCHASE_TEL ")
                            .append("FROM TB_SHOP_PURCHASE sp " +
                                    "JOIN TB_SHOP s ON sp.PK_SHOP_CD = s.PK_SHOP_CD ")
                            .append("AND sp.PK_SHOP_PURCHASE_SEQ IN ( ");


            for (int i=0; i<purchaseSeq.size(); i++) {
                sql.append("?");
                if (i<purchaseSeq.size()-1)
                    sql.append(", ");
            }
            sql.append(") ");
            sql.append("ORDER BY sp.DT_SHOP_PURCHASE_DATE");

            pstmt = conn.prepareStatement(sql.toString());

            int idx = 1;
            for (Long seq: purchaseSeq) {
                pstmt.setLong(idx, seq);
                idx++;
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                PurchaseVO purchase = new PurchaseVO();
                if (rs.getString("V_SHOP_PURCHASE_STATUS") != null)
                    purchase.setShopPurchaseStatus(PurchaseEnum.valueOf(rs.getString("V_SHOP_PURCHASE_STATUS")));
                if (rs.getString("V_SHOP_PURCHASE_CLAIM") != null)
                    purchase.setShopPurchaseClaim(PurchaseClaimEnum.valueOf(rs.getString("V_SHOP_PURCHASE_CLAIM")));
                purchase.setShopPurchaseDate(rs.getDate("DT_SHOP_PURCHASE_DATE"));
                purchase.setShopPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ"));
                purchase.setShopName(rs.getString("V_SHOP_NM"));
                purchase.setShopPurchaseName(rs.getString("V_SHOP_PURCHASE_NM"));
                purchase.setShopPurchaseTel(rs.getString("V_SHOP_PURCHASE_TEL"));
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

    public List<PurchaseVO> getPurchaseClaimListByPurchaseSeq(List<Long> purchaseSeq) {
        List<PurchaseVO> purchaseList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            StringBuilder sql =
                    new StringBuilder("SELECT sp.V_SHOP_PURCHASE_STATUS, sp.DT_SHOP_PURCHASE_DATE, sp.PK_SHOP_PURCHASE_SEQ," +
                            "sp.V_SHOP_PURCHASE_CLAIM, " +
                            "s.V_SHOP_NM, sp.V_SHOP_PURCHASE_NM, sp.V_SHOP_PURCHASE_TEL ")
                            .append("FROM TB_SHOP_PURCHASE sp " +
                                    "JOIN TB_SHOP s ON sp.PK_SHOP_CD = s.PK_SHOP_CD ")
                            .append("WHERE sp.V_SHOP_PURCHASE_STATUS NOT IN ('주문취소완료', '반품완료') AND " )
                            .append("sp.PK_SHOP_PURCHASE_SEQ IN ( ");

            for (int i=0; i<purchaseSeq.size(); i++) {
                sql.append("?");
                if (i<purchaseSeq.size()-1)
                    sql.append(", ");
            }
            sql.append(") ");
            sql.append("ORDER BY sp.PK_SHOP_PURCHASE_SEQ");

            pstmt = conn.prepareStatement(sql.toString());

            int idx = 1;
            for (Long seq: purchaseSeq) {
                pstmt.setLong(idx, seq);
                idx++;
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                PurchaseVO purchase = new PurchaseVO();
                if (rs.getString("V_SHOP_PURCHASE_STATUS") != null)
                    purchase.setShopPurchaseStatus(PurchaseEnum.valueOf(rs.getString("V_SHOP_PURCHASE_STATUS")));
                if (rs.getString("V_SHOP_PURCHASE_CLAIM") != null)
                    purchase.setShopPurchaseClaim(PurchaseClaimEnum.valueOf(rs.getString("V_SHOP_PURCHASE_CLAIM")));
                purchase.setShopPurchaseDate(rs.getDate("DT_SHOP_PURCHASE_DATE"));
                purchase.setShopPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ"));
                purchase.setShopName(rs.getString("V_SHOP_NM"));
                purchase.setShopPurchaseName(rs.getString("V_SHOP_PURCHASE_NM"));
                purchase.setShopPurchaseTel(rs.getString("V_SHOP_PURCHASE_TEL"));
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
    public List<PurchaseVO> findAll() {
        List<PurchaseVO> purchaseList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            StringBuilder sql =
                    new StringBuilder("SELECT sp.V_SHOP_PURCHASE_STATUS, sp.DT_SHOP_PURCHASE_DATE, sp.PK_SHOP_PURCHASE_SEQ," +
                            "sp.V_SHOP_PURCHASE_CLAIM, spd.PK_SHOP_PURCHASE_DETAIL_SEQ, " +
                            "s.V_SHOP_NM, p.V_PRODUCT_NM, p.V_PRODUCT_BRAND, sp.V_SHOP_PURCHASE_NM, sp.V_SHOP_PURCHASE_TEL, " +
                            "spd.N_PRODUCT_CNT, p.N_PRODUCT_PRICE ")
                            .append("FROM TB_SHOP_PURCHASE sp " +
                                    "JOIN TB_SHOP s ON sp.PK_SHOP_CD = s.PK_SHOP_CD " +
                                    "JOIN TB_SHOP_PURCHASE_DETAIL spd ON sp.PK_SHOP_PURCHASE_SEQ = spd.PK_SHOP_PURCHASE_SEQ " +
                                    "JOIN TB_PRODUCT p ON spd.V_PRODUCT_CD = p.V_PRODUCT_CD ")
                            .append("ORDER BY sp.PK_SHOP_PURCHASE_SEQ");

            pstmt = conn.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                PurchaseVO purchase = new PurchaseVO();
                if (rs.getString("V_SHOP_PURCHASE_STATUS") != null &&
                        !rs.getString("V_SHOP_PURCHASE_STATUS").isEmpty())
                    purchase.setShopPurchaseStatus(PurchaseEnum.valueOf(rs.getString("V_SHOP_PURCHASE_STATUS")));
//                purchase.setShopPurchaseClaim(PurchaseClaimEnum.valueOf(rs.getString("V_SHOP_PURCHASE_CLAIM")));
                purchase.setShopPurchaseDate(rs.getDate("DT_SHOP_PURCHASE_DATE"));
                purchase.setShopPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ"));
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

    // 클레임 수집 시 상태 변경
    @Override
    public int updatePurchaseStatusCancelOrReturn(List<Long> purchaseSeqList) {
        Connection conn;
        PreparedStatement pstmt = null;
        int updatedRows = 0;

        try {
            conn = DbConnection.getInstance().getConnection();

            StringBuilder sql = new StringBuilder("UPDATE TB_SHOP_PURCHASE ")
                    .append("SET V_SHOP_PURCHASE_STATUS = CASE ")
                    .append("WHEN V_SHOP_PURCHASE_CLAIM = '취소' THEN ? ")
                    .append("ELSE V_SHOP_PURCHASE_STATUS END ")
                    .append("WHERE V_SHOP_PURCHASE_STATUS NOT IN ('주문취소완료', '반품완료') ")
                    .append("AND V_SHOP_PURCHASE_CLAIM != '반품' ")
                    .append("AND PK_SHOP_PURCHASE_SEQ IN ( ");


            for (int i = 0; i < purchaseSeqList.size(); i++) {
                sql.append("?");
                if (i < purchaseSeqList.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(" )");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, PurchaseEnum.주문취소접수.toString());
//            pstmt.setString(2, PurchaseEnum.반품접수.toString());

            int idx = 2;
            for (Long seq : purchaseSeqList) {
                pstmt.setLong(idx++, seq);
            }

            updatedRows = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return updatedRows;
    }

    public String processPurchaseCancelOrReturn(Long purchaseSeq) {
        Connection conn;
        CallableStatement cstmt = null;
        String result = "";

        try {
            conn = DbConnection.getInstance().getConnection();

            String procedure = "{call PROCESS_ORDER_CLAIM(?, ?)}";
            cstmt = conn.prepareCall(procedure);
            cstmt.setLong(1, purchaseSeq);
            
            cstmt.registerOutParameter(2, Types.VARCHAR);
            cstmt.execute();
            result = cstmt.getString(2);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cstmt != null) cstmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 반품 승인 시, 주문 테이블에 주문 반품건 하나 생성함
    @Override
    public Long createPurchaseReturn(Long purchaseSeq) {
        Connection conn;
        PreparedStatement pstmt = null;
        int updatedRows = 0;
        Long generatedKey = 0L;

        try {
            conn = DbConnection.getInstance().getConnection();

            String sql = new StringBuilder("INSERT INTO TB_SHOP_PURCHASE " +
                    "(PK_SHOP_CD, V_SHOP_PURCHASE_NM, V_SHOP_PURCHASE_ADDR, V_SHOP_PURCHASE_TEL, V_SHOP_PURCHASE_STATUS, V_SHOP_PURCHASE_CLAIM) ")
                    .append("SELECT PK_SHOP_CD, V_SHOP_PURCHASE_NM, V_SHOP_PURCHASE_ADDR, V_SHOP_PURCHASE_TEL, ? , V_SHOP_PURCHASE_CLAIM ")
                    .append("FROM TB_SHOP_PURCHASE ")
                    .append("WHERE PK_SHOP_PURCHASE_SEQ = ?").toString();

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, PurchaseEnum.반품접수.toString());
            pstmt.setLong(2, purchaseSeq);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedKey = generatedKeys.getLong(1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return generatedKey;
    }

    public int updatePurchaseCancelStatus(Long purchaseSeq, PurchaseEnum status) {
        Connection conn;
        PreparedStatement pstmt = null;
        int updatedRows = 0;

        try {
            conn = DbConnection.getInstance().getConnection();

            String sql = new StringBuilder("UPDATE TB_SHOP_PURCHASE ")
                    .append("SET V_SHOP_PURCHASE_STATUS = CASE ")
                    .append("WHEN V_SHOP_PURCHASE_CLAIM = '취소' THEN ? ")
                    .append(" ELSE V_SHOP_PURCHASE_STATUS END ")
                    .append("WHERE PK_SHOP_PURCHASE_SEQ IN ( ? )").toString();

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, status.toString());
            pstmt.setLong(2, purchaseSeq);
            updatedRows = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return updatedRows;
    }
}
