package launcher;

import controller.IncomingController;
import controller.MemberController;
import controller.MembrManagemntController;
import controller.OrderController;
import controller.OutgoingController;
import controller.ProductManagementController;
import dao.IncomingDAOImpl;
import dao.LoginManagementDAOImpl;
import dao.OrderDAOImpl;
import dao.OutgoingDAOImpl;
import dao.ProductManagementDaoImpl;
import java.util.Scanner;
import service.IncomingServiceImpl;
import service.LoginManagementServiceImpl;
import service.MemberServicelmpl;
import service.OrderServiceImpl;
import service.OutgoingServiceImpl;
import service.ProductServiceImpl;
import util.AsciiPrinter;

public class mainLauncher {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        AsciiPrinter asciiPrinter = new AsciiPrinter();

        // DAO 객체 생성
        IncomingDAOImpl incomingDAO = IncomingDAOImpl.getInstance();
        OutgoingDAOImpl outgoingDAO = OutgoingDAOImpl.getInstance();
        OrderDAOImpl orderDAO = OrderDAOImpl.getInstance();
        LoginManagementDAOImpl loginDAO = LoginManagementDAOImpl.getInstance();
        ProductManagementDaoImpl productDAO = ProductManagementDaoImpl.getInstance();

        // 서비스 객체 생성 및 DAO 객체 주입
        IncomingServiceImpl incomingService = new IncomingServiceImpl(incomingDAO);
        OutgoingServiceImpl outgoingService = new OutgoingServiceImpl(outgoingDAO);
        OrderServiceImpl orderService = new OrderServiceImpl(orderDAO);
        LoginManagementServiceImpl loginService = new LoginManagementServiceImpl();
        MemberServicelmpl memberService = new MemberServicelmpl();
        ProductServiceImpl productService = ProductServiceImpl.getInstance();

        // 컨트롤러 객체 생성 및 서비스 객체 주입
        IncomingController incomingController = IncomingController.getInstance(incomingService);
        OutgoingController outgoingController = OutgoingController.getInstance(outgoingService);
        MemberController memberController = MemberController.getInstance();
        MembrManagemntController membrManagemntController = MembrManagemntController.getInstance();
        ProductManagementController productManagementController = ProductManagementController.getInstance(); // 수정: 싱글톤 인스턴스 사용

        asciiPrinter.printMainTitle();

        // 로그인 절차 수행
        System.out.println("로그인이 필요합니다.");
        System.out.print("아이디: ");
        String id = scanner.nextLine();
        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        memberController.logIn(id, password);
        if (loginDAO.getMemberId() != null) {
            boolean menuContinue = true;

            while (menuContinue) {
                System.out.println("SSG WMS SYSTEM MAIN");
                System.out.println("1. 멤버 관리");
                System.out.println("2. 상품 관리");
                System.out.println("3. 주문 관리");
                System.out.println("4. 송장 관리");
                System.out.println("5. 발주 관리");
                System.out.println("6. 입고 관리");
                System.out.println("7. 출고 관리");
                System.out.println("8. 창고 관리");
                System.out.println("9. 재고 관리");
                System.out.println("10. 프로그램 종료");

                System.out.print("선택: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    //멤버 관리
                    case 1 -> membrManagemntController.menu();
                    //상품 관리
                    case 2 -> productManagementController.menu();
                    //주문 관리
//                    case 3 -> productManagementController.menu();
                    //송장 관리
//                    case 4 -> productManagementController.menu();
                    //발주 관리
//                    case 5 -> productManagementController.menu();
                    //입고 관리
                    case 6 -> incomingController.incomingProductMenu();
                    //출고 관리
                    case 7 -> outgoingController.outgoingProductMenu();
                    //창고 관리
//                    case 8 -> productManagementController.menu();

                    //재고 관리
                    case 9 -> {
                        OrderController orderController = new OrderController(orderService);
                        orderController.printAllOrdersWithDetails();

                    }
                    case 10 -> menuContinue = false;
                    default -> System.out.println("옳지 않은 입력입니다.");
                }
            }
        } else {
            System.out.println("로그인에 실패했습니다. 프로그램을 종료합니다.");
        }

        scanner.close();
    }

}

