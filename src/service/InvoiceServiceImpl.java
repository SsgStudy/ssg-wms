package service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import dao.InvoiceDaoImpl;
import util.enumcollect.WaybillTypeEnum;
import vo.Invoice;
import vo.OutgoingProductVO;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

/**
 * The type Invoice service.
 */
public class InvoiceServiceImpl implements InvoiceService {
    private static Logger logger = Logger.getLogger(InvoiceServiceImpl.class.getName());
    private InvoiceDaoImpl invoiceDAO;
    private static InvoiceServiceImpl instance;
    List<Invoice> invoiceList = new ArrayList<>();

    InvoiceServiceImpl(){};

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized InvoiceServiceImpl getInstance() {
        if (instance == null) {
            instance = new InvoiceServiceImpl();
        }
        return instance;
    }

    /**
     * Instantiates a new Invoice service.
     *
     * @param invoiceDAO the invoice dao
     */
    public InvoiceServiceImpl(InvoiceDaoImpl invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }


    @Override
    public Long registerInvoice(Invoice invoice) {
        return invoiceDAO.registerInvoice(invoice);
    }

    public List<Invoice> viewInvoice() {
        return invoiceDAO.viewInvoice();
    }

    public int putQRCode(Blob qrcode, Long invoiceSeq) {
        return invoiceDAO.putQRCode(qrcode, invoiceSeq);
    }


    @Override
    public Invoice getInvoiceRowByInvoiceSeq(Long pkInvoiceSeq) {
        return invoiceDAO.getInvoiceRowByInvoiceSeq(pkInvoiceSeq);
    }

    /**
     * Creates a QR Code image based on invoice information and outgoing product details.
     *
     * @param invoice         the invoice
     * @param outgoingProduct the outgoing product
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public Blob createQRCode(Invoice invoice, OutgoingProductVO outgoingProduct) throws IOException, SQLException {
        String text = new StringBuilder("Invoice Code: " + invoice.getInvoiceCode())
                .append("\nInvoice Type: " + invoice.getInvoiceType())
                .append("\nPurchase Code: " + invoice.getPurchaseSeq())
                .append("\nCustomer Name : " + outgoingProduct.getPurchaseName())
                .append("\nCustomer Tel : " + outgoingProduct.getPurchaseTel())
                .append("\nCustomer Address : " + outgoingProduct.getPurchaseAddr()).toString();
        int width = 300;
        int height = 300;
        try {
            Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE); // QR 코드의 배경색
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK); // QR 코드의 색상
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            File qrFile = new File(invoice.getInvoiceCode() + ".png");
            ImageIO.write(image, "png", qrFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            Blob blob = new SerialBlob(imageBytes);
            return new javax.sql.rowset.serial.SerialBlob(imageBytes);
        } catch (WriterException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating QR Code", e);
        }
    }
}
