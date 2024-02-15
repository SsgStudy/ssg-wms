package controller;

import dao.LoginManagementDAOImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import service.OrderService;
import service.PurchaseService;
import service.WareHouseService;
import util.MenuBoxPrinter;
import util.enumcollect.MemberEnum;
import util.enumcollect.OrderStatusEnum;
import vo.OrderVO;
import vo.Product;
import vo.WareHouse;
import vo.WareHouseZone;

public class OrderController {

    private static OrderController instance;
    private Scanner sc = new Scanner(System.in);

    private OrderService orderService;
    private WareHouseService wareHouseService;

    private LoginManagementDAOImpl loginDao;
    private MemberEnum loginMemberRole;
    private String loginMemberId;

    public OrderController(OrderService orderService, WareHouseService wareHouseService) {
        this.orderService = orderService;
        this.wareHouseService = wareHouseService;
        this.loginDao = LoginManagementDAOImpl.getInstance();
        updateLoginInfo(); // 로그인 정보 초기화
    }

    public static synchronized OrderController getInstance(OrderService orderService, WareHouseService wareHouseService) {
        if (instance == null) {
            instance = new OrderController(orderService, wareHouseService);
        }
        return instance;
    }

    public void updateLoginInfo() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
    }

    public void menu() {

        updateLoginInfo();

        boolean menuContinue = true;
        while (menuContinue) {
            String[] menuItems = {
                    "1. 발주 등록\t",
                    "2. 발주 확정\t",
                    "3. 발주 조회\t",
                    "4. 메뉴 나가기\t\t\t"
            };
            MenuBoxPrinter.printMenuBoxWithTitle("발주 조회\t\t", menuItems);

            int ch = Integer.parseInt(sc.nextLine().trim());
            switch (ch) {
                case 1:
                                if (!(
                        loginMemberRole == MemberEnum.ADMIN ||
                                loginMemberRole == MemberEnum.OPERATOR
                )) {
                    System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
                    break;
                }
                
                    Product product = new Product();
                    // 상품 재고 순으로 조회
                    List<Product> productList = orderService.getProductInventoryList();
                    System.out.print("\n➔ 발주할 상품을 선택하세요 : ");
                    int productNo = Integer.parseInt(sc.nextLine().trim());

                    if (productNo >= 1 && productNo <= productList.size())
                        product = productList.get(productNo - 1);
                    else {
                        System.out.println("잘못된 입력 입니다.");
                        break;
                    }

                    System.out.print("\n➔ 수량을 입력하세요 : ");
                    product.setInventoryCnt(Integer.parseInt(sc.nextLine().trim()));

                    // product 내용으로 발주 등록
                    System.out.print("\n➔ 납기 일자를 입력해주세요 (YYYY-mm-dd) : ");
                    String date = sc.nextLine().trim();
                    Long orderSeq = orderService.registerOrder(date, product);

                    // 등록된 내역 조회
                    OrderVO orderDetail = orderService.getOneOrderInformation(orderSeq);
                    break;
                case 2:
                    // 발주 확정
                    List<OrderVO> orderList = orderService.getAllOrdersStatusProgress();
                    System.out.print("\n➔ 확정할 발주 번호를 입력하세요. ");
                    Long orderProgressSeq = Long.parseLong(sc.nextLine().trim());

                    boolean flag = false;

                    for (OrderVO o : orderList) {
                        if (o.getOrderSeq() == orderProgressSeq) {
                            if (o.getOrderDetailStatus().equals(OrderStatusEnum.COMPLETE)) {
                                flag = true;
                            } else flag = false;
                        }
                    }
                    if (flag) {
                        orderService.updateOrderStauts(orderProgressSeq);
                        System.out.println(orderProgressSeq + "번의 발주가 확정되었습니다.");
                    } else {
                        System.out.println("발주 실패 - 미입고 상태입니다.");
                    }
                    break;
                case 3:
                    // 발주 조회
                    printAllOrdersWithDetails();
                    break;
                case 4:
                {return;}
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // 발주 등록
    public void registerOrder() {
        Product product = new Product();
        List<Product> productList = orderService.getProductInventoryList();
        System.out.print("발주할 상품을 선택하세요. ");
        int productNo = Integer.parseInt(sc.nextLine());

        if (productNo >= 1 && productNo <= productList.size())
            product = productList.get(productNo - 1);
        else {
            System.out.println("잘못된 입력 입니다.");
        }

        // 창고 선택
        List<WareHouse> wareHouses = wareHouseService.viewWareHouseByMemberId(loginMemberId);
        printWarehouseFormat(wareHouses);
        System.out.println("창고를 선택하세요. ");
        int warehouseNo = Integer.parseInt(sc.nextLine().trim());
        String warehouseCd = wareHouses.get(warehouseNo-1).getWarehouseCode();
        product.setWarehouseCode(warehouseCd);

        // 구역 선택
        List<WareHouseZone> wareHouseZones = wareHouseService.viewWareHouseZoneByWarehouseCd(warehouseCd);
        printWarehouseZoneFormat(wareHouseZones);
        System.out.println("구역을 선택하세요. ");
        int warehouseZoneNo = Integer.parseInt(sc.nextLine().trim());
        String warehouseZone = wareHouseZones.get(warehouseZoneNo-1).getZoneCode();
        product.setZoneCode(warehouseZone);

        System.out.print("수량을 입력하세요. ");
        product.setInventoryCnt(Integer.parseInt(sc.nextLine()));

        // product 내용으로 발주 등록
        System.out.println("납기 일자를 입력해주세요 (YYYY-mm-dd)");
        String date = sc.nextLine();
        Long orderSeq = orderService.registerOrder(date, product);

        // 등록된 내역 조회
        OrderVO orderDetail = orderService.getOneOrderInformation(orderSeq);

    }

    // 발주 확정
    public void completeOrder() {
        List<OrderVO> orderList = orderService.getAllOrdersStatusProgress();
        System.out.print("확정할 발주 번호를 입력하세요. ");
        Long orderProgressSeq = Long.parseLong(sc.nextLine());

        boolean flag = false;

        for (OrderVO o : orderList) {
            if (o.getOrderSeq() == orderProgressSeq) {
                if (o.getOrderDetailStatus().equals(OrderStatusEnum.COMPLETE)) {
                    flag = true;
                } else flag = false;
            }
        }
        if (flag) {
            orderService.updateOrderStauts(orderProgressSeq);
            System.out.println(orderProgressSeq + "번의 발주가 확정되었습니다.");

        } else {
            System.out.println("발주 실패 - 미입고 상태입니다.");
        }

    }

    public List<OrderVO> getAllOrdersWithDetails() {
        return orderService.getAllOrdersWithDetails();
    }

    public void printAllOrdersWithDetails() {

        // 권한 검사 로직
        if (!(loginMemberRole == MemberEnum.ADMIN || loginMemberRole == MemberEnum.WAREHOUSE_MANAGER || loginMemberRole == MemberEnum.OPERATOR)) {
            System.out.println("권한이 없습니다.");
            return;
        }
        List<OrderVO> orders = getAllOrdersWithDetails();


        System.out.printf("%-10s%-12s%-25s%-20s%-20s%-16s%-8s%-22s%s\n",
                "발주 번호", "발주 상태", "발주 상품 공급업체명", "발주 상품 배송예정일",
                "발주 완료일", "발주 상세 번호", "발주 수량", "발주 상품 코드", "창고 코드");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------");

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

    public void printWarehouseFormat(List<WareHouse> wareHouses) {
        System.out.printf("%-10s %-12s %-25s %-20s %-15s\n",
                "창고 번호", "창고 코드", "창고명", "창고 위치", "창고 타입");
        System.out.println("------------------------------------------------------------------------------");

        int idx = 1;
        for (WareHouse wareHouse : wareHouses) {
            System.out.printf("%-10d %-12s %-25s %-20s %-15s\n",
                    idx++,
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseName(),
                    wareHouse.getWarehouseLocation(),
                    wareHouse.getWarehouseType());
        }
    }

    public void printWarehouseZoneFormat(List<WareHouseZone> wareHouseZones) {
        System.out.printf("%-10s %-12s %-25s\n",
                "창고 번호", "구역 코드", "구역명");
        System.out.println("------------------------------------------------------------------------------");

        int idx = 1;
        for (WareHouseZone wareHouseZone : wareHouseZones) {
            System.out.printf("%-10d %-12s %-25s\n",
                    idx++,
                    wareHouseZone.getZoneCode(),
                    wareHouseZone.getZoneName());
        }
    }
}

