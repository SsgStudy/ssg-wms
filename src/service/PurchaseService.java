package service;

import vo.PurchaseVO;

import java.util.List;

public interface PurchaseService {
    List<Long> integrateShopPurchases(String date, List<Integer> shopIdxes);
    int updatePurchaseToConfirmed(List<Long> selectedPurchaseSeq);
    String processPurchaseToCancelOrReturn(Long purchaseSeq);
    List<PurchaseVO> readAllPurchases();
    List<Long> integrateShopClaims(String date, List<Integer> shopIdxes);
    List<String> getShoppingmallList();
    List<PurchaseVO> getPurchaseListByPurchaseSeq(List<Long> shopPurchaseSeqList);
    List<PurchaseVO> getPurchaseClaimListByPurchaseSeq(List<Long> claimSeqList);
    List<Long> updateNewPurchaseStatus(List<Long> shopPurchaseSeqList);
//    String processPurchaseToCancel(Long purchaseSeq);
    int createPurchaseReturn(Long purchaseSeq);
    void updatePurchaseToCancel(Long purchaseSeq, String status);
    int updatePurchaseStatusToReturnComplete(Long purchaseSeq);
}
