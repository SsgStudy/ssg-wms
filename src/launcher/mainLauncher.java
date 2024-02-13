package launcher;

import controller.IncomingController;
import controller.MemberController;
import controller.MembrManagemntController;
import controller.OrderController;
import dao.IncomingDAOImpl;
import dao.LoginManagementDAOImpl;
import dao.OrderDAOImpl;
import java.util.Scanner;
import service.IncomingServiceImpl;
import service.LoginManagementServiceImpl;
import service.MemberServicelmpl;
import service.OrderServiceImpl;
import util.AsciiPrinter;

public class mainLauncher {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        AsciiPrinter asciiPrinter = new AsciiPrinter();

        // DAO 객체 생성
        IncomingDAOImpl incomingDAO = IncomingDAOImpl.getInstance();
        OrderDAOImpl orderDAO = OrderDAOImpl.getInstance();
        LoginManagementDAOImpl loginDAO = LoginManagementDAOImpl.getInstance();

        // 서비스 객체 생성 및 DAO 객체 주입
        IncomingServiceImpl incomingService = new IncomingServiceImpl(incomingDAO);
        OrderServiceImpl orderService = new OrderServiceImpl(orderDAO);
        LoginManagementServiceImpl loginService = new LoginManagementServiceImpl();
        MemberServicelmpl memberService = new MemberServicelmpl();

        // 컨트롤러 객체 생성 및 서비스 객체 주입
        IncomingController incomingController = new IncomingController(incomingService);
        MemberController memberController = new MemberController();
        MembrManagemntController membrManagemntController = MembrManagemntController.getInstance();

        asciiPrinter.printMainTitle();

        // 로그인 절차 수행
        System.out.println("로그인이 필요합니다.");
        System.out.print("아이디: ");
        String id = scanner.nextLine();
        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        memberController.logIn(id, password);
        if (loginDAO.getMemberId() != null) { // 로그인 성공 확인
            boolean menuContinue = true;

            while (menuContinue) {
                System.out.println("SSG WMS SYSTEM MAIN");
                System.out.println("1. 회원 관리");
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
                    case 5 -> {
                        OrderController orderController = new OrderController(orderService);
                        orderController.printAllOrdersWithDetails();

                    }
                    case 10 -> menuContinue =false;
                    default -> System.out.println("옳지 않은 입력입니다.");
                }
            }
        } else {
            System.out.println("로그인에 실패했습니다. 프로그램을 종료합니다.");
        }

        scanner.close();
    }

}

