package service;

import vo.Invoice;
import vo.OutgoingProductVO;

import java.io.IOException;
import java.sql.SQLException;

public interface InvoiceService {
    int registerInvoice(OutgoingProductVO outgoingProductVO) throws IOException, SQLException;
    void viewInvoice() throws IOException, SQLException;
    Invoice getInvoiceRowByInvoiceSeq(Long pkInvoiceSeq);
}
