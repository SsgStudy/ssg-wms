package dao;

import vo.InventoryVO;

import java.util.List;

public interface InventoryAdjustmentDAO {
    List<InventoryVO> getInventoryInformation();
    int updateIncreaseInventoryQuantity(int selectedNumber, int adjustedQuantity);
    int updateDecreaseInventoryQuantity(int selectedNumber, int adjustedQuantity);
    List<InventoryVO> getUpdatedInventory(int selectedNumber);
    String updateRestoreInventoryQuantity(Long purchaseSeq);
    List<InventoryVO> updateInventoryForRestoration(Long purchaseSeq);
}
