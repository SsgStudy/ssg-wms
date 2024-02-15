package dao;

import static util.DbConnection.getConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.DbConnection;
import vo.DetailedIncomingVO;
import vo.IncomingVO;

public class IncomingDAOImpl implements IncomingDAO {
//    private static Connection conn;

    private static IncomingDAOImpl instance;

    private IncomingDAOImpl() {
        try {
            Connection conn = DbConnection.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized IncomingDAOImpl getInstance() {
        if (instance == null) {
            instance = new IncomingDAOImpl();
        }
        return instance;
    }

    public List<IncomingVO> getAllIncomingProductsWithDetails() {
        List<IncomingVO> incomingProducts = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM TB_INCOMING_PRODUCT";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                IncomingVO incomingProduct = new IncomingVO();
                incomingProduct.setIncomingProductSeq(rs.getLong("PK_INCOMING_PRODUCT_SEQ"));
                incomingProduct.setIncomingProductStatus(rs.getString("V_INCOMING_PRODUCT_STATUS"));
                Timestamp incomingProductDateTimestamp = rs.getTimestamp("DT_INCOMING_PRODUCT_DATE");
                if (incomingProductDateTimestamp != null) {
                    incomingProduct.setIncomingProductDate(incomingProductDateTimestamp.toLocalDateTime());
                }
                incomingProduct.setIncomingProductType(rs.getString("V_INCOMING_PRODUCT_TYPE"));
                incomingProduct.setIncomingProductSupplierName(rs.getString("V_INCOMING_PRODUCT_SUPPLIER_NM"));
                incomingProduct.setIncomingProductCount(rs.getInt("N_INCOMING_PRODUCT_CNT"));
                incomingProduct.setIncomingProductPrice(rs.getInt("N_INCOMING_PRODUCT_PRICE"));
                incomingProduct.setProductCode(rs.getString("V_PRODUCT_CD"));
                incomingProduct.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                incomingProduct.setZoneCode(rs.getString("V_ZONE_CD"));
                incomingProducts.add(incomingProduct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return incomingProducts;
    }

    // 입고 상품 정보 수정
    public void updateIncomingProduct(long seq, String zoneCode, int count, int price) throws SQLException {
        String sql = "UPDATE TB_INCOMING_PRODUCT SET V_ZONE_CD = ?, N_INCOMING_PRODUCT_CNT = ?, N_INCOMING_PRODUCT_PRICE = ? WHERE PK_INCOMING_PRODUCT_SEQ = ?";

        try (
                Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, zoneCode);
            stmt.setInt(2, count);
            stmt.setInt(3, price);
            stmt.setLong(4, seq);

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("입고 상품 정보 수정 중 오류 발생", e);
        }
    }

    // 입고 상품 승인 (상태를 COMPLETE로 변경)
    public void approveIncomingProduct(long seq) throws Exception {
        String sql = "UPDATE TB_INCOMING_PRODUCT SET V_INCOMING_PRODUCT_STATUS = 'COMPLETE', DT_INCOMING_PRODUCT_DATE = ? WHERE PK_INCOMING_PRODUCT_SEQ = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, seq);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("입고 상품 승인 중 오류 발생", e);
        }
    }

    public List<IncomingVO> getIncomingProductsByMonth(int year, int month) throws SQLException {
        List<IncomingVO> incomingProducts = new ArrayList<>();
        String sql = "SELECT * FROM TB_INCOMING_PRODUCT WHERE YEAR(DT_INCOMING_PRODUCT_DATE) = ? AND MONTH(DT_INCOMING_PRODUCT_DATE) = ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    IncomingVO incomingProduct = new IncomingVO();
                    incomingProduct.setIncomingProductSeq(rs.getLong("PK_INCOMING_PRODUCT_SEQ"));
                    incomingProduct.setIncomingProductStatus(rs.getString("V_INCOMING_PRODUCT_STATUS"));
                    Timestamp incomingProductDateTimestamp = rs.getTimestamp("DT_INCOMING_PRODUCT_DATE");
                    if (incomingProductDateTimestamp != null) {
                        incomingProduct.setIncomingProductDate(incomingProductDateTimestamp.toLocalDateTime());
                    }
                    incomingProduct.setIncomingProductType(rs.getString("V_INCOMING_PRODUCT_TYPE"));
                    incomingProduct.setIncomingProductSupplierName(rs.getString("V_INCOMING_PRODUCT_SUPPLIER_NM"));
                    incomingProduct.setIncomingProductCount(rs.getInt("N_INCOMING_PRODUCT_CNT"));
                    incomingProduct.setIncomingProductPrice(rs.getInt("N_INCOMING_PRODUCT_PRICE"));
                    incomingProduct.setProductCode(rs.getString("V_PRODUCT_CD"));
                    incomingProduct.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                    incomingProduct.setZoneCode(rs.getString("V_ZONE_CD"));
                    incomingProducts.add(incomingProduct);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return incomingProducts;
    }

    public List<IncomingVO> getIncomingProductsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<IncomingVO> incomingProducts = new ArrayList<>();
        String sql = "SELECT * FROM TB_INCOMING_PRODUCT WHERE DT_INCOMING_PRODUCT_DATE BETWEEN ? AND ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    IncomingVO incomingProduct = new IncomingVO();
                    incomingProduct.setIncomingProductSeq(rs.getLong("PK_INCOMING_PRODUCT_SEQ"));
                    incomingProduct.setIncomingProductStatus(rs.getString("V_INCOMING_PRODUCT_STATUS"));
                    Timestamp incomingProductDateTimestamp = rs.getTimestamp("DT_INCOMING_PRODUCT_DATE");
                    if (incomingProductDateTimestamp != null) {
                        incomingProduct.setIncomingProductDate(incomingProductDateTimestamp.toLocalDateTime());
                    }
                    incomingProduct.setIncomingProductType(rs.getString("V_INCOMING_PRODUCT_TYPE"));
                    incomingProduct.setIncomingProductSupplierName(rs.getString("V_INCOMING_PRODUCT_SUPPLIER_NM"));
                    incomingProduct.setIncomingProductCount(rs.getInt("N_INCOMING_PRODUCT_CNT"));
                    incomingProduct.setIncomingProductPrice(rs.getInt("N_INCOMING_PRODUCT_PRICE"));
                    incomingProduct.setProductCode(rs.getString("V_PRODUCT_CD"));
                    incomingProduct.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                    incomingProduct.setZoneCode(rs.getString("V_ZONE_CD"));
                    incomingProducts.add(incomingProduct);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return incomingProducts;
    }

