package dao;

import vo.Invoice;

import java.sql.Blob;
import java.util.List;

public interface InvoiceDao {
    Long registerInvoice(Invoice invoice);
    List<Invoice> viewInvoice();
    Invoice getInvoiceRowByInvoiceSeq(Long pkInvoiceSeq);
    int putQRCode(Blob qrcode, Long pkInvoiceSeq);
}
