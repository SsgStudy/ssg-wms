package dao;

import java.sql.SQLException;
import java.util.List;
import vo.IncomingVO;
import vo.OrderVO;

public interface IncomingDAO {
    List<IncomingVO> getAllIncomingProductsWithDetails();
    void updateIncomingProduct(long seq, String zoneCode, int count, int price) throws SQLException;
    void approveIncomingProduct(long seq) throws Exception;



    }
