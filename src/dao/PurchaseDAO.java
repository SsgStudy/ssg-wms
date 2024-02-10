package dao;


import vo.PurchaseVO;

import java.util.List;

public interface PurchaseDAO {
    public List<PurchaseVO> findByDateAndShop(String startDate, String endDate, List<String> shopName);
    public void updatePurchaseStatus();
    public List<String> findShopName();
}
