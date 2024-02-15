package controller;

import dao.LoginManagementDAOImpl;
import service.InventoryAdjustmentService;
import service.InvoiceService;
import service.PurchaseService;

import util.MenuBoxPrinter;
import util.enumcollect.MemberEnum;
import util.enumcollect.PurchaseEnum;
import util.enumcollect.WaybillTypeEnum;
import vo.Invoice;
import vo.OutgoingProductVO;
import vo.PurchaseVO;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PurchaseController {

    private static Logger logger = Logger.getLogger(PurchaseController.class.getName());


    private static PurchaseController instance;
    private Scanner sc = new Scanner(System.in);
    private PurchaseService purchaseService;
    private InvoiceService invoiceService;
    private InventoryAdjustmentService inventoryAdjustmentService;

    private LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
    private MemberEnum loginMemberRole;
    private String loginMemberId;

    private PurchaseController(PurchaseService purchaseService, InvoiceService invoiceService, InventoryAdjustmentService inventoryAdjustmentService) {
        this.purchaseService = purchaseService;
        this.invoiceService = invoiceService;
        this.inventoryAdjustmentService = inventoryAdjustmentService;
        updateLoginInfo(); // 로그인 정보 초기화
    }

    public static PurchaseController getInstance(PurchaseService purchaseService, InvoiceService invoiceService, InventoryAdjustmentService inventoryAdjustmentService) {
        if (instance == null) {
            instance = new PurchaseController(purchaseService, invoiceService, inventoryAdjustmentService);
        }
        return instance;
    }

    public void updateLoginInfo() {
        loginMemberRole = loginDao.getMemberRole();
        loginMemberId = loginDao.getMemberId();
    }

    public void menu() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
        updateLoginInfo();
        String[] menuItems = {
                "1. 주문 수집하기\t\t\t",
                "2. 주문 조회하기\t\t\t",
                "3. 주문 클레임 수집하기\t\t\t\t",
                "4. 메뉴 나가기\t\t\t",
        };
        MenuBoxPrinter.printMenuBoxWithTitle("주문 관리\t\t", menuItems);

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
        String[] menuItems = {
                "1. 주문 확정\t",
                "2. 메뉴 나가기\t\t\t",
        };
        MenuBoxPrinter.printMenuBoxWithTitle("주문 수집 목록\t\t\t\t", menuItems);
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
        System.out.print("\n➔ 주문 수집 일자를 입력해주세요 (YYYY-mm-DD YYYY-mm-DD) : ");
        String date = sc.nextLine();
        date = getDateFormatCheck(date);

        System.out.println("쇼핑몰 리스트");
        List<String> shopList = purchaseService.getShoppingmallList();
        shopList.forEach(idx -> System.out.println((shopList.indexOf(idx) + 1) + ". " + idx));

        System.out.println("\n➔ 수집할 쇼핑몰의 번호를 입력해주세요. (1 2 3) : ");
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
        System.out.println("\n➔ 확정할 주문의 번호를 입력해주세요. (1 2 3) : ");
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
        System.out.println("\n➔ 클레임 수집 일자를 입력해주세요 (YYYY-mm-DD YYYY-mm-DD) : ");
        String date = sc.nextLine();
        date = getDateFormatCheck(date);

        System.out.println("쇼핑몰 리스트");
        List<String> shopList = purchaseService.getShoppingmallList();
        shopList.forEach(idx -> System.out.println((shopList.indexOf(idx) + 1) + ". " + idx));

        System.out.println("\n➔ 수집할 쇼핑몰의 번호를 입력해주세요. (1 2 3) : ");
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
        System.out.println("\n➔ 취소/반품할 주문의 번호를 입력해주세요. (1 2 3) : ");
        Long purchaseSeq = Long.parseLong(sc.nextLine());

        String status = purchaseService.processPurchaseToCancelOrReturn(purchaseSeq);

        if (status.equals("CANCEL")) {
            System.out.println(purchaseSeq + "번 주문이 취소 되었습니다.");
        } else {
            if (status.equals("RETURN")) {
                purchaseService.createPurchaseReturn(purchaseSeq);
                purchaseService.updatePurchaseStatusToReturn(purchaseSeq, PurchaseEnum.반품완료);
                System.out.println(purchaseSeq + "번 주문이 반품 처리 되었습니다.");
            } else if (status.equals("INVOICE")) {
                purchaseService.createPurchaseReturn(purchaseSeq);
                System.out.println(purchaseSeq + "번 주문이 반품 처리 중에 있습니다.");
                purchaseReturnMenu(purchaseSeq);
            }
            else {
                logger.info("null 값");
            }
        }
    }

    // 반품 절차
    public void purchaseReturnMenu(Long purchaseSeq) {
        OutgoingProductVO outgoingProduct = new OutgoingProductVO();
        outgoingProduct.setShopPurchaseSeq(purchaseSeq);
        String[] menuItems2 = {
                "1. 송장 접수\t",
                "2. 입고 확인\t",
                "3. 검수",
                "4. 메뉴 나가기\t\t\t",
        };
        MenuBoxPrinter.printMenuBoxWithTitle("클레임 관리\t\t\t", menuItems2);
        int ch = Integer.parseInt(sc.nextLine());

        switch (ch) {
            case 1 -> {
                try {
                    promptInvoice(outgoingProduct);
                } catch (Exception e) {
                    System.out.println();
                }
                purchaseReturnMenu(purchaseSeq);
            }
            case 2 -> {
                // 창고에 재고 증가
                inventoryAdjustmentService.updateRestoreInventoryQuantity(purchaseSeq);
                // 주문 상태 - 반품 입고
                purchaseService.updatePurchaseStatusToReturn(purchaseSeq, PurchaseEnum.반품입고);
                purchaseReturnMenu(purchaseSeq);
            }
            case 3 -> {
                // 검수 - 출고 select by purchaseSeq 상품 일련 번호 -> 창고구역 tb join 재고 변경 이력
                int quantity = inventoryAdjustmentService.updateRestoration(purchaseSeq);
                // 주문 상태 - 반품 완료
                if (quantity == 1)
                    purchaseService.updatePurchaseStatusToReturn(purchaseSeq, PurchaseEnum.반품완료);
                else
                    System.out.println("반품 실패");
                purchaseReturnMenu(purchaseSeq);
            }
            case 4-> {
                return;
            }
            default -> {
                System.out.println("다시 입력하세요");
            }
        }

    }

    public int promptInvoice(OutgoingProductVO outgoingProductVO) {
        Invoice invoice = new Invoice();
        int status = 0;
        Long invoiceSeq = 0L;

        String[] menuItems = {
                "1. 일반\t",
                "2. 특급\t",
                "3. 국제\t",
                "4. 등기\t\t",
        };
        MenuBoxPrinter.printMenuBoxWithTitle("송장 종류 선택\t", menuItems);



        try{
            int ch = Integer.parseInt(sc.nextLine().trim());

            switch (ch) {
                case 1 -> invoice.setInvoiceType(WaybillTypeEnum.STANDARD);
                case 2 -> invoice.setInvoiceType(WaybillTypeEnum.EXPRESS);
                case 3 -> invoice.setInvoiceType(WaybillTypeEnum.INTERNATIONAL);
                case 4 -> invoice.setInvoiceType(WaybillTypeEnum.REGISTERED);
            }

            invoice.setPurchaseSeq(outgoingProductVO.getShopPurchaseSeq());

            try {
                String[] menuItems2 = {
                        "1. 한진택배\t",
                        "2. CJ대한통운\t",
                        "3. 우체국택배\t\t",
                        "4. 롯데택배\t",
                        "5. 로젠택배\t",
                };
                MenuBoxPrinter.printMenuBoxWithTitle("택배사 선택\t\t\t", menuItems2);
                invoice.setLogisticSeq(Long.parseLong(sc.nextLine()));

                invoiceSeq = invoiceService.registerInvoice(invoice);
                Invoice result = invoiceService.getInvoiceRowByInvoiceSeq(invoiceSeq);

                invoice.setInvoiceCode(result.getInvoiceCode());
                invoice.setInvoicePrintDate(result.getInvoicePrintDate());
                Blob qrCodeImage = invoiceService.createQRCode(invoice, outgoingProductVO);
                status = invoiceService.putQRCode(qrCodeImage, invoiceSeq);

            } catch (NumberFormatException e){
                logger.info("숫자로 입력하세요.");
                e.printStackTrace();
            }
        }catch (IOException | SQLException i){
            i.printStackTrace();
        }

        return status;
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
