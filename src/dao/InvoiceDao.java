package dao;

import vo.Invoice;

import java.util.List;

public interface InvoiceDao {
    void registerInvoice(Invoice invoice);
    List<Invoice> viewInvoice();
}
