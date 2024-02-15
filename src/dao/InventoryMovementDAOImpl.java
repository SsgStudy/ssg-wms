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
    private static Connection conn;
    private static PreparedStatement pstmt;

    private InventoryMovementDAOImpl() {
        try {
            conn = DbConnection.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        try {
            Connection conn = DbConnection.getInstance().getConnection();

            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

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
            pstmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inventoryList;
    }

    @Override
    public List<String> getWarehouseCode() {
        List<String> warehouseCodeList = new ArrayList<>();
        String sql = "SELECT V_WAREHOUSE_CD FROM TB_WAREHOUSE";

        try {
            Connection conn = DbConnection.getInstance().getConnection();

            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                warehouseCodeList.add(rs.getString("V_WAREHOUSE_CD"));
            }
            pstmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return warehouseCodeList;
    }

    @Override
    public List<String> getZoneCode(String wareHouseCode) {
        List<String> zoneCodeList = new ArrayList<>();
        String sql = "SELECT V_ZONE_CD FROM TB_ZONE WHERE V_WAREHOUSE_CD = ?";

        try {
            Connection conn = DbConnection.getInstance().getConnection();

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, wareHouseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    zoneCodeList.add(rs.getString("V_ZONE_CD"));
                }
            }
            pstmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return zoneCodeList;
    }

    @Override
    public int updateInventoryForMovement(InventoryVO inventory) {
        int ack = 0;
        String sql = "UPDATE TB_INVENTORY SET V_ZONE_CD = ?, V_WAREHOUSE_CD = ? WHERE PK_INVENTORY_SEQ = ?";

        try {
            Connection conn = DbConnection.getInstance().getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, inventory.getZoneCd());
            pstmt.setString(2, inventory.getWarehouseCd());
            pstmt.setInt(3, inventory.getInventorySeq());

            ack = pstmt.executeUpdate();

            pstmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ack;
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber) {
        List<InventoryVO> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM TB_INVENTORY WHERE PK_INVENTORY_SEQ = ?";

        try {
            Connection conn = DbConnection.getInstance().getConnection();

            pstmt = conn.prepareStatement(sql);

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
            pstmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inventoryList;
    }
}
