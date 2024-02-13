package vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DetailedIncomingVO {
    private long incomingProductSeq;
    private String incomingProductStatus;
    private LocalDateTime incomingProductDate;
    private String incomingProductType;
    private String incomingProductSupplierName;
    private int incomingProductCount;
    private int incomingProductPrice;
    private String productCode;
    private String warehouseCode;
    private String zoneCode;
    private String productName;
    private int productPrice;
    private String productBrand;
    private String productOrigin;
    private String productManufactor;
    private String productStatus;
    private LocalDateTime productManufactorDate;
    private String categoryCode;
}
