package dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import vo.DetailedIncomingVO;
import vo.IncomingVO;
import vo.OrderVO;

public interface IncomingDAO {
    List<IncomingVO> getAllIncomingProductsWithDetails();
    void updateIncomingProduct(long seq, String zoneCode, int count, int price) throws SQLException;
    void approveIncomingProduct(long seq) throws Exception;
    List<IncomingVO> getIncomingProductsByMonth(int year, int month) throws SQLException;

    List<IncomingVO> getIncomingProductsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;
    DetailedIncomingVO getIncomingProductDetailsWithProductInfo(long seq) throws Exception;


    }
