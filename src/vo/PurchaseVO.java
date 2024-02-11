package vo;

import lombok.Data;

import java.sql.Date;

@Data
public class PurchaseVO {
    private long shopPurchaseSeq;
    private String shopCd;
    private String shopName;
    private String shopPurchaseName;
    private String shopPurchaseAddress;
    private String shopPurchaseTel;
    private String shopPurchaseStatus;
    private Date shopPurchaseDate;
    private long shopPurchaseDetailSeq;
    private int productCnt;
    private String productName;
    private String productBrand;
    private int productPrice;
}