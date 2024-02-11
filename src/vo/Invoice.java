package vo;

import util.enumcollect.WaybillTypeEnum;

import java.sql.Blob;
import java.util.Date;

public class Invoice {
    private String invoiceCode;
    private Date invoicePrintDate;
    private WaybillTypeEnum invoiceType;
    private Blob qrCode;
    private int logisticCode;
    private int purchaseCode;

    public int getPurchaseCode() {
        return purchaseCode;
    }

    public void setPurchaseCode(int purchaseCode) {
        this.purchaseCode = purchaseCode;
    }

    public Blob getQrCode() {
        return qrCode;
    }

    public void setQrCode(Blob qrCode) {
        this.qrCode = qrCode;
    }


    public int getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(int logisticCode) {
        this.logisticCode = logisticCode;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public java.sql.Date getInvoicePrintDate() {
        return (java.sql.Date) invoicePrintDate;
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
