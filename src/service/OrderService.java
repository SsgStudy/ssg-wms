package service;

import java.util.List;

import vo.OrderVO;
import vo.Product;

/**
 * The interface Order service.
 */
public interface OrderService {

    /**
     * Gets all orders with details.
     *
     * @return the all orders with details
     */
    List<OrderVO> getAllOrdersWithDetails();

    /**
     * Gets all orders status progress.
     *
     * @return the all orders status progress
     */
    List<OrderVO> getAllOrdersStatusProgress();

    /**
     * Gets product inventory list.
     *
     * @return the product inventory list
     */
    List<Product> getProductInventoryList();

    /**
     * Register order long.
     *
     * @param date    the date
     * @param product the product
     * @return the long
     */
    Long registerOrder(String date, Product product);

    /**
     * Gets one order information.
     *
     * @param orderSeq the order seq
     * @return the one order information
     */
    OrderVO getOneOrderInformation(Long orderSeq);

    /**
     * Update order stauts int.
     *
     * @param orderSeq the order seq
     * @return the int
     */
    int updateOrderStauts(Long orderSeq);
}
