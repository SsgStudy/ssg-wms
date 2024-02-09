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
        // 헤더 출력
        System.out.printf("%-10s%-12s%-25s%-20s%-20s%-16s%-8s%-22s%s\n",
                "발주 번호", "발주 상태", "발주 상품 공급업체명", "발주 상품 배송예정일",
                "발주 완료일", "발주 상세 번호", "발주 수량", "발주 상품 코드", "창고 코드");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------");

        // 가져온 주문 정보 출력
        for (OrderVO order : orders) {
            System.out.printf("%-10d%-12s%-25s%-20s%-20s%-16d%-8d%-22s%s\n",
                    order.getOrderSeq(),
                    order.getOrderStatus(),
                    order.getIncomingProductSupplierName(),
                    order.getDeliveryDate(),
                    order.getOrderCompletionDate() == null ? "            미완료                    " : order.getOrderCompletionDate(),
                    order.getOrderDetailSeq(),
                    order.getOrderCnt(),
                    order.getProductCode(),
                    order.getWarehouseCode());
        }
    }

}
