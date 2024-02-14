package service;

import vo.WareHouse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface WareHouseService {
    void registerWareHouse(WareHouse wareHouse) throws IOException;

    List<WareHouse> viewWareHouse() throws IOException;

    List<WareHouse> viewWareHouseByName(String name) throws SQLException;

    List<WareHouse> viewWareHouseByLocation(String location) throws SQLException;

    List<WareHouse> viewWareHouseByType(String type) throws SQLException;
}
