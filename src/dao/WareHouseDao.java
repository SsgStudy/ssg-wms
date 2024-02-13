package dao;

import vo.WareHouse;

import java.util.List;

public interface WareHouseDao {
    void registerWareHouse(WareHouse wareHouse);
    List<WareHouse> viewWareHouse();
    List<WareHouse> viewWareHouseByName(String name);
    List<WareHouse> viewWareHouseByNameMain();
    List<WareHouse> viewWareHouseByLocation(String location);
    List<WareHouse> viewWareHouseByLocationMain();
    List<WareHouse> viewWareHouseByType(String type);
    List<WareHouse> viewWareHouseByTypeMain();
}
