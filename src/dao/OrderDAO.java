package dao;

import java.util.List;

import vo.OrderDetailVO;
import vo.OrderVO;
import vo.Product;

public interface OrderDAO {
    List<OrderVO> getAllOrdersWithDetails();
    Long registerOrder(String date, Product product);
    List<Product> getAllProductQuantity();
    OrderVO getOneOrderInformation(Long orderSeq);


}
