package service;

import dao.InventoryAdjustmentDAOImpl;
import vo.InventoryVO;


import java.util.*;

public class InventoryAdjustmentServiceImpl implements InventoryAdjustmentService{

    private InventoryAdjustmentDAOImpl inventoryAdjustmentDao = new InventoryAdjustmentDAOImpl();

    @Override
    public List<InventoryVO> getInventoryInformation(){
        return inventoryAdjustmentDao.getInventoryInformation();
    }

    @Override
    public int updateIncreaseInventoryQuantity(int selectedNumber, int adjustedQuantity){
        return inventoryAdjustmentDao.updateIncreaseInventoryQuantity(selectedNumber, adjustedQuantity);
    }

    @Override
    public int updateDecreaseInventoryQuantity(int selectedNumber, int adjustedQuantity){
        return inventoryAdjustmentDao.updateDecreaseInventoryQuantity(selectedNumber, adjustedQuantity);
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber) {
        return inventoryAdjustmentDao.getUpdatedInventory(selectedNumber);
    }
}
