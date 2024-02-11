package dao;

import vo.Invoice;

public interface InvoiceDao {
    void registerInvoice(Invoice invoice);
    void viewInvoice();
}
