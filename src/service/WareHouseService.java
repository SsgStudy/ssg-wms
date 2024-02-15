package service;

import vo.WareHouse;
import vo.WareHouseZone;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * The interface Ware house service.
 */
public interface WareHouseService {

    /**
     * Register ware house.
     *
     * @param wareHouse the ware house
     * @throws IOException the io exception
     */
    void registerWareHouse(WareHouse wareHouse) throws IOException;

    /**
     * View warehouse list.
     *
     * @return the list
     * @throws IOException the io exception
     */
    List<WareHouse> viewWareHouse() throws IOException;

    /**
     * View warehouse by name list.
     *
     * @param name the name
     * @return the list
     * @throws SQLException the sql exception
     */
    List<WareHouse> viewWareHouseByName(String name) throws SQLException;

    /**
     * View warehouse by location list.
     *
     * @param location the location
     * @return the list
     * @throws SQLException the sql exception
     */
    List<WareHouse> viewWareHouseByLocation(String location) throws SQLException;

    /**
     * View warehouse by type list.
     *
     * @param type the type
     * @return the list
     * @throws SQLException the sql exception
     */
    List<WareHouse> viewWareHouseByType(String type) throws SQLException;

    /**
     * View warehouse by member id list.
     *
     * @param memberId the member id
     * @return the list
     */
    List<WareHouse> viewWareHouseByMemberId(String memberId);

    /**
     * View warehouse zone by warehouse cd list.
     *
     * @param warehouseCd the warehouse cd
     * @return the list
     */
    List<WareHouseZone> viewWareHouseZoneByWarehouseCd(String warehouseCd);
}
