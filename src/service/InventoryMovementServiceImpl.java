package service;

import dao.InventoryMovementDAO;
import dao.InventoryMovementDAOImpl;
import vo.InventoryVO;

import java.util.List;

/**
 * The type Inventory movement service.
 */
public class InventoryMovementServiceImpl implements InventoryMovementService {
    private InventoryMovementDAO inventoryMovementDao;

    /**
     * Instantiates a new Inventory movement service.
     */
    public InventoryMovementServiceImpl() {
        this.inventoryMovementDao = InventoryMovementDAOImpl.getInstance();
    }

    @Override
    public List<InventoryVO> getInventoryInformation() {
        return inventoryMovementDao.getInventoryInformation();
    }

    @Override
    public int updateInventoryMovement(InventoryVO selectedInventory) {
        return inventoryMovementDao.updateInventoryForMovement(selectedInventory);
    }

    @Override
    public List<String> getWarehouseCode() {
        return inventoryMovementDao.getWarehouseCode();
    }

    @Override
    public List<String> getZoneCode(String selectedWarehouseCode) {
        return inventoryMovementDao.getZoneCode(selectedWarehouseCode);
    }

    @Override
    public List<InventoryVO> getUpdatedInventory(int selectedNumber) {
        return inventoryMovementDao.getUpdatedInventory(selectedNumber);
    }
}
