package launcher;

import controller.*;
import dao.*;

import java.util.Scanner;

import service.*;
import util.AsciiPrinter;
import util.MenuBoxPrinter;

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
        PurchaseDAOImpl purchaseDAO = PurchaseDAOImpl.getInstance();


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
        PurchaseServiceImpl purchaseService = new PurchaseServiceImpl(purchaseDAO);


        // 컨트롤러 객체 생성 및 서비스 객체 주입
        WareHouseController wareHouseController = WareHouseController.getInstance(warehouseService);
        IncomingController incomingController = IncomingController.getInstance(incomingService);
        OutgoingController outgoingController = OutgoingController.getInstance(outgoingService, invoiceService, purchaseService);
        MemberController memberController = MemberController.getInstance();
        MembrManagemntController membrManagemntController = MembrManagemntController.getInstance();
        ProductManagementController productManagementController = ProductManagementController.getInstance(); // 수정: 싱글톤 인스턴스 사용
        InventoryController inventoryController = new InventoryController(adjustmentService, movementService, queryService);
        InvoiceController invoiceController = InvoiceController.getInstance(invoiceService);
        PurchaseController purchaseController = PurchaseController.getInstance(purchaseService, invoiceService, adjustmentService);
        OrderController orderController = OrderController.getInstance(orderService, warehouseService);

        asciiPrinter.printMainTitle();
        boolean isRunning = true;

        while (isRunning) {
            // 로그인 절차 수행
            System.out.println("LOG IN\n");
            System.out.print("➔ 아이디 : ");
            String id = scanner.nextLine().trim();
            System.out.print("➔ 비밀번호 : ");
            String password = scanner.nextLine().trim();

            memberController.logIn(id, password);
            if (loginDAO.getMemberId() != null) {
                boolean menuContinue = true;

                while (menuContinue) {
                    String fiftyNewLines = "x".repeat(50).replaceAll(".", "\n");
                    System.out.println(fiftyNewLines);
                    asciiPrinter.printMainTitle();
                    String[] menuItems = {
                            "1. 멤버 관리\t",
                            "2. 상품 관리\t",
                            "3. 주문 관리\t",
                            "4. 송장 관리\t",
                            "5. 발주 관리\t",
                            "6. 입고 관리\t",
                            "7. 출고 관리\t",
                            "8. 창고 관리\t",
                            "9. 재고 관리\t",
                            "10. 로그 아웃\t\t",
                            "11. WMS 종료"
                    };
                    MenuBoxPrinter.printMenuBoxWithTitle("SSG WMS SYSTEM MAIN", menuItems);

                    String input = scanner.nextLine();
                    int choice = -1;
                    try {
                        choice = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        System.out.println("숫자를 입력해주세요.");
                        continue;
                    }

                    switch (choice) {
                        //멤버 관리
                        case 1 -> membrManagemntController.menu();
                        //상품 관리
                        case 2 -> productManagementController.menu();
                        //주문 관리
                        case 3 -> purchaseController.menu();
                        //송장 관리
                        case 4 -> invoiceController.menu();
                        //발주 관리
                        case 5 -> orderController.menu();
                        //입고 관리
                        case 6 -> incomingController.incomingProductMenu();
                        //출고 관리
                        case 7 -> outgoingController.outgoingProductMenu();
                        //창고 관리
                        case 8 -> wareHouseController.menu();
                        //재고 관리
                        case 9 -> inventoryController.menu();
                        //로그아웃
                        case 10 -> {
                            memberController.logOut();
                            menuContinue = false;
                            scanner.nextLine();
                        }
                        //프로그램 종료
                        case 11 -> {
                            menuContinue = false;
                            isRunning = false;
                        }
                        default -> System.out.println("옳지 않은 입력입니다.");
                    }

                }
            } else {
                System.out.println("로그인에 실패했습니다. 프로그램을 종료합니다.");
            }
        }
        scanner.close();
    }

}

