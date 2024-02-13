package dao;

import util.DbConnection;
import vo.InventoryVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
}
