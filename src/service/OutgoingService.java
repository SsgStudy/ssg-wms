package service;

import dao.OutgoingDAO;
import dao.OutgoingDAOImpl;
import java.util.List;
import vo.OutgoingInstVO;
import vo.OutgoingVO;

public interface OutgoingService {
    List<OutgoingInstVO> getAllOutgoingInsts();
    void addOutgoingProduct(int shopSeq) throws Exception;

}
