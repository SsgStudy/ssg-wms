package service;

import dao.OrderDAO;
import java.util.List;
import vo.OrderVO;

public class OrderServiceImpl implements OrderService {
    private OrderDAO orderDAO;

    // Dependency Injection을 통해 OrderDAOImpl 인스턴스를 주입받음
    public OrderServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public List<OrderVO> getAllOrdersWithDetails() {
        return orderDAO.getAllOrdersWithDetails();
    }
}