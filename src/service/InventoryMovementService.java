package service;

import vo.InventoryVO;

import java.util.List;

public interface InventoryMovementService {
    List<String> getWarehouseCode();

    List<InventoryVO> getInventoryInformation();

    List<String> getZoneCode(String selectedWarehouseCode);

    int updateInventoryMovement(InventoryVO selectedInventory);

    List<InventoryVO> getUpdatedInventory(int selectedNumber);
}
