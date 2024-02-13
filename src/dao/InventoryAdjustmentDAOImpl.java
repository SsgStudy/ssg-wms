package dao;

import util.DbConnection;
import util.enumcollect.PurchaseEnum;
import vo.InventoryVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryAdjustmentDAOImpl implements InventoryAdjustmentDAO {

    private static InventoryAdjustmentDAOImpl instance;

    private InventoryAdjustmentDAOImpl() {
    }

    public static synchronized InventoryAdjustmentDAOImpl getInstance() {
        if (instance == null) {
            instance = new InventoryAdjustmentDAOImpl();
        }
        return instance;
    }

    @Override
    public List<InventoryVO> getInventoryInformation() {
        List<InventoryVO> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM TB_INVENTORY";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                InventoryVO inventory = new InventoryVO();
                inventory.setInventorySeq(rs.getInt("PK_INVENTORY_SEQ"));
                inventory.setInventorySlipDate(rs.getString("DT_INVENTORY_SLIP_DATE"));
                inventory.setInventoryCnt(rs.getInt("N_INVENTORY_CNT"));
                inventory.setZoneCd(rs.getString("V_ZONE_CD"));
                inventory.setWarehouseCd(rs.getString("V_WAREHOUSE_CD"));
                inventory.setProductCd(rs.getString("V_PRODUCT_CD"));
                inventoryList.add(inventory);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inventoryList;
    }

    @Override
    public int updateIncreaseInventoryQuantity(int selectedNumber, int adjustedQuantity) {
        String sql = "UPDATE TB_INVENTORY SET N_INVENTORY_CNT = N_INVENTORY_CNT + ? WHERE PK_INVENTORY_SEQ = ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adjustedQuantity);
            pstmt.setInt(2, selectedNumber);
            return pstmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateDecreaseInventoryQuantity(int selectedNumber, int adjustedQuantity) {
        String sql = "UPDATE TB_INVENTORY SET N_INVENTORY_CNT = N_INVENTORY_CNT - ? WHERE PK_INVENTORY_SEQ = ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adjustedQuantity);
            pstmt.setInt(2, selectedNumber);
            return pstmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber) {
        List<InventoryVO> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM TB_INVENTORY WHERE PK_INVENTORY_SEQ = ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selectedNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    InventoryVO inventory = new InventoryVO();
                    inventory.setInventorySeq(rs.getInt("PK_INVENTORY_SEQ"));
                    inventory.setInventorySlipDate(rs.getString("DT_INVENTORY_SLIP_DATE"));
                    inventory.setInventoryCnt(rs.getInt("N_INVENTORY_CNT"));
                    inventory.setZoneCd(rs.getString("V_ZONE_CD"));
                    inventory.setWarehouseCd(rs.getString("V_WAREHOUSE_CD"));
                    inventory.setProductCd(rs.getString("V_PRODUCT_CD"));
                    inventoryList.add(inventory);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inventoryList;
    }

    @Override
    public String updateRestoreInventoryQuantity(Long purchaseSeq) {
        Connection conn;
        CallableStatement cstmt = null;
        String result = "fail";

        try {
            conn = DbConnection.getInstance().getConnection();

            String procedure = "{call RESTORE_INVENTORY(?, ?)}";

            cstmt = conn.prepareCall(procedure);
            cstmt.setLong(1, purchaseSeq);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            cstmt.execute();

            result = cstmt.getString(2);

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

    @Override
    public List<InventoryVO> updateInventoryForRestoration(Long purchaseSeq) {
        List<InventoryVO> inventoryList = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        int updatedRows = 0;

        try {
            conn = DbConnection.getInstance().getConnection();

            String sql = new StringBuilder("SELECT i.V_PRODUCT_CD, ")
                    .append("SUM(CASE WHEN h.V_INVENTORY_CHANEGE_TYPE = 'CHANGE_CNT_OUTBOUND' THEN -h.N_INVENTORY_SHIPPING_CNT ")
                    .append("WHEN h.V_INVENTORY_CHANEGE_TYPE = 'CHANGE_CNT_INBOUND' THEN h.N_INVENTORY_SHIPPING_CNT ")
                    .append("ELSE 0 END ) AS net_inventory_change ")
                    .append("FROM TB_INVENTORY_HISTORY h JOIN TB_INVENTORY i ON h.PK_INVENTORY_SEQ = i.PK_INVENTORY_SEQ ")
                    .append("JOIN TB_SHOP_PURCHASE_DETAIL d ON i.V_PRODUCT_CD = d.V_PRODUCT_CD ")
                    .append("WHERE d.PK_SHOP_PURCHASE_SEQ = ? AND h.V_INVENTORY_CHANEGE_TYPE IN ('CHANGE_CNT_OUTBOUND', 'CHANGE_CNT_INBOUND')")
                    .append("GROUP BY i.V_PRODUCT_CD").toString();

            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, purchaseSeq);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                InventoryVO inventory = new InventoryVO();
                inventory.setProductCd(rs.getString("V_PRODUCT_CD"));
                inventory.setInventoryCnt(rs.getInt("net_inventory_change"));
                inventoryList.add(inventory);
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

        return inventoryList;
    }
}
