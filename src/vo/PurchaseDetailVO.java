package vo;

import lombok.Data;

@Data
public class PurchaseDetailVO {
    private long shopPurchaseDetailSeq;
    private long shopPurchaseSeq;
    private int productCnt;
    private String productCd;
}
