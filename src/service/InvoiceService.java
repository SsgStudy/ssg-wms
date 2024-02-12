package service;

import java.io.IOException;
import java.sql.SQLException;

public interface InvoiceService {
    void registerInvoice() throws IOException, SQLException;
    void viewInvoice() throws IOException, SQLException;

}
