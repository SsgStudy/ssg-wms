package service;

import java.io.IOException;

public interface WareHouseService {
    void registerWareHouse() throws IOException;
    void viewWareHouse() throws IOException;
    void updateWareHouse();
}
