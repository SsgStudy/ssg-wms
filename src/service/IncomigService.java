package service;

import java.sql.SQLException;
import java.util.List;
import vo.IncomingVO;

public interface IncomigService {
    List<IncomingVO> getAllIncomingProductsWithDetails();
    void updateIncomingProductDetails(long seq, String zoneCode, int count, int price) throws SQLException;
    void approveIncomingProduct(long seq) throws Exception;

}
