package controller;

import dao.LoginManagementDAOImpl;
import service.PurchaseService;

import util.enumcollect.MemberEnum;

import java.util.List;
import java.util.Scanner;

public class PurchaseController {

    private static PurchaseController instance;
    private Scanner sc = new Scanner(System.in);
    private PurchaseService purchaseService;

    private LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
    private MemberEnum loginMemberRole;
    private String loginMemberId;

    private PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
        updateLoginInfo(); // 로그인 정보 초기화
    }

    public static PurchaseController getInstance(PurchaseService purchaseService) {
        if (instance == null) {
            instance = new PurchaseController(purchaseService);
        }
        return instance;
    }

    public void updateLoginInfo() {
        loginMemberRole = loginDao.getMemberRole();
        loginMemberId = loginDao.getMemberId();
    }

    public void menu() {
        updateLoginInfo();
        System.out.println("1. 주문 수집하기 | 2. 주문 조회하기 | 3. 주문 확정하기 | 4. 주문 클레임 수집하기 | 5. 메뉴 나가기");
        int ch = Integer.parseInt(sc.nextLine());

        System.out.println(loginMemberId);
        System.out.println(loginMemberRole);
        switch (ch) {
            case 1:
                // 빈 상태 -> 신규 주문으로 변경
                purchaseService.integrateShopPurchases("", "", List.of(""));
                menu();
                break;
            case 2:
                purchaseService.readAllPurchases();
                menu();
                break;
            case 3:
                purchaseService.updatePurchaseToConfirmed();
                menu();
                break;
            case 4:
                purchaseService.integrateShopClaims();
                purchaseService.updatePurchaseToCancel();
                menu();
                break;
            case 5:
                return;
            default:
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                menu();
        }
    }
}
