package service;

import dao.OutgoingDAOImpl;

import java.time.LocalDateTime;
import java.util.List;

import vo.InventoryVO;
import vo.OutgoingInstVO;
import vo.OutgoingProductVO;
import vo.OutgoingVO;

/**
 * The type Outgoing service.
 */
public class OutgoingServiceImpl implements OutgoingService {
    private OutgoingDAOImpl outgoingDAO;

    /**
     * Instantiates a new Outgoing service.
     *
     * @param outgoingDAO the outgoing dao
     */
    public OutgoingServiceImpl(OutgoingDAOImpl outgoingDAO) {
        this.outgoingDAO = outgoingDAO;
    }

    @Override
    public void addOutgoingProduct(int shopSeq) throws Exception {
        outgoingDAO.processOutgoingProducts(shopSeq);
    }

    @Override
    public List<OutgoingInstVO> getAllOutgoingInsts() {
        return outgoingDAO.getAllOutgoingInsts();
    }

    @Override
    public void updateOutgoingProductStatusAndDate(Long pkOutgoingId, LocalDateTime date, String status) throws Exception {
        outgoingDAO.updateOutgoingProductStatusAndDate(pkOutgoingId, date, status);
    }

    @Override
    public int getOutgoingProductQuantity(Long pkOutgoingId) throws Exception {
        return outgoingDAO.getOutgoingProductQuantity(pkOutgoingId);
    }

    @Override
    public List<InventoryVO> getInventoryByProductCodeAndQuantity(String productCd, int quantity) throws Exception {
        return outgoingDAO.getInventoryByProductCodeAndQuantity(productCd, quantity);
    }

    @Override
    public void updateOutgoingProduct(Long pkOutgoingId, int newQuantity, String warehouseCd, String zoneCd) throws Exception {
        outgoingDAO.updateOutgoingProduct(pkOutgoingId, newQuantity, warehouseCd, zoneCd);
    }

    @Override
    public String getProductCodeByOutgoingId(Long pkOutgoingId) throws Exception {
        return outgoingDAO.getProductCodeByOutgoingId(pkOutgoingId);
    }

    @Override
    public List<OutgoingVO> getAllOutgoings() throws Exception {
        return outgoingDAO.getAllOutgoings();
    }

    @Override
    public OutgoingProductVO getOutgoingProductRowByOutgoingId(Long pkOutgoingId) throws Exception {
        return outgoingDAO.getOutgoingRowByOutgoingId(pkOutgoingId);
    }
}
