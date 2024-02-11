package vo;

import lombok.Data;

@Data
public class ProductInventoryWarehouseVO {
    private String productCode;
    private String productName;
    private int inventoryCnt; //재고수량
    private String warehouseCode; //창고코드
}
