package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import util.DbConnection;
import vo.OrderVO;

public class OrderDAOImpl implements OrderDAO {
    private static OrderDAOImpl instance;

    private OrderDAOImpl() {}

    public static synchronized OrderDAOImpl getInstance() {
        if (instance == null) {
            instance = new OrderDAOImpl();
        }
        return instance;
    }

    @Override
    public List<OrderVO> getAllOrdersWithDetails() {
        List<OrderVO> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = "SELECT * FROM TB_ORDER O JOIN TB_ORDER_DETAIL OD ON O.PK_ORDER_SEQ = OD.PK_ORDER_SEQ";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                OrderVO order = new OrderVO();
                order.setOrderSeq(rs.getLong("PK_ORDER_SEQ"));
                order.setOrderStatus(rs.getString("V_ORDER_STATUS"));
                order.setIncomingProductSupplierName(rs.getString("V_INCOMING_PRODUCT_SUPPLIER_NM"));
                order.setDeliveryDate(rs.getTimestamp("DT_DELIVERY_DATE").toLocalDateTime());
                Timestamp completionTimestamp = rs.getTimestamp("DT_ORDER_COMPLETION_DATE");
                if (completionTimestamp != null) {
                    order.setOrderCompletionDate(completionTimestamp.toLocalDateTime());
                }
                order.setOrderDetailSeq(rs.getLong("PK_ORDER_DETAIL_SEQ"));
                order.setOrderCount(rs.getInt("N_ORDER_CNT"));
                order.setProductCode(rs.getString("V_PRODUCT_CD"));
                order.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 자원 해제
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return orders;
    }

}
