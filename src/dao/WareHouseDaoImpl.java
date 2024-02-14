package dao;

import java.sql.SQLException;
import util.DbConnection;
import vo.WareHouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WareHouseDaoImpl implements WareHouseDao {
    private static WareHouseDaoImpl instance;

    private WareHouseDaoImpl() {}

    public static synchronized WareHouseDaoImpl getInstance() {
        if (instance == null) {
            instance = new WareHouseDaoImpl();
        }
        return instance;
    }

    @Override
    public void registerWareHouse(WareHouse wareHouse) {
        String sql = "INSERT INTO TB_WAREHOUSE (V_WAREHOUSE_CD, V_WAREHOUSE_NM, V_WAREHOUSE_LOC, S_WAREHOUSE_TYPE, PK_MEMBER_SEQ) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, wareHouse.getWarehouseCode());
            pstmt.setString(2, wareHouse.getWarehouseName());
            pstmt.setString(3, wareHouse.getWarehouseLocation());
            pstmt.setString(4, wareHouse.getWarehouseType());
            pstmt.setInt(5, wareHouse.getMemberSeq());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<WareHouse> viewWareHouse() {
        List<WareHouse> wareHouses = new ArrayList<>();
        String sql = "SELECT * FROM TB_WAREHOUSE";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                wareHouses.add(extractWareHouseFromResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wareHouses;
    }

    @Override
    public List<WareHouse> viewWareHouseByName(String name) {
        List<WareHouse> wareHouses = new ArrayList<>();
        String sql = "SELECT * FROM TB_WAREHOUSE WHERE V_WAREHOUSE_NM LIKE ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    wareHouses.add(extractWareHouseFromResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wareHouses;
    }

    @Override
    public List<WareHouse> viewWareHouseByLocation(String location) {
        List<WareHouse> wareHouses = new ArrayList<>();
        String sql = "SELECT * FROM TB_WAREHOUSE WHERE V_WAREHOUSE_LOC LIKE ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + location + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    wareHouses.add(extractWareHouseFromResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wareHouses;
    }

    @Override
    public List<WareHouse> viewWareHouseByType(String type) {
        List<WareHouse> wareHouses = new ArrayList<>();
        String sql = "SELECT * FROM TB_WAREHOUSE WHERE S_WAREHOUSE_TYPE LIKE ?";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + type + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    wareHouses.add(extractWareHouseFromResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wareHouses;
    }

    @Override
    public List<WareHouse> viewWareHouseByNameMain() {
        List<WareHouse> wareHouseListByName = new ArrayList<>();
        String sql = "SELECT DISTINCT V_WAREHOUSE_NM FROM TB_WAREHOUSE";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                WareHouse wareHouse = new WareHouse();
                wareHouse.setWarehouseName(rs.getString("V_WAREHOUSE_NM"));
                wareHouseListByName.add(wareHouse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wareHouseListByName;
    }

    @Override
    public List<WareHouse> viewWareHouseByLocationMain() {
        List<WareHouse> wareHouseListByLocation = new ArrayList<>();
        String sql = "SELECT DISTINCT V_WAREHOUSE_LOC FROM TB_WAREHOUSE";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                WareHouse wareHouse = new WareHouse();
                wareHouse.setWarehouseLocation(rs.getString("V_WAREHOUSE_LOC"));
                wareHouseListByLocation.add(wareHouse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wareHouseListByLocation;
    }

    @Override
    public List<WareHouse> viewWareHouseByTypeMain() {
        List<WareHouse> wareHouseListByType = new ArrayList<>();
        String sql = "SELECT DISTINCT S_WAREHOUSE_TYPE FROM TB_WAREHOUSE";
        try (Connection conn = DbConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                WareHouse wareHouse = new WareHouse();
                wareHouse.setWarehouseType(rs.getString("S_WAREHOUSE_TYPE"));
                wareHouseListByType.add(wareHouse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wareHouseListByType;
    }

    public WareHouse extractWareHouseFromResultSet(ResultSet rs) throws SQLException {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setWarehouseCode(rs.getString("V_WAREHOUSE_CD"));
        wareHouse.setWarehouseName(rs.getString("V_WAREHOUSE_NM"));
        wareHouse.setWarehouseLocation(rs.getString("V_WAREHOUSE_LOC"));
        wareHouse.setWarehouseType(rs.getString("S_WAREHOUSE_TYPE"));
        wareHouse.setMemberSeq(rs.getInt("PK_MEMBER_SEQ"));
        return wareHouse;
    }
}
