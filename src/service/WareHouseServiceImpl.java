package service;

import dao.WareHouseDaoImpl;
import vo.WareHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

    public List<WareHouse> viewWareHouseByType(String type){
        return wareHouseDao.viewWareHouseByType(type);
    }

    public List<WareHouse> viewWareHouseByNameMain(){
        return wareHouseDao.viewWareHouseByNameMain();
    }

    public List<WareHouse> viewWareHouseByLocationMain(){
        return wareHouseDao.viewWareHouseByLocationMain();
    }

    public List<WareHouse> viewWareHouseByTypeMain(){
        return wareHouseDao.viewWareHouseByTypeMain();
    }

    public WareHouse extractWareHouseFromResultSet(ResultSet rs) throws SQLException {
        return wareHouseDao.extractWareHouseFromResultSet(rs);
    }










}
