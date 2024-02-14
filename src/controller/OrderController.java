package controller;

import dao.LoginManagementDAOImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import service.OrderService;
import service.PurchaseService;
import util.enumcollect.MemberEnum;
import vo.OrderVO;
import vo.Product;

public class OrderController {
    private static OrderController instance;
    private Scanner sc = new Scanner(System.in);

    private OrderService orderService;
    private LoginManagementDAOImpl loginDao;
    private MemberEnum loginMemberRole;
    private String loginMemberId;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
        this.loginDao = LoginManagementDAOImpl.getInstance();
        updateLoginInfo(); // 로그인 정보 초기화
    }

    public static OrderController getInstance(OrderService orderService) {
        if (instance == null) {
            instance = new OrderController(orderService);
        }
        return instance;
    }

    public void updateLoginInfo() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
        System.out.println("로그인 유지 정보 출력 아이디 : " + loginMemberId);
        System.out.println("로그인 유지 정보 출력 권한 : " + loginMemberRole);
    }

    public void menu() {
        updateLoginInfo();
        System.out.println("1. 발주 등록 | 2. 발주 확정 | 3. 발주 조회 | 4. 메뉴 나가기");
        String ch = sc.nextLine();
        System.out.println(loginMemberId);
        System.out.println(loginMemberRole);
        switch (ch) {
            case "1":
                Product product = new Product();
                // 상품 재고 순으로 조회
                List<Product> productList = orderService.getProductInventoryList();
                System.out.print("발주할 상품을 선택하세요. ");
                int productNo = Integer.parseInt(sc.nextLine());

                if (productNo >= 1 && productNo <= productList.size())
                    product = productList.get(productNo - 1);
                else {
                    System.out.println("잘못된 입력 입니다.");
                }

                System.out.print("수량을 입력하세요. ");
                product.setInventoryCnt(Integer.parseInt(sc.nextLine()));

                // product 내용으로 발주 등록
                System.out.println("납기 일자를 입력해주세요 (YYYY-mm-dd)");
                String date = sc.nextLine();
                Long orderSeq = orderService.registerOrder(date, product);

                // 등록된 내역 조회
                OrderVO orderDetail = orderService.getOneOrderInformation(orderSeq);
                menu();
                break;
            case "2":
                // 발주 확정 - 여기서는 발주건 하나에 대한 세부발주건 상태가 모두 입고 완료인 경우 확정할 수 있도록 함. 상태 COMPLETE으로 변경
                menu();
                break;
            case "3":
                // 발주 상태 조회
                menu();
                break;
            case "4":
                return;
            default:
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                menu();
        }
    }

    public List<OrderVO> getAllOrdersWithDetails() {
        return orderService.getAllOrdersWithDetails();
    }

    public void printAllOrdersWithDetails() {
        System.out.println("현재 로그인한 사용자: " + loginMemberId);
        System.out.println("사용자 권한: " + loginMemberRole);

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
}
