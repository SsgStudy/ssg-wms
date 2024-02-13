package service;

import dao.PurchaseDAOImpl;
import util.enumcollect.PurchaseEnum;
import vo.PurchaseVO;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PurchaseServiceImpl implements PurchaseService {
    static Scanner sc = new Scanner(System.in);
    private static PurchaseServiceImpl instance;
    private PurchaseDAOImpl purchaseDAO;

    public PurchaseServiceImpl(PurchaseDAOImpl purchaseDAO) {
        this.purchaseDAO = purchaseDAO;
    }

    public PurchaseServiceImpl() {};

    public static synchronized PurchaseServiceImpl getInstance() {
        if (instance == null) {
            instance = new PurchaseServiceImpl();
        }
        return instance;
    }

    private InvoiceServiceImpl invoiceService = InvoiceServiceImpl.getInstance();
    private InventoryAdjustmentServiceImpl inventoryAdjustmentService = new InventoryAdjustmentServiceImpl();

    @Override
    public void integrateShopPurchases(String startDate, String endDate, List<String> shopName) {
        System.out.println("주문 수집 일자를 입력해주세요 (YYYY-mm-DD YYYY-mm-DD)");
        String date = sc.nextLine();
        String[] dates = date.split(" ");

        System.out.println("쇼핑몰 리스트");
        List<String> shopList = purchaseDAO.findShopName();
        IntStream.rangeClosed(1, shopList.size())
                .forEach(idx -> System.out.println(idx + ". " + shopList.get(idx - 1)));

        System.out.println("수집할 쇼핑몰의 번호를 입력해주세요. (1 2 3)");
        List<Integer> selectedShopIndexes = List.of(sc.nextLine().split(" "))
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<String> selectedShopNames = selectedShopIndexes.stream()
                .map(idx -> shopList.get(idx - 1))
                .collect(Collectors.toList());

        List<Long> shopPurchaseSeqList = purchaseDAO.getPurchaseByDateAndShopName(dates[0], dates[1], selectedShopNames);

        if (shopPurchaseSeqList.size() == 0)
            return;

        // 상태 변경
        int result = purchaseDAO.updatePurchaseStatus(shopPurchaseSeqList, PurchaseEnum.신규등록);
        System.out.println(result + "건의 주문이 수집되었습니다.");

        List<PurchaseVO> purchaseList = purchaseDAO.getPurchaseListByPurchaseSeq(shopPurchaseSeqList);

        print(purchaseList);
    }

    @Override
    public void updatePurchaseToConfirmed() {
        System.out.println("확정할 주문의 번호를 입력해주세요. (1 2 3)");
        List<Long> selectedPurchaseSeq = List.of(sc.nextLine().split(" "))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        int result = purchaseDAO.updatePurchaseStatus(selectedPurchaseSeq, PurchaseEnum.주문확정);

        System.out.println(result + "건의 주문이 확정 되었습니다.");

        List<PurchaseVO> resultList = purchaseDAO.getPurchaseListByPurchaseSeq(selectedPurchaseSeq);
        print(resultList);
    }

    @Override
    public void updatePurchaseToCancel() {
        System.out.println("취소/반품할 주문의 번호를 입력해주세요. (1 2 3)");
        Long purchaseSeq = Long.parseLong(sc.nextLine());
        String result = purchaseDAO.processPurchaseCancelOrReturn(purchaseSeq);

        if (result.equals("CANCEL")) {
            System.out.println(purchaseSeq + "번 주문이 취소 되었습니다.");
            // 상태 변경은 프로시저 안에서 처리됨
        } else if (result.equals("RETURN")) {
            purchaseDAO.createPurchaseCancel(purchaseSeq);
            purchaseDAO.updatePurchaseCancelStatus(purchaseSeq, PurchaseEnum.반품완료);
            System.out.println(purchaseSeq + "번 주문이 반품 처리 되었습니다.");
        } else if (result.equals("INVOICE")) {
            System.out.println(purchaseSeq + "번 주문이 반품 처리 중에 있습니다.");

            int ch = Integer.parseInt(sc.nextLine());
            System.out.println("1.송장 접수 | 2.입고 확인 | 3.검수");

            switch (ch) {
                case 1 -> {
                    try {
                        invoiceService.registerInvoice(purchaseSeq);
                    } catch (Exception e) {
                        System.out.println();
                    }
                }
                case 2 -> {
                    // 창고에 재고 증가
                    inventoryAdjustmentService.updateRestoreInventoryQuantity(purchaseSeq);
                    // 주문 상태 - 반품 입고
                    purchaseDAO.updatePurchaseCancelStatus(purchaseSeq, PurchaseEnum.반품입고);
                }
                case 3 -> {
                    // 검수 - 출고 select by purchaseSeq 상품 일련 번호 -> 창고구역 tb join 재고 변경 이력
                    int quantity = inventoryAdjustmentService.updateRestoration(purchaseSeq);
                    // 주문 상태 - 반품 완료
                    if (quantity == 1)
                        purchaseDAO.updatePurchaseCancelStatus(purchaseSeq, PurchaseEnum.반품완료);
                    else
                        System.out.println("반품 실패");
                }
            }
        } else
            System.out.println("null 값");
    }

    @Override
    public void readAllPurchases() {
        List<PurchaseVO> purchaseList = purchaseDAO.findAll();
        print(purchaseList);
    }

    @Override
    public void integrateShopClaims() {
        System.out.println("클레임 수집 일자를 입력해주세요 (YYYY-mm-DD YYYY-mm-DD)");
        String date = sc.nextLine();
        String[] dates = date.split(" ");

        System.out.println("쇼핑몰 리스트");
        List<String> shopList = purchaseDAO.findShopName();
        IntStream.rangeClosed(1, shopList.size())
                .forEach(idx -> System.out.println(idx + ". " + shopList.get(idx - 1)));

        System.out.println("수집할 쇼핑몰의 번호를 입력해주세요. (1 2 3)");
        List<Integer> selectedShopIndexes = List.of(sc.nextLine().split(" "))
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<String> selectedShopNames = selectedShopIndexes.stream()
                .map(idx -> shopList.get(idx - 1))
                .collect(Collectors.toList());

        // 수집
        List<Long> claimSeqList = purchaseDAO.getClaimByDateAndShopName(dates[0], dates[1], selectedShopNames);

        if (claimSeqList.size() < 1)
            return;

        // 상태 변경
        int result = purchaseDAO.updatePurchaseStatusCancelOrReturn(claimSeqList);

        System.out.println(result + "건의 취소/반품 내역이 있습니다.");

        // 클레임 수집 내용 조회
        List<PurchaseVO> claimList = purchaseDAO.getPurchaseListByPurchaseSeq(claimSeqList);
        printClaim(claimList);
    }


    public void print(List<PurchaseVO> purchaseList) {
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

    public void printClaim(List<PurchaseVO> purchaseList) {
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
}