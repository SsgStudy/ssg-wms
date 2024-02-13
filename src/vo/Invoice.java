package vo;

import lombok.Data;
import util.enumcollect.WaybillTypeEnum;

import java.sql.Blob;
import java.util.Date;

@Data
public class Invoice {
    private String invoiceCode;
    private Date invoicePrintDate;
    private WaybillTypeEnum invoiceType;
    private Blob qrCode;
    private Long logisticSeq;
    private Long purchaseSeq;
    private String customerName;
    private String customerAddress;
    private String productName;
    private int quantityOrdered;
}
