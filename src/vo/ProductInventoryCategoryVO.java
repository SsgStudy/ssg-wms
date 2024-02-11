package vo;

import lombok.Data;

@Data
public class ProductInventoryCategoryVO {

    private String productCode;
    private String productName;
    private int productPrice;
    private int totalInventoryCnt; //재고수량
    private String categoryCode; //카테고리
}
