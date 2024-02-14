package service;

import vo.Invoice;
import vo.OutgoingProductVO;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

public interface InvoiceService {
    Long registerInvoice(Invoice invoice);
    List<Invoice> viewInvoice();
    Invoice getInvoiceRowByInvoiceSeq(Long pkInvoiceSeq);

    Blob createQRCode(Invoice invoice, OutgoingProductVO outgoingProduct) throws IOException, SQLException;

    int putQRCode(Blob qrcode, Long invoiceSeq);
}
