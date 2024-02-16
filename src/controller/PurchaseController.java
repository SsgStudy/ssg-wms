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

/**
 * 이 클래스는 주문을 담당하는 PurchaseController 컨트롤러입니다.
 * 주로 주문 수집, 주문 조회, 클레임 수집, 송장 처리, 반품과 같은 기능을 수행합니다.
 * 해당 클래스는 Singleton 패턴을 따르며, 하나의 인스턴스만을 생성하여 사용합니다.
 *
 * @author : 장서윤
 */
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

    /**
     * Instantiates a new Purchase controller.
     *
     * @param purchaseService            the purchase service
     * @param invoiceService             the invoice service
     * @param inventoryAdjustmentService the inventory adjustment service
     */
    private PurchaseController(PurchaseService purchaseService, InvoiceService invoiceService, InventoryAdjustmentService inventoryAdjustmentService) {
        this.purchaseService = purchaseService;
        this.invoiceService = invoiceService;
        this.inventoryAdjustmentService = inventoryAdjustmentService;
        updateLoginInfo();
    }

    /**
     * Gets instance.
     *
     * @param purchaseService            the purchase service
     * @param invoiceService             the invoice service
     * @param inventoryAdjustmentService the inventory adjustment service
     * @return the instance
     */
    public static PurchaseController getInstance(PurchaseService purchaseService, InvoiceService invoiceService, InventoryAdjustmentService inventoryAdjustmentService) {
        if (instance == null) {
            instance = new PurchaseController(purchaseService, invoiceService, inventoryAdjustmentService);
        }
        return instance;
    }

    /**
     * Update login info.
     */
    public void updateLoginInfo() {
        loginMemberRole = loginDao.getMemberRole();
        loginMemberId = loginDao.getMemberId();
    }

    /**
     * Menu.
     */
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

    /**
     * Purchase menu.
     */
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

    /**
     * Prompt for purchase list.
     *
     * @return the list
     */
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

    /**
     * Update purchase status for new purchase.
     * <p>
     * 접근제한 : 창고관리자
     * <p>
     * @param shopPurchaseSeqList the shop purchase seq list
     */
    public void updatePurchaseStatusForNewPurchase(List<Long> shopPurchaseSeqList) {
        if (!(
                loginMemberRole == MemberEnum.ADMIN ||
                        loginMemberRole == MemberEnum.OPERATOR
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        purchaseService.updateNewPurchaseStatus(shopPurchaseSeqList);
        if (!shopPurchaseSeqList.isEmpty()) {
            printForPurchaseList(shopPurchaseSeqList);
            purchaseMenu();
        }
    }

    /**
     * Print for purchase list.
     *
     * @param shopPurchaseSeqList the shop purchase seq list
     */
    public void printForPurchaseList(List<Long> shopPurchaseSeqList) {
        if (!shopPurchaseSeqList.isEmpty()) {
            purchasePrintFormat(purchaseService.getPurchaseListByPurchaseSeq(shopPurchaseSeqList));
        }
    }

    /**
     * Prompt for purchase confirmed.
     * <p>
     * 접근제한 : 창고관리자
     */
    public void promptForPurchaseConfirmed() {
        if (!(
                loginMemberRole == MemberEnum.ADMIN ||
                        loginMemberRole == MemberEnum.OPERATOR
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        System.out.println("\n➔ 확정할 주문의 번호를 입력해주세요. (1 2 3) : ");
        List<Long> selectedPurchaseSeq = List.of(sc.nextLine().split(" "))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        purchaseService.updatePurchaseToConfirmed(selectedPurchaseSeq);
        printForPurchaseList(selectedPurchaseSeq);
    }

    /**
     * Print for purchase confirm list.
     *
     * @param shopPurchaseSeqList the shop purchase seq list
     */
    public void printForPurchaseConfirmList(List<PurchaseVO> shopPurchaseSeqList) {
        if (!shopPurchaseSeqList.isEmpty())
            purchasePrintFormat(shopPurchaseSeqList);
    }

    /**
     * Prompt for purchase claim.
     */
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
        } else {
            System.out.println("반품 내역이 없습니다.");
        }
    }

    /**
     * Print for purchase claim list.
     *
     * @param shopPurchaseSeqList the shop purchase seq list
     */
    public void printForPurchaseClaimList(List<Long> shopPurchaseSeqList) {
        purchaseClaimPrintFormat(purchaseService.getPurchaseClaimListByPurchaseSeq(shopPurchaseSeqList));
    }

    /**
     * Prompt for purchase cancel or return.
     */
    public void promptForPurchaseCancelOrReturn() {
        System.out.println("\n➔ 취소/반품할 주문의 번호를 입력해주세요. (1 2 3) : ");
        Long purchaseSeq = Long.parseLong(sc.nextLine());

        String status = purchaseService.processPurchaseToCancelOrReturn(purchaseSeq);

        if (status.equals("CANCEL")) {
            System.out.println(purchaseSeq + "번 주문이 취소 되었습니다.");
        } else {
            Long purchaseReturnKey = purchaseService.createPurchaseReturn(purchaseSeq);

            if (status.equals("RETURN") || status.equals("RESTORE")) {
                purchaseService.updatePurchaseStatusToReturn(purchaseReturnKey, PurchaseEnum.반품완료);
                purchaseService.updatePurchaseStatusEx(purchaseReturnKey,PurchaseEnum.반품완료);
                System.out.println(purchaseSeq + "번 주문이 반품 처리 되었습니다.");


            } else if (status.equals("INVOICE")) {
                System.out.println("반품 처리 중 입니다.");
                purchaseReturnMenu(purchaseReturnKey);
            } else {
                logger.info("null 값");
            }
        }
    }

    /**
     * Purchase return menu.
     *
     * @param purchaseSeq the purchase seq
     */
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
                restoreInventory(purchaseSeq);
                purchaseReturnMenu(purchaseSeq);
            }
            case 3 -> {
                checkRestoreInventory(purchaseSeq);
                purchaseReturnMenu(purchaseSeq);
            }
            case 4 -> {
                return;
            }
            default -> {
                System.out.println("다시 입력하세요");
            }
        }

    }

    /**
     * Prompt invoice int.
     *
     * @param outgoingProductVO the outgoing product vo
     * @return the int
     */
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

        try {
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

            } catch (NumberFormatException e) {
                logger.info("숫자로 입력하세요.");
                e.printStackTrace();
            }
        } catch (IOException | SQLException i) {
            i.printStackTrace();
        }

        return status;
    }

    /**
     * Restore inventory.
     *
     * @param purchaseSeq the purchase seq
     */
    public void restoreInventory(Long purchaseSeq) {
        int result = inventoryAdjustmentService.updateRestoreInventoryQuantity(purchaseSeq);
        purchaseService.updatePurchaseStatusToReturn(purchaseSeq, PurchaseEnum.반품입고);
    }

    /**
     * Check restore inventory.
     *
     * @param purchaseSeq the purchase seq
     */
    public void checkRestoreInventory(Long purchaseSeq) {
        int quantity = inventoryAdjustmentService.updateRestoration(purchaseSeq);
        if (quantity == 1)
            purchaseService.updatePurchaseStatusToReturn(purchaseSeq, PurchaseEnum.반품완료);
        else
            System.out.println("반품 실패");

    }

    /**
     * Print for all purchase list.
     */
    public void printForAllPurchaseList() {
        List<PurchaseVO> purchaseList = purchaseService.readAllPurchases();
        purchasePrintFormat(purchaseList);
    }

    /**
     * Purchase print format.
     *
     * @param purchaseList the purchase list
     */
    public void purchasePrintFormat(List<PurchaseVO> purchaseList) {
        System.out.println("주문 리스트");
        System.out.printf("%-10s %-10s %-15s %-15s %-15s %-15s %n",
                "주문 번호", "주문 상태", "주문 일시", "쇼핑몰", "구매자", "전화번호");

        System.out.println("----------------------------------------------------------------------------------------------------------------");

        purchaseList.forEach(purchase -> {
            System.out.printf("%-10s %-10s %-15s %-15s %-15s %-15s %n",
                    purchase.getShopPurchaseSeq(),
                    purchase.getShopPurchaseStatus(),
                    purchase.getShopPurchaseDate().toString(),
                    purchase.getShopName(),
                    purchase.getShopPurchaseName(),
                    purchase.getShopPurchaseTel());
        });

        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Purchase claim print format.
     *
     * @param purchaseList the purchase list
     */
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

    /**
     * Gets date format check.
     *
     * @param date the date
     * @return the date format check
     */
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
