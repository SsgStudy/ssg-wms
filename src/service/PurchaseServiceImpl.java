package service;

import dao.PurchaseDAOImpl;
import util.enumcollect.PurchaseEnum;
import vo.OutgoingProductVO;
import vo.PurchaseVO;

import java.util.*;
import java.util.regex.Pattern;
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
    public List<Long> integrateShopPurchases(String date, List<Integer> shopIdxes) {
        String[] dates = date.split(" ");

        List<String> shopList = getShoppingmallList();
        List<String> selectedShopNames = shopIdxes.stream()
                .map(idx -> shopList.get(idx - 1))
                .collect(Collectors.toList());

        return purchaseDAO.getPurchaseByDateAndShopName(dates[0], dates[1], selectedShopNames);
    }

    @Override
    public List<Long> updateNewPurchaseStatus(List<Long> shopPurchaseSeqList) {
        if (!shopPurchaseSeqList.isEmpty()) {
            // 상태 변경
            purchaseDAO.updatePurchaseStatus(shopPurchaseSeqList, PurchaseEnum.신규등록);
            System.out.println(shopPurchaseSeqList.size()+1 + "건의 주문이 수집되었습니다.");
        }
        else
            System.out.println("수집할 주문이 없습니다.");

        return shopPurchaseSeqList;
    }


    @Override
    public List<PurchaseVO> getPurchaseListByPurchaseSeq(List<Long> shopPurchaseSeqList) {
        return purchaseDAO.getPurchaseListByPurchaseSeq(shopPurchaseSeqList);
    }

    @Override
    public List<Long> integrateShopClaims(String date, List<Integer> shopIdxes) {
        String[] dates = date.split(" ");

        List<String> shopList = getShoppingmallList();
        List<String> selectedShopNames = shopIdxes.stream()
                .map(idx -> shopList.get(idx - 1))
                .collect(Collectors.toList());
        List<Long> claimSeqList = purchaseDAO.getClaimByDateAndShopName(dates[0], dates[1], selectedShopNames);

        if (!claimSeqList.isEmpty()) {
            int result = purchaseDAO.updatePurchaseStatusCancelOrReturn(claimSeqList);
            System.out.println(result + "건의 취소/반품 내역이 있습니다.");
        }
        else {
            System.out.println("수집/반품 내역이 없습니다.");
        }

        return claimSeqList;
    }

    @Override
    public List<PurchaseVO> getPurchaseClaimListByPurchaseSeq(List<Long> claimSeqList) {
        return purchaseDAO.getPurchaseClaimListByPurchaseSeq(claimSeqList);
    }
  
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
            OutgoingProductVO outgoingProduct = new OutgoingProductVO();
            outgoingProduct.setShopPurchaseSeq(purchaseSeq);
            int ch = Integer.parseInt(sc.nextLine());
            System.out.println("1.송장 접수 | 2.입고 확인 | 3.검수");

            switch (ch) {
                case 1 -> {
                    try {
//                        invoiceService.registerInvoice(outgoingProduct);
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
    public List<String> getShoppingmallList() {
        return purchaseDAO.findShopName();
    }

    @Override
    public int updatePurchaseToConfirmed(List<Long> selectedPurchaseSeq) {
        return purchaseDAO.updatePurchaseStatus(selectedPurchaseSeq, PurchaseEnum.주문확정);
    }

    // 한 건의 주문 취소 또는 반품
    @Override
    public String processPurchaseToCancelOrReturn(Long purchaseSeq) {
        return purchaseDAO.processPurchaseCancelOrReturn(purchaseSeq);
    }

    @Override
    public void updatePurchaseToCancel(Long purchaseSeq, String status) {
        if (status.equals("CANCEL")) {
            System.out.println(purchaseSeq + "번 주문이 취소 되었습니다.");
        } else {
            createPurchaseReturn(purchaseSeq);

            if (status.equals("RETURN")) {
                updatePurchaseStatusToReturnComplete(purchaseSeq);
                System.out.println(purchaseSeq + "번 주문이 반품 처리 되었습니다.");
            } else if (status.equals("INVOICE")) {
                System.out.println(purchaseSeq + "번 주문이 반품 처리 중에 있습니다.");
                purchaseReturnMenu(purchaseSeq);
            } else
                System.out.println("null 값");
        }
    }

    public void purchaseReturnMenu(Long purchaseSeq) {
        OutgoingProductVO outgoingProduct = new OutgoingProductVO();
        outgoingProduct.setShopPurchaseSeq(purchaseSeq);
        System.out.println("1.송장 접수 | 2.입고 확인 | 3.검수 | 4.돌아가기");
        int ch = Integer.parseInt(sc.nextLine());
        switch (ch) {
            case 1 -> {
                try {
                    invoiceService.registerInvoice(outgoingProduct);
                } catch (Exception e) {
                    System.out.println();
                }
                purchaseReturnMenu(purchaseSeq);
            }
            case 2 -> {
                // 창고에 재고 증가
                inventoryAdjustmentService.updateRestoreInventoryQuantity(purchaseSeq);
                // 주문 상태 - 반품 입고
                purchaseDAO.updatePurchaseCancelStatus(purchaseSeq, PurchaseEnum.반품입고);
                purchaseReturnMenu(purchaseSeq);
            }
            case 3 -> {
                // 검수 - 출고 select by purchaseSeq 상품 일련 번호 -> 창고구역 tb join 재고 변경 이력
                int quantity = inventoryAdjustmentService.updateRestoration(purchaseSeq);
                // 주문 상태 - 반품 완료
                if (quantity == 1)
                    purchaseDAO.updatePurchaseCancelStatus(purchaseSeq, PurchaseEnum.반품완료);
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

    // 반품건 생성
    @Override
    public int createPurchaseReturn(Long purchaseSeq) {
        return purchaseDAO.createPurchaseReturn(purchaseSeq);
    }

    @Override
    public int updatePurchaseStatusToReturnComplete(Long purchaseSeq) {
        return purchaseDAO.updatePurchaseCancelStatus(purchaseSeq, PurchaseEnum.반품완료);
    }


    @Override
    public List<PurchaseVO> readAllPurchases() {
        return purchaseDAO.findAll();
    }





}