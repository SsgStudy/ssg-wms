package controller;

import dao.LoginManagementDAOImpl;
import service.PurchaseService;

import util.enumcollect.MemberEnum;
import vo.PurchaseVO;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        System.out.println("로그인 유지 정보 업데이트: ID = " + loginMemberId + ", Role = " + loginMemberRole);
    }

    public void menu() {
        updateLoginInfo();
        System.out.println("1. 주문 수집하기 | 2. 주문 조회하기 | 3. 주문 클레임 수집하기 | 4. 메뉴 나가기");
        int ch = Integer.parseInt(sc.nextLine().trim());

        System.out.println(loginMemberId);
        System.out.println(loginMemberRole);
        switch (ch) {
            case 1:
                List<Long> shopPurchaseSeqList = promptForPurchase();
                updatePurchaseStatusForNewPurchase(shopPurchaseSeqList);
                menu();
                break;
            case 2:
                printForAllPurchaseList();
                menu();
                break;
            case 3:
                promptForPurchaseClaim();
                menu();
                break;
            case 4:
                return;
            default:
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                menu();
        }
    }

    public void purchaseMenu() {
        System.out.println("1. 주문 확정 | 2. 돌아가기");
        int ch = Integer.parseInt(sc.nextLine().trim());

        switch (ch) {
            case 1 -> {
                promptForPurchaseConfirmed();
                purchaseMenu();
            }
            case 2 -> {
                return;
            }
            default -> {
                System.out.println("잘못 입력하셨습니다.");
                purchaseMenu();
            }
        }
    }

    // 주문 수집 일자 쇼핑몰 선택
    public List<Long> promptForPurchase() {
        System.out.println("주문 수집 일자를 입력해주세요 (YYYY-mm-DD YYYY-mm-DD)");
        String date = sc.nextLine();
        date = getDateFormatCheck(date);

        System.out.println("쇼핑몰 리스트");
        List<String> shopList = purchaseService.getShoppingmallList();
        shopList.forEach(idx -> System.out.println((shopList.indexOf(idx) + 1) + ". " + idx));

        System.out.println("수집할 쇼핑몰의 번호를 입력해주세요. (1 2 3)");
        List<Integer> selectedShopIndexes = Arrays.stream(sc.nextLine().split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return purchaseService.integrateShopPurchases(date, selectedShopIndexes);
    }

    // 주문 수집 시 신규 등록
    public void updatePurchaseStatusForNewPurchase(List<Long> shopPurchaseSeqList) {
        purchaseService.updateNewPurchaseStatus(shopPurchaseSeqList);
        if (!shopPurchaseSeqList.isEmpty()) {
            printForPurchaseList(shopPurchaseSeqList);
            purchaseMenu();
        }
    }

    // 주문 수집 내역 조회
    public void printForPurchaseList(List<Long> shopPurchaseSeqList) {
        if (!shopPurchaseSeqList.isEmpty()) {
            purchasePrintFormat(purchaseService.getPurchaseListByPurchaseSeq(shopPurchaseSeqList));
        }
    }

    // 주문 확정하기
    public void promptForPurchaseConfirmed() {
        System.out.println("확정할 주문의 번호를 입력해주세요. (1 2 3)");
        List<Long> selectedPurchaseSeq = List.of(sc.nextLine().split(" "))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        purchaseService.updatePurchaseToConfirmed(selectedPurchaseSeq);
        printForPurchaseList(selectedPurchaseSeq);
    }

    // 주문 수집 확정 내역 조회
    public void printForPurchaseConfirmList(List<PurchaseVO> shopPurchaseSeqList) {
        if (!shopPurchaseSeqList.isEmpty())
            purchasePrintFormat(shopPurchaseSeqList);
    }

    // 클레임 수집 일자, 쇼핑몰 선택
    public void promptForPurchaseClaim() {
        System.out.println("클레임 수집 일자를 입력해주세요 (YYYY-mm-DD YYYY-mm-DD)");
        String date = sc.nextLine();
        date = getDateFormatCheck(date);

        System.out.println("쇼핑몰 리스트");
        List<String> shopList = purchaseService.getShoppingmallList();
        shopList.forEach(idx -> System.out.println((shopList.indexOf(idx) + 1) + ". " + idx));

        System.out.println("수집할 쇼핑몰의 번호를 입력해주세요. (1 2 3)");
        List<Integer> selectedShopIndexes = Arrays.stream(sc.nextLine().split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Long> claimSeqList = purchaseService.integrateShopClaims(date, selectedShopIndexes);

        if (!claimSeqList.isEmpty()) {
            printForPurchaseClaimList(claimSeqList);
            promptForPurchaseCancelOrReturn();
        }
        else {
            System.out.println("반품 내역이 없습니다.");
        }

    }

    // 주문 클레임 처리 내역 조회
    public void printForPurchaseClaimList(List<Long> shopPurchaseSeqList) {
        purchaseClaimPrintFormat(purchaseService.getPurchaseClaimListByPurchaseSeq(shopPurchaseSeqList));
    }

    // 주문 취소/반품 하기
    public void promptForPurchaseCancelOrReturn() {
        System.out.println("취소/반품할 주문의 번호를 입력해주세요. (1 2 3)");
        Long purchaseSeq = Long.parseLong(sc.nextLine());

        String status = purchaseService.processPurchaseToCancelOrReturn(purchaseSeq);
        purchaseService.updatePurchaseToCancel(purchaseSeq, status);
    }

    public void printForAllPurchaseList() {
        List<PurchaseVO> purchaseList = purchaseService.readAllPurchases();
        purchasePrintFormat(purchaseList);
    }

    public void purchasePrintFormat(List<PurchaseVO> purchaseList) {
        System.out.println("주문 리스트");
        System.out.printf("%-10s %-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-10s %-10s %n",
                "주문 번호", "주문 상태", "주문 일시", "주문 상세 번호", "쇼핑몰", "상품명", "브랜드", "구매자", "연락처 번호", "주문 수량", "판매 금액");

        System.out.println("----------------------------------------------------------------------------------------------------------------");

        purchaseList.forEach(purchase -> {
            System.out.printf("%-10s %-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-10d %-10d %n",
                    purchase.getShopPurchaseSeq(),
                    purchase.getShopPurchaseStatus(),
                    purchase.getShopPurchaseDate().toString(),
                    purchase.getShopPurchaseDetailSeq(),
                    purchase.getShopName(),
                    purchase.getProductName(),
                    purchase.getProductBrand(),
                    purchase.getShopPurchaseName(),
                    purchase.getShopPurchaseTel(),
                    purchase.getProductCnt(),
                    purchase.getProductCnt() * purchase.getProductPrice());
        });

        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    public void purchaseClaimPrintFormat(List<PurchaseVO> purchaseList) {
        System.out.println("주문 리스트");
        System.out.printf("%-10s %-10s %-10s %-15s %-15s %-15s %-15s %n",
                "주문 번호", "클레임", "주문 상태", "주문 일시", "쇼핑몰", "구매자", "연락처 번호");

        System.out.println("----------------------------------------------------------------------------------------------------------------");

        purchaseList.forEach(purchase -> {
            System.out.printf("%-10s %-10s %-10s %-15s %-15s %-15s %-15s %n",
                    purchase.getShopPurchaseSeq(),
                    purchase.getShopPurchaseClaim(),
                    purchase.getShopPurchaseStatus(),
                    purchase.getShopPurchaseDate().toString(),
                    purchase.getShopName(),
                    purchase.getShopPurchaseName(),
                    purchase.getShopPurchaseTel());
        });

        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    public String getDateFormatCheck(String date) {
        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{4}-\\d{2}-\\d{2}$");

        System.out.println("날짜 범위를 'YYYY-mm-DD YYYY-mm-DD' 형식으로 입력해주세요:");

        while (!pattern.matcher(date).matches()) {
            System.out.println("입력 형식이 올바르지 않습니다. 다시 입력해주세요:");
            date = scanner.nextLine();
        }

        return date;
    }


}
