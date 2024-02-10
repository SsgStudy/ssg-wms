package service;

import dao.IncomingDAOImpl;
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
}
