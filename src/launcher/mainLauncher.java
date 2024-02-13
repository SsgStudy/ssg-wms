package launcher;

import controller.IncomingController;
import controller.InventoryController;
import controller.InvoiceController;
import controller.MemberController;
import controller.MembrManagemntController;
import controller.OrderController;
import controller.OutgoingController;
import controller.ProductManagementController;
import controller.WareHouseController;
import dao.IncomingDAOImpl;
import dao.InvoiceDaoImpl;
import dao.LoginManagementDAOImpl;
import dao.OrderDAOImpl;
import dao.OutgoingDAOImpl;
import dao.ProductManagementDaoImpl;
import dao.WareHouseDaoImpl;
import java.util.Scanner;
import service.IncomingServiceImpl;
import service.InventoryAdjustmentServiceImpl;
import service.InventoryMovementServiceImpl;
import service.InventoryQueryServiceImpl;
import service.InvoiceServiceImpl;
import service.LoginManagementServiceImpl;
import service.MemberServicelmpl;
import service.OrderServiceImpl;
import service.OutgoingServiceImpl;
import service.ProductServiceImpl;
import service.WareHouseServiceImpl;
import util.AsciiPrinter;

public class mainLauncher {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        AsciiPrinter asciiPrinter = new AsciiPrinter();

        // DAO 객체 생성
        WareHouseDaoImpl wareHouseDao = WareHouseDaoImpl.getInstance();
        IncomingDAOImpl incomingDAO = IncomingDAOImpl.getInstance();
        OutgoingDAOImpl outgoingDAO = OutgoingDAOImpl.getInstance();
        OrderDAOImpl orderDAO = OrderDAOImpl.getInstance();
        LoginManagementDAOImpl loginDAO = LoginManagementDAOImpl.getInstance();
        ProductManagementDaoImpl productDAO = ProductManagementDaoImpl.getInstance();
        InvoiceDaoImpl invoiceDao = InvoiceDaoImpl.getInstance();

        // 서비스 객체 생성 및 DAO 객체 주입
        WareHouseServiceImpl warehouseService = new WareHouseServiceImpl();
        IncomingServiceImpl incomingService = new IncomingServiceImpl(incomingDAO);
        OutgoingServiceImpl outgoingService = new OutgoingServiceImpl(outgoingDAO);
        OrderServiceImpl orderService = new OrderServiceImpl(orderDAO);
        LoginManagementServiceImpl loginService = new LoginManagementServiceImpl();
        MemberServicelmpl memberService = new MemberServicelmpl();
        ProductServiceImpl productService = ProductServiceImpl.getInstance();
        InventoryAdjustmentServiceImpl adjustmentService = new InventoryAdjustmentServiceImpl();
        InventoryMovementServiceImpl movementService = new InventoryMovementServiceImpl();
        InventoryQueryServiceImpl queryService = new InventoryQueryServiceImpl();
        InvoiceServiceImpl invoiceService = new InvoiceServiceImpl(invoiceDao);

        // 컨트롤러 객체 생성 및 서비스 객체 주입
        WareHouseController wareHouseController = WareHouseController.getInstance(warehouseService);
        IncomingController incomingController = IncomingController.getInstance(incomingService);
        OutgoingController outgoingController = OutgoingController.getInstance(outgoingService);
        MemberController memberController = MemberController.getInstance();
        MembrManagemntController membrManagemntController = MembrManagemntController.getInstance();
        ProductManagementController productManagementController = ProductManagementController.getInstance(); // 수정: 싱글톤 인스턴스 사용
        InventoryController inventoryController = new InventoryController(adjustmentService, movementService, queryService);
        InvoiceController invoiceController = InvoiceController.getInstance(invoiceService);

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
                System.out.println("10. 로그 아웃");
                System.out.println("11. 프로그램 종료");

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
                    case 4 -> invoiceController.menu();
                    //발주 관리
//                    case 5 -> productManagementController.menu();
                    //입고 관리
                    case 6 -> incomingController.incomingProductMenu();
                    //출고 관리
                    case 7 -> outgoingController.outgoingProductMenu();
                    //창고 관리
                    case 8 -> wareHouseController.menu();
                    //재고 관리
                    case 9 -> inventoryController.menu();
                    //로그아웃
                    case 10 -> memberController.logOut();
                    //프로그램 종료
                    case 11 -> menuContinue = false;
                    default -> System.out.println("옳지 않은 입력입니다.");
                }
            }
        } else {
            System.out.println("로그인에 실패했습니다. 프로그램을 종료합니다.");
        }

        scanner.close();
    }

}

