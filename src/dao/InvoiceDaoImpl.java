package dao;

import service.InvoiceServiceImpl;
import util.DbConnection;
import vo.Invoice;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class InvoiceDaoImpl implements InvoiceDao {
    Connection conn = null;

    @Override
    public void registerInvoice(Invoice invoice) {//송장코드, 송장출력날짜, 송장종류, qr이미지파일, 택배사코드, 발주코드
        InvoiceServiceImpl invoiceService = new InvoiceServiceImpl();
        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = "INSERT INTO `TB_INVOICE` (V_INVOICE_CD, DT_INVOICE_PRINT_DATE, V_INVOICE_TYPE, B_INVOICE_QR_CD, PK_LOGISTIC_SEQ, PK_SHOP_PURCHASE_SEQ) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, invoice.getInvoiceCode());
            pstmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setString(3, String.valueOf(invoice.getInvoiceType()));
            pstmt.setBlob(4, invoice.getQrCode());
            pstmt.setInt(5, invoice.getLogisticCode());
            pstmt.setInt(6, invoice.getPurchaseCode());
            int row = pstmt.executeUpdate();
            if (row != 0) {
                System.out.println("[송장 등록 완료]");
            }else{
                System.out.println("[다시 시도하세요]");
                invoiceService.invoiceMain();
            }
            pstmt.close();
            DbConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Invoice> viewInvoice() {
        List<Invoice> invoices = new ArrayList<>();
        try {
            conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder().append("SELECT V_INVOICE_CD, DATE_FORMAT(DT_INVOICE_PRINT_DATE, '%Y-%m-%d'), V_INVOICE_TYPE, B_INVOICE_QR_CD, PK_LOGISTIC_SEQ, PK_SHOP_PURCHASE_SEQ FROM TB_INVOICE").toString();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceCode(rs.getString("V_INVOICE_CD"));
                invoice.setInvoicePrintDate(rs.getDate("DATE_FORMAT(DT_INVOICE_PRINT_DATE, '%Y-%m-%d')"));
                invoice.setInvoiceType(rs.getString("V_INVOICE_TYPE"));
                invoice.setQrCode(rs.getBlob("B_INVOICE_QR_CD"));
                invoice.setLogisticCode(rs.getInt("PK_LOGISTIC_SEQ"));
                invoice.setPurchaseCode(rs.getInt("PK_SHOP_PURCHASE_SEQ"));
                invoices.add(invoice);
            }

            rs.close();
            pstmt.close();

            DbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return invoices;
    }
}
