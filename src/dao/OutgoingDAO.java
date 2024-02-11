package dao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import vo.IncomingVO;
import vo.InventoryVO;
import vo.OutgoingInstVO;
import vo.OutgoingVO;

public interface OutgoingDAO {

    List<OutgoingInstVO> getAllOutgoingInsts();

    void insertOutgoingProduct(OutgoingVO outgoingVO) throws Exception;

    void processOutgoingProducts(int shopSeq) throws Exception;

    void updateOutgoingProductStatusAndDate(Long pkOutgoingId, LocalDateTime date, String status) throws Exception;

    int getOutgoingProductQuantity(Long pkOutgoingId) throws Exception;

    List<InventoryVO> getInventoryByProductCodeAndQuantity(String productCd, int quantity) throws Exception;

    void updateOutgoingProduct(Long pkOutgoingId, int newQuantity, String warehouseCd, String zoneCd) throws Exception;
}
