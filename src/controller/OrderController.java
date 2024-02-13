package controller;

import dao.LoginManagementDAOImpl;
import java.util.List;
import service.OrderService;
import util.enumcollect.MemberEnum;
import vo.OrderVO;

public class OrderController {
    private OrderService orderService;
    private LoginManagementDAOImpl loginDao;
    private MemberEnum loginMemberRole;
    private String loginMemberId;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
        this.loginDao = LoginManagementDAOImpl.getInstance();
        updateLoginInfo(); // 로그인 정보 초기화
    }
    public void updateLoginInfo() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
        System.out.println("로그인 유지 정보 출력 아이디 : " + loginMemberId);
        System.out.println("로그인 유지 정보 출력 권한 : " + loginMemberRole);
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
