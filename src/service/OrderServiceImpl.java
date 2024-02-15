package service;

import dao.OrderDAO;

import java.util.List;

import vo.OrderVO;
import vo.Product;

/**
 * The type Order service.
 */
public class OrderServiceImpl implements OrderService {
    private OrderDAO orderDAO;
    private static OrderServiceImpl instance;

    /**
     * Instantiates a new Order service.
     *
     * @param orderDAO the order dao
     */
    public OrderServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public List<OrderVO> getAllOrdersWithDetails() {
        return orderDAO.getAllOrdersWithDetails();
    }

    @Override
    public List<OrderVO> getAllOrdersStatusProgress() {
        List<OrderVO> orderList = orderDAO.getAllOrdersStatusProgress();
        printOrderProgressList(orderList);
        return orderList;
    }

    @Override
    public List<Product> getProductInventoryList() {
        List<Product> productList = orderDAO.getAllProductQuantity();
        printProductList(productList);

        return productList;
    }

    @Override
    public Long registerOrder(String date, Product product) {
        return orderDAO.registerOrder(date, product);
    }

    @Override
    public OrderVO getOneOrderInformation(Long orderSeq) {
        OrderVO order = orderDAO.getOneOrderInformation(orderSeq);
        printOrderInfo(order);
        return order;
    }

    @Override
    public int updateOrderStauts(Long orderSeq) {
        return orderDAO.updateOrderStatus(orderSeq);
    }

    /**
     * Print order info.
     *
     * @param order the order
     */
    public void printOrderInfo(OrderVO order) {
        System.out.printf("%-4s %-12s %-20s %-8s%n",
                "발주 번호", "발주 상태", "공급 업체", "납품 일자");

        System.out.printf("%-4d %-12s %-20s %tF %<tT%n \n",
                order.getOrderSeq(), order.getOrderStatus(), order.getIncomingProductSupplierName(),
                order.getDeliveryDate());
    }

    /**
     * Print order progress list.
     *
     * @param orderList the order list
     */
    public void printOrderProgressList(List<OrderVO> orderList) {
        System.out.printf("%-4s %-12s %-20s %-10s  \n",
                "발주 번호", "발주 상태", "공급 업체", "납품 일자");

        for (OrderVO order : orderList) {
            System.out.printf("%-4d %-12s %-20s %tF %<tT%n",
                    order.getOrderSeq(), order.getOrderStatus(), order.getIncomingProductSupplierName(),
                    order.getDeliveryDate());
        }
    }

    /**
     * Print product list.
     *
     * @param productList the product list
     */
    public void printProductList(List<Product> productList) {
        System.out.printf("%-4s %-12s %-20s %-8s %-15s %-15s %-15s%n",
                "번호", "상품 코드", "상품명", "상품 가격", "브랜드", "원산지", "제조사");

        int index = 1;
        for (Product product : productList) {
            System.out.printf("%-4d %-12s %-20s %-8d %-15s %-15s %-15s%n",
                    index++, product.getProductCode(), product.getProductName(), product.getProductPrice(),
                    product.getProductBrand(), product.getProductOrign(), product.getManufactor());
        }
    }
}
