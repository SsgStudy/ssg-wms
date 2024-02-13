package service;

import dao.InventoryMovementDAOImpl;
import vo.InventoryVO;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryMovementServiceImpl implements InventoryMovementService {

    private InventoryMovementDAOImpl warehouseInventoryMovementService = new InventoryMovementDAOImpl();

    @Override
    public List<InventoryVO> getInventoryInformation(){
        return warehouseInventoryMovementService.getInventoryInformation();
    }

    @Override
    public int updateInventoryMovement(InventoryVO selectedInventory){
        return warehouseInventoryMovementService.updateInventoryForMovement(selectedInventory);
    }

    @Override
    public List<String> getWarehouseCode(){
        return warehouseInventoryMovementService.getWarehouseCode();
    }

    @Override
    public List<String> getZoneCode(String selectedWarehouseCode){
        return warehouseInventoryMovementService.getZoneCode(selectedWarehouseCode);
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber){
        return warehouseInventoryMovementService.getUpdatedInventory(selectedNumber);
    }
}
