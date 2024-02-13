package vo;

import lombok.Data;

@Data
public class InventoryVO {
    private Integer inventorySeq;
    private String inventorySlipDate;
    private String warehouseCd;
    private String zoneCd;
    private Integer inventoryCnt;
    private String productCd;
}
