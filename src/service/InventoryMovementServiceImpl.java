package service;

import dao.InventoryMovementDAOImpl;
import vo.InventoryVO;

import java.util.*;

public class InventoryMovementServiceImpl implements InventoryMovementService {

    private InventoryMovementDAOImpl warehouseInventoryMovementDao = new InventoryMovementDAOImpl();

    @Override
    public List<InventoryVO> getInventoryInformation(){
        return warehouseInventoryMovementDao.getInventoryInformation();
    }

    @Override
    public int updateInventoryMovement(InventoryVO selectedInventory){
        return warehouseInventoryMovementDao.updateInventoryForMovement(selectedInventory);
    }

    @Override
    public List<String> getWarehouseCode(){
        return warehouseInventoryMovementDao.getWarehouseCode();
    }

    @Override
    public List<String> getZoneCode(String selectedWarehouseCode){
        return warehouseInventoryMovementDao.getZoneCode(selectedWarehouseCode);
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber){
        return warehouseInventoryMovementDao.getUpdatedInventory(selectedNumber);
    }
}
