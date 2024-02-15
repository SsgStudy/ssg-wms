package service;

import vo.Invoice;
import vo.OutgoingProductVO;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

/**
 * The interface Invoice service.
 */
public interface InvoiceService {

    /**
     * Register invoice long.
     *
     * @param invoice the invoice
     * @return the long
     */
    Long registerInvoice(Invoice invoice);

    /**
     * View invoice list.
     *
     * @return the list
     */
    List<Invoice> viewInvoice();

    /**
     * Gets invoice row by invoice seq.
     *
     * @param pkInvoiceSeq the pk invoice seq
     * @return the invoice row by invoice seq
     */
    Invoice getInvoiceRowByInvoiceSeq(Long pkInvoiceSeq);

    /**
     * Create qr code blob.
     *
     * @param invoice         the invoice
     * @param outgoingProduct the outgoing product
     * @return the blob
     * @throws IOException  the io exception
     * @throws SQLException the sql exception
     */
    Blob createQRCode(Invoice invoice, OutgoingProductVO outgoingProduct) throws IOException, SQLException;

    /**
     * Put qr code int.
     *
     * @param qrcode     the qrcode
     * @param invoiceSeq the invoice seq
     * @return the int
     */
    int putQRCode(Blob qrcode, Long invoiceSeq);
}
