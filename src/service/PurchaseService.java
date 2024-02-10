package service;

import java.util.List;

public interface PurchaseService {
    public void integrateShopPurchases(String startDate,
                                       String endDate, List<String> shopName);

    public void updateOrderToConfirmed();
}
