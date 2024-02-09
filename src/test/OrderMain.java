package test;

import controller.OrderController;
import dao.OrderDAO;
import dao.OrderDAOImpl;
import java.util.List;
import service.OrderService;
import service.OrderServiceImpl;
import vo.OrderVO;

public class OrderMain {
    public static void main(String[] args) {
        // OrderDAOImpl 인스턴스 생성
        OrderDAO orderDAO = OrderDAOImpl.getInstance();
        // OrderServiceImpl 인스턴스 생성, OrderDAOImpl 주입
        OrderService orderService = new OrderServiceImpl(orderDAO);
        // OrderController 인스턴스 생성, OrderServiceImpl 주입
        OrderController orderController = new OrderController(orderService);

        // 모든 주문 정보와 상세 정보 가져오기
        List<OrderVO> orders = orderController.getAllOrdersWithDetails();

        // 가져온 주문 정보 출력
        for (OrderVO order : orders) {
            System.out.println("주문 번호: " + order.getOrderSeq());
            System.out.println("주문 상태: " + order.getOrderStatus());
            System.out.println("입고 상품 공급업체명: " + order.getIncomingProductSupplierName());
            System.out.println("배송일: " + order.getDeliveryDate());
            System.out.println("주문 완료일: " + order.getOrderCompletionDate());
            System.out.println("------------------------------------");
        }
    }

}
