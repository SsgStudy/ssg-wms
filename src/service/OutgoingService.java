package service;

import dao.OutgoingDAO;
import dao.OutgoingDAOImpl;
import java.time.LocalDateTime;
import java.util.List;
import vo.InventoryVO;
import vo.OutgoingInstVO;
import vo.OutgoingVO;

public interface OutgoingService {

    //출고 지시 전체 조회
    List<OutgoingInstVO> getAllOutgoingInsts();

    //출고 등록
    void addOutgoingProduct(int shopSeq) throws Exception;

    //출고 등록 수정 및 승인
    void updateOutgoingProductStatusAndDate(Long pkOutgoingId, LocalDateTime date, String status) throws Exception;

    //출고 지시 수량 조회
    int getOutgoingProductQuantity(Long pkOutgoingId) throws Exception;

    //창고 및 출고 수량 조회
    List<InventoryVO> getInventoryByProductCodeAndQuantity(String productCd, int quantity) throws Exception;

    void updateOutgoingProduct(Long pkOutgoingId, int newQuantity, String warehouseCd, String zoneCd) throws Exception;

    String getProductCodeByOutgoingId(Long pkOutgoingId) throws Exception;

}
