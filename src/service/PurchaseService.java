package service;

import vo.PurchaseVO;

import java.util.List;

public interface PurchaseService {
    void integrateShopPurchases(String startDate, String endDate, List<String> shopName);
    void updateOrderToConfirmed();
    void readAllPurchases();

}
