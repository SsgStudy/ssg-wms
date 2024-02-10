package test;

import controller.IncomingController;
import controller.OrderController;
import dao.IncomingDAOImpl;
import dao.OrderDAO;
import dao.OrderDAOImpl;
import java.util.Scanner;
import service.IncomingServiceImpl;
import service.OrderService;
import service.OrderServiceImpl;

public class OrderMain {
    public static void main(String[] args) {

        // DAO 객체 생성
        IncomingDAOImpl incomingDAO = IncomingDAOImpl.getInstance();
        OrderDAOImpl orderDAO = OrderDAOImpl.getInstance();

        // 서비스 객체 생성 및 DAO 객체 주입
        IncomingServiceImpl incomingService = new IncomingServiceImpl(incomingDAO);
        OrderServiceImpl orderService = new OrderServiceImpl(orderDAO);


        // 컨트롤러 객체 생성 및 서비스 객체 주입
        IncomingController incomingController = new IncomingController(incomingService);
        OrderController orderController = new OrderController(orderService);


        // 컨트롤러 메소드 테스트
//        incomingController.printAllIncomingProductsWithDetails();
//        orderController.printAllOrdersWithDetails();

        Scanner scanner = new Scanner(System.in);
        System.out.println("SSG WMS SYSTEM MAIN");
        System.out.println("1. 입고 관리");
        System.out.println("2. 발주 목록 출력");
        System.out.print("선택: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> incomingController.printAllIncomingProductsWithDetails();
            case 2 -> orderController.printAllOrdersWithDetails();
            default -> System.out.println("옳지 않은 입력입니다.");
        }

        scanner.close();

    }
}
