package vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class IncomingVO {
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
}
