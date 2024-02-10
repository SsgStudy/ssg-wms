package dao;

        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Timestamp;
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
}
