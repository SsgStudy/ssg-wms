package service;

import dao.PurchaseDAOImpl;
import util.enumcollect.PurchaseEnum;
import vo.OutgoingProductVO;
import vo.PurchaseVO;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The type Purchase service.
 */
public class PurchaseServiceImpl implements PurchaseService {
    static Scanner sc = new Scanner(System.in);
    private static PurchaseServiceImpl instance;
    private PurchaseDAOImpl purchaseDAO;

    /**
     * Instantiates a new Purchase service.
     *
     * @param purchaseDAO the purchase dao
     */
    public PurchaseServiceImpl(PurchaseDAOImpl purchaseDAO) {
        this.purchaseDAO = purchaseDAO;
    }

    /**
     * Instantiates a new Purchase service.
     */
    public PurchaseServiceImpl() {}

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized PurchaseServiceImpl getInstance() {
        if (instance == null) {
            instance = new PurchaseServiceImpl();
        }
        return instance;
    }

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
            System.out.println(shopPurchaseSeqList.size() + 1 + "건의 주문이 수집되었습니다.");
        } else
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
            purchaseDAO.updatePurchaseStatusCancelOrReturn(claimSeqList);
            System.out.println(claimSeqList.size() + "건의 취소/반품 내역이 있습니다.");
        } else {
            System.out.println("수집/반품 내역이 없습니다.");
        }

        return claimSeqList;
    }

    @Override
    public List<PurchaseVO> getPurchaseClaimListByPurchaseSeq(List<Long> claimSeqList) {
        return purchaseDAO.getPurchaseClaimListByPurchaseSeq(claimSeqList);
    }

    @Override
    public List<String> getShoppingmallList() {
        return purchaseDAO.findShopName();
    }

    @Override
    public int updatePurchaseToConfirmed(List<Long> selectedPurchaseSeq) {
        return purchaseDAO.updatePurchaseStatus(selectedPurchaseSeq, PurchaseEnum.주문확정);
    }

    @Override
    public String processPurchaseToCancelOrReturn(Long purchaseSeq) {
        return purchaseDAO.processPurchaseCancelOrReturn(purchaseSeq);
    }

    @Override
    public Long createPurchaseReturn(Long purchaseSeq) {
        return purchaseDAO.createPurchaseReturn(purchaseSeq);
    }

    @Override
    public int updatePurchaseStatusToReturn(Long purchaseSeq, PurchaseEnum purchaseEnum) {
        return purchaseDAO.updatePurchaseCancelStatus(purchaseSeq, purchaseEnum);
    }

    @Override
    public List<PurchaseVO> readAllPurchases() {
        return purchaseDAO.findAll();
    }
}