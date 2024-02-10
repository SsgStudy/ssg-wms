package dao;

        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Timestamp;
        import java.time.LocalDateTime;
        import java.util.ArrayList;
        import java.util.List;
        import util.DbConnection;
        import vo.IncomingVO;

public class IncomingDAOImpl implements IncomingDAO{
    private static IncomingDAOImpl instance;

    private IncomingDAOImpl() {}

    public static synchronized IncomingDAOImpl getInstance() {
        if (instance == null) {
            instance = new IncomingDAOImpl();
        }
        return instance;
    }

    public List<IncomingVO> getAllIncomingProductsWithDetails() {
        List<IncomingVO> incomingProducts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
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
                }                incomingProduct.setIncomingProductType(rs.getString("V_INCOMING_PRODUCT_TYPE"));
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
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return incomingProducts;
    }
    // 입고 상품 정보 수정
    public void updateIncomingProduct(long seq, String zoneCode, int count, int price) throws SQLException {
        String sql = "UPDATE TB_INCOMING_PRODUCT SET V_ZONE_CD = ?, N_INCOMING_PRODUCT_CNT = ?, N_INCOMING_PRODUCT_PRICE = ? WHERE PK_INCOMING_PRODUCT_SEQ = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
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
}
