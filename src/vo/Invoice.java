package vo;

import util.enumcollect.WaybillTypeEnum;

import java.sql.Blob;
import java.util.Date;


public class Invoice {
    private String invoiceCode;
    private Date invoicePrintDate;
    private WaybillTypeEnum invoiceType;
    private Blob qrCode;
    private Long logisticSeq;
    private Long purchaseSeq;


    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public Date getInvoicePrintDate() {
        return invoicePrintDate;
    }

    public void setInvoicePrintDate(Date invoicePrintDate) {
        this.invoicePrintDate = invoicePrintDate;
    }

    public WaybillTypeEnum getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(WaybillTypeEnum invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Blob getQrCode() {
        return qrCode;
    }

    public void setQrCode(Blob qrCode) {
        this.qrCode = qrCode;
    }

    public Long getLogisticSeq() {
        return logisticSeq;
    }

    public void setLogisticSeq(Long logisticSeq) {
        this.logisticSeq = logisticSeq;
    }

    public Long getPurchaseSeq() {
        return purchaseSeq;
    }

    public void setPurchaseSeq(Long purchaseSeq) {
        this.purchaseSeq = purchaseSeq;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceCode='" + invoiceCode + '\'' +
                ", invoicePrintDate=" + invoicePrintDate +
                ", invoiceType='" + invoiceType + '\'' +
                ", QRCode=" + qrCode +
                '}';
    }
}
