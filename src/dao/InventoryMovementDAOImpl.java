package dao;

import util.DbConnection;
import vo.InventoryVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryMovementDAOImpl implements InventoryMovementDAO {
    Connection conn;

    {
        try {
            conn = DbConnection.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private PreparedStatement pstmt;
    private List<InventoryVO> inventoryList = new ArrayList<>();

    private List<String> warehouseCodeList = new ArrayList<>();
    private List<String> zoneCodeList = new ArrayList<>();

    @Override
    public List<InventoryVO> getInventoryInformation() {
        String sql = new StringBuilder()
                .append("SELECT * FROM TB_INVENTORY")
                .toString();

        try {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return inventoryList;
    }

    @Override
    public List<String> getWarehouseCode() {
        String sql = new StringBuilder().append("SELECT V_WAREHOUSE_CD FROM TB_WAREHOUSE").toString();

        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                warehouseCodeList.add(rs.getString("V_WAREHOUSE_CD"));
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return warehouseCodeList;
    }

    @Override
    public List<String> getZoneCode(String wareHouseCode) {
        String sql = new StringBuilder()
                .append("SELECT V_ZONE_CD FROM TB_ZONE ")
                .append("WHERE V_WAREHOUSE_CD = ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, wareHouseCode);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                zoneCodeList.add(rs.getString("V_ZONE_CD"));
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return zoneCodeList;
    }

    @Override
    public int updateInventoryForMovement(InventoryVO inventory){
        int ack = 0;

        String sql = new StringBuilder()
                .append("UPDATE TB_INVENTORY ")
                .append("SET V_ZONE_CD = ?, ")
                .append("V_WAREHOUSE_CD = ? ")
                .append("WHERE PK_INVENTORY_SEQ = ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, inventory.getZoneCd());
            pstmt.setString(2, inventory.getWarehouseCd());
            pstmt.setInt(3, inventory.getInventorySeq());
            ack = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ack;
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber) {
        inventoryList.clear();
        InventoryVO inventory = new InventoryVO();
        inventoryList.clear();
        String sql = new StringBuilder()
                .append("SELECT * FROM TB_INVENTORY ")
                .append("WHERE PK_INVENTORY_SEQ = ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, selectedNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                inventory.setInventorySeq(rs.getInt("PK_INVENTORY_SEQ"));
                inventory.setInventorySlipDate(rs.getString("DT_INVENTORY_SLIP_DATE"));
                inventory.setInventoryCnt(rs.getInt("N_INVENTORY_CNT"));
                inventory.setZoneCd(rs.getString("V_ZONE_CD"));
                inventory.setWarehouseCd(rs.getString("V_WAREHOUSE_CD"));
                inventory.setProductCd(rs.getString("V_PRODUCT_CD"));
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return inventoryList;
    }
}
