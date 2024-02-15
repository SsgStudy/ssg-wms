package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.DbConnection;
import util.enumcollect.OrderCollectionTypeEnum;
import util.enumcollect.OrderStatusEnum;
import util.enumcollect.PurchaseEnum;
import vo.OrderVO;
import vo.Product;
import vo.WareHouse;

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
                order.setOrderCnt(rs.getInt("N_ORDER_CNT"));
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

    @Override
    public List<OrderVO> getAllOrdersStatusProgress() {
        List<OrderVO> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = "SELECT O.PK_ORDER_SEQ, O.V_ORDER_STATUS, O.V_INCOMING_PRODUCT_SUPPLIER_NM, O.DT_DELIVERY_DATE, O.DT_ORDER_COMPLETION_DATE, " +
                    "OD.PK_ORDER_DETAIL_SEQ, OD.N_ORDER_CNT, OD.V_ORDER_STATUS AS V_ORDER_DETAIL_STATUS, OD.V_PRODUCT_CD, OD.V_WAREHOUSE_CD " +
                    "FROM TB_ORDER O JOIN TB_ORDER_DETAIL OD ON O.PK_ORDER_SEQ = OD.PK_ORDER_SEQ " +
                    "WHERE O.V_ORDER_STATUS LIKE ? ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, OrderStatusEnum.PROGRESS.toString());
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
                order.setOrderDetailStatus(OrderStatusEnum.valueOf(rs.getString("V_ORDER_DETAIL_STATUS")));
                order.setOrderCnt(rs.getInt("N_ORDER_CNT"));
                order.setProductCode(rs.getString("V_PRODUCT_CD"));
                order.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                orders.add(order);
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

        return orders;
    }

    // 발주 등록
    @Override
    public Long registerOrder(String deliveryDate, Product product) {
        Long orderSeq = 0l;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbConnection.getInstance().getConnection();

            // TB_ORDER에 주문 생성
            String insertOrderSql = "INSERT INTO TB_ORDER (V_ORDER_STATUS, V_INCOMING_PRODUCT_SUPPLIER_NM, DT_DELIVERY_DATE) " +
                                    "VALUES (?, ?, ?)";

            pstmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, OrderStatusEnum.PROGRESS.toString());
            pstmt.setString(2, product.getManufactor());
            pstmt.setString(3, deliveryDate);
            pstmt.executeUpdate();

            // 생성된 PK_ORDER_SEQ 가져오기
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderSeq = generatedKeys.getLong(1);
                }
            }

            // TB_ORDER_DETAIL에 주문 상세 추가
            String insertDetailSql = "INSERT INTO TB_ORDER_DETAIL (PK_ORDER_SEQ, N_ORDER_CNT, V_ORDER_STATUS, V_PRODUCT_CD, V_WAREHOUSE_CD) " +
                                        "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmtd = conn.prepareStatement(insertDetailSql)) {
                pstmtd.setLong(1, orderSeq);
                pstmtd.setInt(2, product.getInventoryCnt());
                pstmtd.setString(3, OrderStatusEnum.PROGRESS.toString());
                pstmtd.setString(4, product.getProductCode());
                pstmtd.setString(5, product.getWarehouseCode());
                pstmtd.executeUpdate();
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
        return orderSeq;
    }

    public List<Product> getAllProductQuantity() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM TB_PRODUCT;";
        Connection conn = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductCode(rs.getString("V_PRODUCT_CD"));
                product.setProductName(rs.getString("V_PRODUCT_NM"));
                product.setProductPrice(rs.getInt("N_PRODUCT_PRICE"));
                product.setProductBrand(rs.getString("V_PRODUCT_BRAND"));
                product.setProductOrign(rs.getString("V_PRODUCT_ORIGIN"));
                product.setManufactor(rs.getString("V_PRODUCT_MANUFACTOR"));
                productList.add(product);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productList;
    }

    @Override
    public OrderVO getOneOrderInformation(Long orderSeq) {
        OrderVO order = new OrderVO();
        String sql = "SELECT o.PK_ORDER_SEQ, o.V_ORDER_STATUS, o.V_INCOMING_PRODUCT_SUPPLIER_NM, o.DT_DELIVERY_DATE, o.DT_ORDER_COMPLETION_DATE, " +
                "d.PK_ORDER_DETAIL_SEQ, d.N_ORDER_CNT, d.V_ORDER_STATUS AS V_ORDER_DETAIL_STATUS, d.V_PRODUCT_CD, d.V_WAREHOUSE_CD " +
                "FROM TB_ORDER o JOIN TB_ORDER_DETAIL d ON o.PK_ORDER_SEQ = d.PK_ORDER_SEQ WHERE o.PK_ORDER_SEQ = ?";

        Connection conn = null;

        try {
            conn = DbConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderSeq);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                order.setOrderSeq(rs.getLong("PK_ORDER_SEQ"));
                order.setOrderStatus(rs.getString("V_ORDER_STATUS"));
                order.setIncomingProductSupplierName(rs.getString("V_INCOMING_PRODUCT_SUPPLIER_NM"));
                order.setDeliveryDate(rs.getTimestamp("DT_DELIVERY_DATE").toLocalDateTime());

                order.setOrderDetailSeq(rs.getLong("PK_ORDER_DETAIL_SEQ"));
                order.setOrderCnt(rs.getInt("N_ORDER_CNT"));
                order.setOrderDetailStatus(OrderStatusEnum.valueOf(rs.getString("V_ORDER_DETAIL_STATUS")));
                order.setProductCode(rs.getString("V_PRODUCT_CD"));
                order.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return order;
    }

    // 발주 상태 변경
    @Override
    public int updateOrderStatus(Long orderSeq) {
        Connection conn;
        PreparedStatement pstmt = null;
        int updatedRows = 0;

        try {
            conn = DbConnection.getInstance().getConnection();

            String sql = new StringBuilder("UPDATE TB_ORDER ")
                    .append("SET V_ORDER_STATUS = ? ")
                    .append("WHERE PK_ORDER_SEQ IN ( ? )").toString();

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, OrderStatusEnum.COMPLETE.name());
            pstmt.setLong(2, orderSeq);
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
