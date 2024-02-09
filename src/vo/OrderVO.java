package vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderVO {
    private long orderSeq;
    private String orderStatus;
    private String incomingProductSupplierName;
    private LocalDateTime deliveryDate;
    private LocalDateTime orderCompletionDate;
    private long orderDetailSeq; // 주문 상세 정보의 기본 키
    private int orderCount; // 주문 수량
    private String productCode; // 상품 코드
    private String warehouseCode; // 창고 코드
}
