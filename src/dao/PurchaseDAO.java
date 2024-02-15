package dao;


import util.enumcollect.PurchaseEnum;
import vo.PurchaseVO;

import java.util.List;

public interface PurchaseDAO {
    List<Long> getPurchaseByDateAndShopName(String startDate, String endDate, List<String> shopName);
    List<Long> getClaimByDateAndShopName(String startDate, String endDate, List<String> shopName);
    int updatePurchaseStatus(List<Long> purchaseDetailSeq, PurchaseEnum purchaseStatus);
    List<String> findShopName();
    List<PurchaseVO> findAll();
    int updatePurchaseStatusCancelOrReturn(List<Long> purchaseSeqList);
    String processPurchaseCancelOrReturn(Long purchaseSeq);
    Long createPurchaseReturn(Long purchaseSeq);
    int updatePurchaseStatusEx(Long purchaseSeq, PurchaseEnum purchaseStatus);


}
