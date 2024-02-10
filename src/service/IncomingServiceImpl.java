package service;

import dao.IncomingDAOImpl;
import java.sql.SQLException;
import java.util.List;
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
    public void updateIncomingProductDetails(long seq, String zoneCode, int count, int price) throws SQLException {
        incomingDAO.updateIncomingProduct(seq, zoneCode, count, price);
    }

    @Override
    public void approveIncomingProduct(long seq) throws Exception {
        incomingDAO.approveIncomingProduct(seq);

    }


}
