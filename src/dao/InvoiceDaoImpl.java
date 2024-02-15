package dao;

import util.DbConnection;
import util.enumcollect.WaybillTypeEnum;
import vo.Invoice;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class InvoiceDaoImpl implements InvoiceDao {

    private static InvoiceDaoImpl instance;

    public InvoiceDaoImpl() {
    }

    public static synchronized InvoiceDaoImpl getInstance() {
        if (instance == null) {
            instance = new InvoiceDaoImpl();
        }
        return instance;
    }

    @Override
    public Long registerInvoice(Invoice invoice) {//송장코드, 송장출력날짜, 송장종류, qr이미지파일, 택배사코드, 주문번호
        int row=0;
        Long invoiceSeq = 0L;

        try {
            Connection conn = DbConnection.getInstance().getConnection();

            String sql = "INSERT INTO `TB_INVOICE` (V_INVOICE_TYPE, B_INVOICE_QR_CD, PK_LOGISTIC_SEQ, PK_SHOP_PURCHASE_SEQ) VALUES "
                    + "(?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, String.valueOf(invoice.getInvoiceType()));
            pstmt.setBlob(2, invoice.getQrCode());
            pstmt.setLong(3, invoice.getLogisticSeq());
            pstmt.setLong(4, invoice.getPurchaseSeq());

            row = pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            invoiceSeq = null;
            if (generatedKeys.next()) {
                invoiceSeq = generatedKeys.getLong(1);
            }

            int status = putInvoiceCode(invoiceSeq);

            if (row != 0) {
                System.out.println("[송장 등록 완료]");
            }else{
                System.out.println("[다시 시도하세요]");
            }
            pstmt.close();
            DbConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return invoiceSeq;
    }

    @Override
    public List<Invoice> viewInvoice() {
        List<Invoice> invoices = new ArrayList<>();
        ResultSet rs;

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder().append("SELECT V_INVOICE_CD, DATE_FORMAT(DT_INVOICE_PRINT_DATE, '%Y-%m-%d'), V_INVOICE_TYPE, B_INVOICE_QR_CD, PK_LOGISTIC_SEQ, PK_SHOP_PURCHASE_SEQ FROM TB_INVOICE").toString();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceCode(rs.getString("V_INVOICE_CD"));
                invoice.setInvoiceType(WaybillTypeEnum.valueOf(rs.getString("V_INVOICE_TYPE")));
                invoice.setInvoicePrintDate(rs.getDate("DATE_FORMAT(DT_INVOICE_PRINT_DATE, '%Y-%m-%d')"));
                invoice.setQrCode(rs.getBlob("B_INVOICE_QR_CD"));
                invoice.setLogisticSeq(rs.getLong("PK_LOGISTIC_SEQ"));
                invoice.setPurchaseSeq(rs.getLong("PK_SHOP_PURCHASE_SEQ"));
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

    @Override
    public Invoice getInvoiceRowByInvoiceSeq(Long pkInvoiceSeq) {
        Invoice invoice = new Invoice();
        ResultSet rs;

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder("SELECT V_INVOICE_CD, DATE_FORMAT(DT_INVOICE_PRINT_DATE, '%Y-%m-%d')")
                    .append("FROM TB_INVOICE").toString();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                invoice.setInvoiceCode(rs.getString("V_INVOICE_CD"));
                invoice.setInvoicePrintDate(rs.getDate("DATE_FORMAT(DT_INVOICE_PRINT_DATE, '%Y-%m-%d')"));
            }

            rs.close();
            pstmt.close();

            DbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return invoice;
    }

    @Override
    public int putQRCode(Blob qrcode, Long invoiceSeq) {
        int status = 0;

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder("UPDATE TB_INVOICE ")
                    .append("SET B_INVOICE_QR_CD = ? ")
                    .append("WHERE PK_INVOICE_SEQ = ? ")
                    .toString();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBlob(1, qrcode);
            pstmt.setLong(2, invoiceSeq);
            status = pstmt.executeUpdate();

            pstmt.close();
            DbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    public int putInvoiceCode(Long invoiceSeq) {
        int status = 0;

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            String sql = new StringBuilder("UPDATE TB_INVOICE ")
                    .append("SET V_INVOICE_CD = CONCAT('INV', LPAD(PK_INVOICE_SEQ, 8, '0'))")
                    .append("WHERE PK_INVOICE_SEQ = ? ")
                    .toString();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, invoiceSeq);

            status =  pstmt.executeUpdate();

            pstmt.close();
            DbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

}
