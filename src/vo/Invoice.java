package vo;

import util.enumcollect.WaybillTypeEnum;

import java.sql.Blob;
import java.util.Date;

public class Invoice {
    private String invoiceCode;
    private Date invoicePrintDate;
    private String invoiceType;
    private Blob qrCode;
    private int logisticCode;
    private int purchaseCode;
    private String customerName;
    private String customerAddress;
    private String productName;
    private int quantityOrdered;

    public int getQuantityOrdered() {

        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    private String senderName = "SSGBACK-INTERNATIONAL";

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

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

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
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
