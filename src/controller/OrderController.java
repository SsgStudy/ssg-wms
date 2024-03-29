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

/**
 * 이 클래스는 발주 관리를 담당하는 OrderController 컨트롤러입니다.
 * 즈로 발주 등록, 발주 확정, 발주 조회와 같은 기능을 수행합니다.
 * 해당 클래스는 Singleton 패턴을 따르며, 하나의 인스턴스만을 생성하여 사용합니다.
 *
 * @author : 장서윤
 */
public class OrderController {
    private static OrderController instance;
    private Scanner sc = new Scanner(System.in);
    private OrderService orderService;
    private WareHouseService wareHouseService;
    private LoginManagementDAOImpl loginDao;
    private MemberEnum loginMemberRole;
    private String loginMemberId;
    static boolean menuContinue = true;
    static boolean allContinue = true;

    /**
     * Instantiates a new Order controller.
     *
     * @param orderService     the order service
     * @param wareHouseService the warehouse service
     */
    public OrderController(OrderService orderService, WareHouseService wareHouseService) {
        this.orderService = orderService;
        this.wareHouseService = wareHouseService;
        this.loginDao = LoginManagementDAOImpl.getInstance();
        updateLoginInfo();
    }

    /**
     * Gets instance.
     *
     * @param orderService     the order service
     * @param wareHouseService the warehouse service
     * @return the instance
     */
    public static synchronized OrderController getInstance(OrderService orderService, WareHouseService wareHouseService) {
        if (instance == null) {
            instance = new OrderController(orderService, wareHouseService);
        }
        return instance;
    }

    /**
     * Update login info.
     */
    public void updateLoginInfo() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
    }

    /**
     * Menu.
     */
    public void menu() {
        while (allContinue) {
            menuContinue = true;
            updateLoginInfo();
            String[] menuItems = {
                    "1. 발주 등록\t",
                    "2. 발주 확정\t",
                    "3. 발주 조회\t",
                    "4. 메뉴 나가기\t\t\t"
            };
            MenuBoxPrinter.printMenuBoxWithTitle("발주 조회\t\t", menuItems);

            while (menuContinue) {

                int ch = Integer.parseInt(sc.nextLine().trim());
                switch (ch) {
                    case 1:
                        registerOrder();
                        break;
                    case 2:
                        completeOrder();
                        break;
                    case 3:
                        printAllOrdersWithDetails();
                        break;
                    case 4: {
                        menuContinue = false;
                        allContinue = false;
                        break;
                    }
                    default:
                        System.out.println("잘못된 입력입니다.");
                        break;
                }
            }
        }
    }

    /**
     * Register order.
     */
    public void registerOrder() {
        Product product = new Product();
        List<Product> productList = orderService.getProductInventoryList();
        System.out.print("발주할 상품을 선택하세요. ");
        int productNo = Integer.parseInt(sc.nextLine());

        if (productNo >= 1 && productNo <= productList.size())
            product = productList.get(productNo - 1);
        else {
            System.out.println("잘못된 입력 입니다.");
            menuContinue = false;
            return;
        }

        List<WareHouse> wareHouses = wareHouseService.viewWareHouseByMemberId(loginMemberId);
        printWarehouseFormat(wareHouses);
        System.out.println("창고를 선택하세요. ");
        int warehouseNo = Integer.parseInt(sc.nextLine().trim());
        String warehouseCd = wareHouses.get(warehouseNo - 1).getWarehouseCode();
        product.setWarehouseCode(warehouseCd);

        List<WareHouseZone> wareHouseZones = wareHouseService.viewWareHouseZoneByWarehouseCd(warehouseCd);
        printWarehouseZoneFormat(wareHouseZones);
        System.out.println("구역을 선택하세요. ");
        int warehouseZoneNo = Integer.parseInt(sc.nextLine().trim());
        String warehouseZone = wareHouseZones.get(warehouseZoneNo - 1).getZoneCode();
        product.setZoneCode(warehouseZone);

        System.out.print("수량을 입력하세요. ");
        product.setInventoryCnt(Integer.parseInt(sc.nextLine()));

        System.out.println("납기 일자를 입력해주세요 (YYYY-mm-dd)");
        String date = sc.nextLine();
        Long orderSeq = orderService.registerOrder(date, product);

        OrderVO orderDetail = orderService.getOneOrderInformation(orderSeq);
        menuContinue = false;
    }

    /**
     * Complete order.
     */
    public void completeOrder() {
        List<OrderVO> orderList = orderService.getAllOrdersStatusProgress();
        System.out.print("확정할 발주 번호를 입력하세요. ");
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
        menuContinue = false;
    }

    /**
     * Gets all orders with details.
     *
     * @return the all orders with details
     */
    public List<OrderVO> getAllOrdersWithDetails() {
        return orderService.getAllOrdersWithDetails();
    }

    /**
     * Print all orders with details.
     */
    public void printAllOrdersWithDetails() {

        List<OrderVO> orders = getAllOrdersWithDetails();


        System.out.printf("%-12s %-14s %-30s %-15s %-18s %-10s %-26s %s\n",
                "발주 번호", "발주 상태", "공급 업체명", "배송 예정일", "발주 상세 번호", "발주 수량", "발주 상품 코드", "창고 코드");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (OrderVO order : orders) {
            System.out.printf("%-12d %-14s %-30s %-24s %-18d %-10d %-26s %s\n",
                    order.getOrderSeq(),
                    order.getOrderStatus(),
                    order.getIncomingProductSupplierName(),
                    order.getDeliveryDate(),
                    order.getOrderDetailSeq(),
                    order.getOrderCnt(),
                    order.getProductCode(),
                    order.getWarehouseCode());
        }

        menuContinue = false;
    }

    /**
     * Print warehouse format.
     *
     * @param wareHouses the warehouses
     */
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

    /**
     * Print warehouse zone format.
     *
     * @param wareHouseZones the ware house zones
     */
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

