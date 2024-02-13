package service;

import vo.InventoryVO;

import java.util.List;

public interface InventoryAdjustmentService {
    List<InventoryVO>  getInventoryInformation();
    int updateIncreaseInventoryQuantity(int selectedNumber, int adjustedQuantity);
    int updateDecreaseInventoryQuantity(int selectedNumber, int adjustedQuantity);
    List<InventoryVO> getUpdatedInventory(int selectedNumber);
    int updateRestoreInventoryQuantity(Long purchaseSeq);

}
