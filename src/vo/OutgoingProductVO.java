package vo;

import lombok.Data;
import util.enumcollect.SalesStatus;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OutgoingProductVO {
    private Long outgoingId;
    private LocalDateTime outgoingDate;
    private Long shopPurchaseSeq;

//    private String productName;
//    private String productBrand;
//    private Integer outgoingCnt;
    private String purchaseName;
    private String purchaseAddr;
    private String purchaseTel;

    private List<Product> products;

}
