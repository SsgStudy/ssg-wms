package service;

import dao.WareHouseDaoImpl;
import vo.WareHouse;
import vo.WareHouseZone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The type Ware house service.
 */
public class WareHouseServiceImpl implements WareHouseService {
    private WareHouseDaoImpl wareHouseDao = WareHouseDaoImpl.getInstance();
    private static Logger logger = Logger.getLogger(WareHouseServiceImpl.class.getName());

    public void registerWareHouse(WareHouse wareHouse) {
        wareHouseDao.registerWareHouse(wareHouse);
    }

    public List<WareHouse> viewWareHouse() {
        return wareHouseDao.viewWareHouse();
    }

    public List<WareHouse> viewWareHouseByName(String name) {
        return wareHouseDao.viewWareHouseByName(name);
    }

    public List<WareHouse> viewWareHouseByLocation(String location) {
        return wareHouseDao.viewWareHouseByLocation(location);
    }

    public List<WareHouse> viewWareHouseByType(String type) {
        return wareHouseDao.viewWareHouseByType(type);
    }

    @Override
    public List<WareHouse> viewWareHouseByMemberId(String memberId) {
        return wareHouseDao.viewWarehouseByMemberId(memberId);
    }

    @Override
    public List<WareHouseZone> viewWareHouseZoneByWarehouseCd(String warehouseCd) {
        return wareHouseDao.viewWarehouseZoneCdByWarehouseCd(warehouseCd);
    }

    /**
     * View warehouse by name main list.
     *
     * @return the list
     */
    public List<WareHouse> viewWareHouseByNameMain() {
        return wareHouseDao.viewWareHouseByNameMain();
    }

    /**
     * View warehouse by location main list.
     *
     * @return the list
     */
    public List<WareHouse> viewWareHouseByLocationMain() {
        return wareHouseDao.viewWareHouseByLocationMain();
    }

    /**
     * View warehouse by type main list.
     *
     * @return the list
     */
    public List<WareHouse> viewWareHouseByTypeMain() {
        return wareHouseDao.viewWareHouseByTypeMain();
    }

    /**
     * Extract warehouse from result set warehouse.
     *
     * @param rs the rs
     * @return the warehouse
     * @throws SQLException the sql exception
     */
    public WareHouse extractWareHouseFromResultSet(ResultSet rs) throws SQLException {
        return wareHouseDao.extractWareHouseFromResultSet(rs);
    }
}