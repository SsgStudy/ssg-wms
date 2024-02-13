package dao;

import util.DbConnection;
import vo.InventoryVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InventoryMovementDAOImpl implements InventoryMovementDAO {
    private static InventoryMovementDAOImpl instance;

    private InventoryMovementDAOImpl() {
    }

    public static synchronized InventoryMovementDAOImpl getInstance() {
        if (instance == null) {
            instance = new InventoryMovementDAOImpl();
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
    public List<String> getWarehouseCode() {
        List<String> warehouseCodeList = new ArrayList<>();
        String sql = "SELECT V_WAREHOUSE_CD FROM TB_WAREHOUSE";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                warehouseCodeList.add(rs.getString("V_WAREHOUSE_CD"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return warehouseCodeList;
    }

    @Override
    public List<String> getZoneCode(String wareHouseCode) {
        List<String> zoneCodeList = new ArrayList<>();
        String sql = "SELECT V_ZONE_CD FROM TB_ZONE WHERE V_WAREHOUSE_CD = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, wareHouseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    zoneCodeList.add(rs.getString("V_ZONE_CD"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return zoneCodeList;
    }

    @Override
    public int updateInventoryForMovement(InventoryVO inventory) {
        String sql = "UPDATE TB_INVENTORY SET V_ZONE_CD = ?, V_WAREHOUSE_CD = ? WHERE PK_INVENTORY_SEQ = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inventory.getZoneCd());
            pstmt.setString(2, inventory.getWarehouseCd());
            pstmt.setInt(3, inventory.getInventorySeq());
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
