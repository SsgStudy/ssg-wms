package dao;

import java.util.List;
import vo.IncomingVO;
import vo.OutgoingInstVO;
import vo.OutgoingVO;

public interface OutgoingDAO {
    List<OutgoingInstVO> getAllOutgoingInsts();
    void insertOutgoingProduct(OutgoingVO outgoingVO) throws Exception;
    void processOutgoingProducts(int shopSeq) throws Exception;


}
