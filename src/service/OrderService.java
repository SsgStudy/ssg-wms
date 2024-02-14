package service;

import java.util.List;
import vo.OrderVO;
import vo.Product;

public interface OrderService {
    List<OrderVO> getAllOrdersWithDetails();
    List<OrderVO> getAllOrdersStatusProgress();
    List<Product> getProductInventoryList();
    Long registerOrder(String date, Product product);
    OrderVO getOneOrderInformation(Long orderSeq);
    int updateOrderStauts(Long orderSeq);

}
