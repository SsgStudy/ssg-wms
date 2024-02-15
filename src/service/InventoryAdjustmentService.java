package service;

import vo.InventoryVO;

import java.util.List;

/**
 * The interface Inventory adjustment service.
 */
public interface InventoryAdjustmentService {

    /**
     * Gets inventory information.
     *
     * @return the inventory information
     */
    List<InventoryVO> getInventoryInformation();

    /**
     * Update increase inventory quantity int.
     *
     * @param selectedNumber   the selected number
     * @param adjustedQuantity the adjusted quantity
     * @return the int
     */
    int updateIncreaseInventoryQuantity(int selectedNumber, int adjustedQuantity);

    /**
     * Update decrease inventory quantity int.
     *
     * @param selectedNumber   the selected number
     * @param adjustedQuantity the adjusted quantity
     * @return the int
     */
    int updateDecreaseInventoryQuantity(int selectedNumber, int adjustedQuantity);

    /**
     * Gets updated inventory.
     *
     * @param selectedNumber the selected number
     * @return the updated inventory
     */
    List<InventoryVO> getUpdatedInventory(int selectedNumber);

    /**
     * Update restore inventory quantity int.
     *
     * @param purchaseSeq the purchase seq
     * @return the int
     */
    int updateRestoreInventoryQuantity(Long purchaseSeq);

    /**
     * Update restoration int.
     *
     * @param purchaseSeq the purchase seq
     * @return the int
     */
    int updateRestoration(Long purchaseSeq);
}
