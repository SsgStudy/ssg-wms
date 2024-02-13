package dao;

import dao.WareHouseDao;
import util.DbConnection;
import vo.WareHouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WareHouseDaoImpl implements WareHouseDao {

    Connection conn = null;

    @Override
    public void registerWareHouse(WareHouse wareHouse) {
        try {
            System.out.println("dao 전달받은 값 : " + wareHouse.getMemberSeq());

            conn = DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO `TB_WAREHOUSE` (`V_WAREHOUSE_CD`, `V_WAREHOUSE_NM`, `V_WAREHOUSE_LOC`, `S_WAREHOUSE_TYPE`, `PK_MEMBER_SEQ`) VALUES\n" +
                    "(?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, wareHouse.getWarehouseCode());
            pstmt.setString(2, wareHouse.getWarehouseName());
            pstmt.setString(3, wareHouse.getWarehouseLocation());
            pstmt.setString(4, wareHouse.getWarehouseType());
            pstmt.setInt(5, wareHouse.getMemberSeq());
            System.out.println(pstmt.toString());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<WareHouse> viewWareHouse() {
        List<WareHouse> wareHouses = new ArrayList<>();
        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder().append("SELECT * FROM TB_WAREHOUSE").toString();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                WareHouse wareHouse = new WareHouse();
                wareHouse.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                wareHouse.setWarehouseName(rs.getString("V_WAREHOUSE_NM"));
                wareHouse.setWarehouseLocation(rs.getString("V_WAREHOUSE_LOC"));
                wareHouse.setWarehouseType(rs.getString("S_WAREHOUSE_TYPE"));
                wareHouse.setMemberSeq(rs.getInt("PK_MEMBER_SEQ"));
                wareHouses.add(wareHouse);
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return wareHouses;
    }

    @Override
    public List<WareHouse> viewWareHouseByName(String name) {
        List<WareHouse> wareHouses = new ArrayList<>();
        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder().append("SELECT * FROM TB_WAREHOUSE WHERE V_WAREHOUSE_NM LIKE ?").toString();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                WareHouse wareHouse = new WareHouse();
                wareHouse.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                wareHouse.setWarehouseName(rs.getString("V_WAREHOUSE_NM"));
                wareHouse.setWarehouseLocation(rs.getString("V_WAREHOUSE_LOC"));
                wareHouse.setWarehouseType(rs.getString("S_WAREHOUSE_TYPE"));
                wareHouse.setMemberSeq(rs.getInt("PK_MEMBER_SEQ"));
                wareHouses.add(wareHouse);
            }
            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    DbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return wareHouses;
    }

    @Override
    public List<WareHouse> viewWareHouseByLocation(String location) {
        List<WareHouse> wareHouses = new ArrayList<>();
        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder().append("SELECT * FROM TB_WAREHOUSE WHERE V_WAREHOUSE_LOC LIKE ?").toString();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + location + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                WareHouse wareHouse = new WareHouse();
                wareHouse.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                wareHouse.setWarehouseName(rs.getString("V_WAREHOUSE_NM"));
                wareHouse.setWarehouseLocation(rs.getString("V_WAREHOUSE_LOC"));
                wareHouse.setWarehouseType(rs.getString("S_WAREHOUSE_TYPE"));
                wareHouse.setMemberSeq(rs.getInt("PK_MEMBER_SEQ"));
                wareHouses.add(wareHouse);
            }
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    DbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return wareHouses;
    }

    @Override
    public List<WareHouse> viewWareHouseByType(String type) {
        List<WareHouse> wareHouses = new ArrayList<>();
        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder().append("SELECT * FROM TB_WAREHOUSE WHERE S_WAREHOUSE_TYPE LIKE ?").toString();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + type + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                WareHouse wareHouse = new WareHouse();
                wareHouse.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
                wareHouse.setWarehouseName(rs.getString("V_WAREHOUSE_NM"));
                wareHouse.setWarehouseLocation(rs.getString("V_WAREHOUSE_LOC"));
                wareHouse.setWarehouseType(rs.getString("S_WAREHOUSE_TYPE"));
                wareHouse.setMemberSeq(rs.getInt("PK_MEMBER_SEQ"));
                wareHouses.add(wareHouse);
            }
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    DbConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return wareHouses;
    }
}