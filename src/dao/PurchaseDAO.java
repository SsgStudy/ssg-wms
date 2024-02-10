package dao;


import util.enumcollect.PurchaseEnum;
import vo.PurchaseVO;

import java.util.List;

public interface PurchaseDAO {
    List<PurchaseVO> findByDateAndShop(String startDate, String endDate, List<String> shopName);
    int updatePurchaseStatus(List<Long> purchaseDetailSeq, PurchaseEnum purchaseStatus);
    List<String> findShopName();
    List<PurchaseVO> findAll();

}
