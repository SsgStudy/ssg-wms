package dao;

import java.util.List;
import vo.OrderVO;

public interface OrderDAO {
    List<OrderVO> getAllOrdersWithDetails();

}
