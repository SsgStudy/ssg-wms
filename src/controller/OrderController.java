package controller;

import dao.OrderDAO;
import dao.OrderDAOImpl;
import java.util.List;
import service.OrderService;
import service.OrderServiceImpl;
import vo.OrderVO;

public class OrderController {
    private OrderService orderService;

    // Dependency Injection을 통해 OrderServiceImpl 인스턴스를 주입받음
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<OrderVO> getAllOrdersWithDetails() {
        return orderService.getAllOrdersWithDetails();
    }
}
