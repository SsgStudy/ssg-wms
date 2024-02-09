package service;

import java.util.List;
import vo.OrderVO;

public interface OrderService {
    List<OrderVO> getAllOrdersWithDetails();
}
