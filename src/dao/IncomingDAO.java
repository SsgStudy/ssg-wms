package dao;

import java.util.List;
import vo.IncomingVO;
import vo.OrderVO;

public interface IncomingDAO {
    List<IncomingVO> getAllIncomingProductsWithDetails();

}
