package service;

import dao.InventoryAdjustmentDAO;
import dao.InventoryAdjustmentDAOImpl;
import vo.InventoryVO;

import java.util.List;

public class InventoryAdjustmentServiceImpl implements InventoryAdjustmentService {
    private InventoryAdjustmentDAO inventoryAdjustmentDao;
    private PurchaseServiceImpl purchaseService;

    public InventoryAdjustmentServiceImpl() {
        this.inventoryAdjustmentDao = InventoryAdjustmentDAOImpl.getInstance();
    }

    @Override
    public List<InventoryVO> getInventoryInformation() {
        return inventoryAdjustmentDao.getInventoryInformation();
    }

    @Override
    public int updateIncreaseInventoryQuantity(int selectedNumber, int adjustedQuantity) {
        return inventoryAdjustmentDao.updateIncreaseInventoryQuantity(selectedNumber, adjustedQuantity);
    }

    @Override
    public int updateDecreaseInventoryQuantity(int selectedNumber, int adjustedQuantity) {
        return inventoryAdjustmentDao.updateDecreaseInventoryQuantity(selectedNumber, adjustedQuantity);
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber) {
        return inventoryAdjustmentDao.getUpdatedInventory(selectedNumber);
    }

    @Override
    public int updateRestoreInventoryQuantity(Long purchaseSeq) {
        String result = inventoryAdjustmentDao.updateRestoreInventoryQuantity(purchaseSeq);

        if (result.equals("success")) {
            return 1;
        }
        else return 0;
    }

    public int updateRestoration(Long purchaseSeq) {
        int quanity = -1;

        List<InventoryVO> inventoryList = inventoryAdjustmentDao.updateInventoryForRestoration(purchaseSeq);
        // 재고 복원 성공
        for (InventoryVO inventory : inventoryList) {
            if (inventory.getInventoryCnt() == 0) {
                quanity = 1;
            }
            else
                quanity = -1;
        }
        return quanity;
    }
}