    public DetailedIncomingVO getIncomingProductDetailsWithProductInfo(long seq) throws Exception {
        DetailedIncomingVO detailedIncoming = null;
        String sql =
                "SELECT ip.*, p.V_PRODUCT_NM, p.N_PRODUCT_PRICE, p.V_PRODUCT_BRAND, p.V_PRODUCT_ORIGIN, p.V_PRODUCT_MANUFACTOR, p.V_PRODUCT_STATUS, p.D_PRODUCT_MANUFACTOR_DATE, p.V_CATEGORY_CD "
                        +
                        "FROM TB_INCOMING_PRODUCT ip " +
                        "JOIN TB_PRODUCT p ON ip.V_PRODUCT_CD = p.V_PRODUCT_CD " +
                        "WHERE ip.PK_INCOMING_PRODUCT_SEQ = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, seq);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    detailedIncoming = new DetailedIncomingVO();

                    detailedIncoming.setIncomingProductSeq(rs.getLong("PK_INCOMING_PRODUCT_SEQ"));
                    detailedIncoming.setIncomingProductStatus(rs.getString("V_INCOMING_PRODUCT_STATUS"));
                    Timestamp incomingProductDateTimestamp = rs.getTimestamp("DT_INCOMING_PRODUCT_DATE");
                    if (incomingProductDateTimestamp != null) {
                        detailedIncoming.setIncomingProductDate(incomingProductDateTimestamp.toLocalDateTime());
                    }
                    detailedIncoming.setIncomingProductType(rs.getString("V_INCOMING_PRODUCT_TYPE"));
                    detailedIncoming.setIncomingProductSupplierName(rs.getString("V_INCOMING_PRODUCT_SUPPLIER_NM"));
                    detailedIncoming.setIncomingProductCount(rs.getInt("N_INCOMING_PRODUCT_CNT"));
                    detailedIncoming.setIncomingProductPrice(rs.getInt("N_INCOMING_PRODUCT_PRICE"));
                    detailedIncoming.setProductCode(rs.getString("V_PRODUCT_CD"));
                    detailedIncoming.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                    detailedIncoming.setZoneCode(rs.getString("V_ZONE_CD"));

                    detailedIncoming.setProductName(rs.getString("V_PRODUCT_NM"));
                    detailedIncoming.setProductPrice(rs.getInt("N_PRODUCT_PRICE"));
                    detailedIncoming.setProductBrand(rs.getString("V_PRODUCT_BRAND"));
                    detailedIncoming.setProductOrigin(rs.getString("V_PRODUCT_ORIGIN"));
                    detailedIncoming.setProductManufactor(rs.getString("V_PRODUCT_MANUFACTOR"));
                    detailedIncoming.setProductStatus(rs.getString("V_PRODUCT_STATUS"));
                    Timestamp productManufactorDateTimestamp = rs.getTimestamp("D_PRODUCT_MANUFACTOR_DATE");
                    if (productManufactorDateTimestamp != null) {
                        detailedIncoming.setProductManufactorDate(productManufactorDateTimestamp.toLocalDateTime());
                    }
                    detailedIncoming.setCategoryCode(rs.getString("V_CATEGORY_CD"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("데이터베이스 조회 중 예외 발생", e);
        }
        return detailedIncoming;

    }

}
