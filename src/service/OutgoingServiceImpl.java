package service;

import dao.OutgoingDAO;
import dao.OutgoingDAOImpl;
import java.time.LocalDateTime;
import java.util.List;
import vo.InventoryVO;
import vo.OutgoingInstVO;
import vo.OutgoingVO;

public class OutgoingServiceImpl implements OutgoingService {

    private OutgoingDAO outgoingDAO;

    @Override
    public void addOutgoingProduct(int shopSeq) throws Exception {
        outgoingDAO.processOutgoingProducts(shopSeq);
    }

    public OutgoingServiceImpl(OutgoingDAOImpl outgoingDAO) {
        this.outgoingDAO = outgoingDAO;
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
}
