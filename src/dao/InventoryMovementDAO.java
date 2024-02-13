package dao;

import vo.InventoryVO;

import java.util.List;

public interface InventoryMovementDAO {
    List<InventoryVO> getInventoryInformation();
    List<String> getWarehouseCode();
    List<String> getZoneCode(String wareHouseCode);
    int updateInventoryForMovement(InventoryVO inventory);
    List<InventoryVO> getUpdatedInventory(int selectedNumber);

}
