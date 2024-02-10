package service;

import dao.OutgoingDAO;
import dao.OutgoingDAOImpl;
import java.util.List;
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

}
