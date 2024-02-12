package test;

import controller.IncomingController;
import controller.MemberController;
import controller.OrderController;
import dao.IncomingDAOImpl;
import dao.LoginManagementDAOImpl;
import dao.OrderDAOImpl;
import java.util.Scanner;
import service.IncomingServiceImpl;
import service.LoginManagementServiceImpl;
import service.OrderServiceImpl;

public class OrderMain {
    public static void main(String[] args) throws Exception {
        // DAO 객체 생성
        IncomingDAOImpl incomingDAO = IncomingDAOImpl.getInstance();
        OrderDAOImpl orderDAO = OrderDAOImpl.getInstance();
        LoginManagementDAOImpl loginDAO = LoginManagementDAOImpl.getInstance(); // 로그인 관리 DAO

        // 서비스 객체 생성 및 DAO 객체 주입
        IncomingServiceImpl incomingService = new IncomingServiceImpl(incomingDAO);
        OrderServiceImpl orderService = new OrderServiceImpl(orderDAO);
        LoginManagementServiceImpl loginService = new LoginManagementServiceImpl();

        // 컨트롤러 객체 생성 및 서비스 객체 주입
        IncomingController incomingController = new IncomingController(incomingService);
        OrderController orderController = new OrderController(orderService);
        MemberController memberController = new MemberController();

        Scanner scanner = new Scanner(System.in);

        // 로그인 절차 수행
        System.out.println("로그인이 필요합니다.");
        System.out.print("아이디: ");
        String id = scanner.nextLine();
        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        memberController.logIn(id, password);
        if (loginDAO.getMemberId() != null) { // 로그인 성공 확인
            System.out.println("SSG WMS SYSTEM MAIN");
            System.out.println("1. 입고 관리");
            System.out.println("2. 발주 목록 출력");
            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> incomingController.incomingProductMenu();
                case 2 -> orderController.printAllOrdersWithDetails();
                default -> System.out.println("옳지 않은 입력입니다.");
            }
        } else {
            System.out.println("로그인에 실패했습니다. 프로그램을 종료합니다.");
        }

        scanner.close();
    }
}
