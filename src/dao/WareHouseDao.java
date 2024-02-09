package dao;

import vo.WareHouse;

import java.util.List;

public interface WareHouseDao {
    void registerWareHouse(WareHouse wareHouse);
    List<WareHouse> viewWareHouse();
    List<WareHouse> viewWareHouseByName();

    List<WareHouse> viewWareHouseByName(String name);
    List<WareHouse> viewWareHouseByLocation(String location);
    List<WareHouse> viewWareHouseByType(String type);
}
