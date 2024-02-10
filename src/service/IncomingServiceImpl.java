package service;

import dao.IncomingDAOImpl;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import vo.DetailedIncomingVO;
import vo.IncomingVO;

public class IncomingServiceImpl implements IncomigService {

    private IncomingDAOImpl incomingDAO;

    // Dependency Injection을 통해 IncomingProductDAOImpl 인스턴스를 주입받음
    public IncomingServiceImpl(IncomingDAOImpl incomingProductDAO) {
        this.incomingDAO = incomingProductDAO;
    }

    @Override
    public List<IncomingVO> getAllIncomingProductsWithDetails() {
        return incomingDAO.getAllIncomingProductsWithDetails();
    }

    @Override
    public void updateIncomingProductDetails(long seq, String zoneCode, int count, int price) throws SQLException {
        incomingDAO.updateIncomingProduct(seq, zoneCode, count, price);
    }

    @Override
    public void approveIncomingProduct(long seq) throws Exception {
        incomingDAO.approveIncomingProduct(seq);

    }
    @Override
    public List<IncomingVO> getIncomingProductsByMonth(int year, int month) throws SQLException {
        return incomingDAO.getIncomingProductsByMonth(year, month);
    }


    @Override
    public List<IncomingVO> getIncomingProductsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException{
        return incomingDAO.getIncomingProductsByDateRange(startDate,endDate);
    }

    @Override
    public DetailedIncomingVO getIncomingProductDetailsWithProductInfo(long seq) throws Exception {
        return incomingDAO.getIncomingProductDetailsWithProductInfo(seq);
    }
}
