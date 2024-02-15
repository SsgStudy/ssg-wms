package service;

import vo.InventoryVO;

import java.util.List;

/**
 * The interface Inventory movement service.
 */
public interface InventoryMovementService {

    /**
     * Gets warehouse code.
     *
     * @return the warehouse code
     */
    List<String> getWarehouseCode();

    /**
     * Gets inventory information.
     *
     * @return the inventory information
     */
    List<InventoryVO> getInventoryInformation();

    /**
     * Gets zone code.
     *
     * @param selectedWarehouseCode the selected warehouse code
     * @return the zone code
     */
    List<String> getZoneCode(String selectedWarehouseCode);

    /**
     * Update inventory movement int.
     *
     * @param selectedInventory the selected inventory
     * @return the int
     */
    int updateInventoryMovement(InventoryVO selectedInventory);

    /**
     * Gets updated inventory.
     *
     * @param selectedNumber the selected number
     * @return the updated inventory
     */
    List<InventoryVO> getUpdatedInventory(int selectedNumber);
}
