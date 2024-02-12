package vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OutgoingVO {
    private Long outgoingId;
    private String outgoingStatus;
    private LocalDateTime outgoingDate;
    private Integer outgoingCnt;
    private Long shopPurchaseDetailSeq;
    private Long shopPurchaseSeq;
    private String productCd;
    private String zoneCd;
    private String warehouseCd;

}
