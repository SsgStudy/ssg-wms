package dao;

import util.DbConnection;
import vo.InventoryVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryAdjustmentDAOImpl implements InventoryAdjustmentDAO{
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

    //inventory리스트 가져오기
    @Override
    public List<InventoryVO> getInventory() {
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
    public int updateIncreaseInventoryQuantity(int selectedNumber, int adjustedQuantity) {
        int ack = 0;

        String sql = new StringBuilder()
                .append("UPDATE TB_INVENTORY ")
                .append("SET N_INVENTORY_CNT = N_INVENTORY_CNT + ? ")
                .append("WHERE PK_INVENTORY_SEQ = ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, adjustedQuantity);
            pstmt.setInt(2, selectedNumber);
            ack = pstmt.executeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ack;
    }

    @Override
    public int updateDecreaseInventoryQuantity(int selectedNumber, int adjustedQuantity) {
        int ack = 0;

        String sql = new StringBuilder()
                .append("UPDATE TB_INVENTORY ")
                .append("SET N_INVENTORY_CNT = N_INVENTORY_CNT - ? ")
                .append("WHERE PK_INVENTORY_SEQ = ?")
                .toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, adjustedQuantity);
            pstmt.setInt(2, selectedNumber);
            ack = pstmt.executeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ack;
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber) {
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
