package serviceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import daoImpl.InvoiceDaoImpl;
import service.InvoiceService;
import util.enumcollect.WaybillTypeEnum;
import vo.Invoice;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

public class InvoiceServiceImpl implements InvoiceService {
    private static Logger logger = Logger.getLogger(InvoiceServiceImpl.class.getName());
    InvoiceDaoImpl invoiceDao = new InvoiceDaoImpl();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    List<Invoice> invoiceList = new ArrayList<>();

    public void invoiceMain() throws IOException, SQLException {
        System.out.println("[송장 관리]");
        System.out.println("--".repeat(25));
        System.out.println("1.송장 등록 | 2.송장 조회");
        System.out.print("메뉴 선택 : ");
        try {
            int cmd = Integer.parseInt(br.readLine().trim());
            switch (cmd) {
                case 1 -> registerInvoice(1L);
                case 2 -> viewInvoice();
            }
        }catch (NumberFormatException e){
            logger.info("숫자로 입력하세요.");
            e.printStackTrace();
            invoiceMain();
        }
    }

    @Override
    public void registerInvoice(Long seq) throws IOException, SQLException {
        Invoice invoice = new Invoice();

        System.out.println("[송장 출력]");
        System.out.println("--".repeat(25));

        /* 송장 종류 선택 */
        // 송장 종류 출력 dao
        System.out.print("송장 종류 입력 (1.일반 | 2.특급 | 3.국제 | 4.등기) ");
        int ch = Integer.parseInt(br.readLine());

        switch (ch) {
            case 1 -> invoice.setInvoiceType(WaybillTypeEnum.STANDARD);
            case 2 -> invoice.setInvoiceType(WaybillTypeEnum.EXPRESS);
            case 3 -> invoice.setInvoiceType(WaybillTypeEnum.INTERNATIONAL);
            case 4 -> invoice.setInvoiceType(WaybillTypeEnum.REGISTERED);
        }

        invoice.setPurchaseSeq(seq);


        try {
            System.out.println("[택배사 선택]");
            System.out.println("--".repeat(25));
            System.out.println("1. 한진택배 | 2.CJ대한통운 | 3.우체국택배 | 4.롯데택배 | 5.로젠택배");
            System.out.print("택배사 선택 : ");
            invoice.setLogisticSeq(Long.parseLong(br.readLine()));
            Blob qrCodeImage = createQRCode2(invoice.getInvoiceCode(), String.valueOf(invoice.getInvoiceType()), invoice.getPurchaseSeq());
            invoice.setQrCode(qrCodeImage);

            invoiceDao.registerInvoice(invoice);
        } catch (NumberFormatException e){
            logger.info("숫자로 입력하세요.");
            e.printStackTrace();
//            registerInvoice();
        }



        invoiceMain();
    }

    public Blob createQRCode(String invoiceCode, WaybillTypeEnum invoiceType, int purchaseCode) {

        // QR 코드로 포함될 전체 텍스트
        String text = "Invoice Code : " + invoiceCode + "\n" +
                "Invoice Type : " + invoiceType + "\n" +
                "Purchase Code : " + purchaseCode;

        // QR 코드 이미지 생성
        int width = 300; // QR 코드의 너비
        int height = 300; // QR 코드의 높이

        try {
            Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            graphics.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // 이미지 바이트 배열을 Blob으로 변환하여 반환
            return new javax.sql.rowset.serial.SerialBlob(imageBytes);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        } catch (SerialException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void viewInvoice() throws IOException, SQLException {
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("[송장 목록]");
        System.out.println("--".repeat(25));
        System.out.printf("%4s | %8s | %5s | %20s \t| %4s | %4s\n", "송장코드", "송장날짜", "송장종류", "QR코드", "택배사코드", "발주코드");
        System.out.println("--".repeat(25));
        invoiceList = invoiceDao.viewInvoice();
        for (Invoice invoice : invoiceList) {
            System.out.printf("%7s | %10s | %7s | %20s |%4s | %4s\n",
                    invoice.getInvoiceCode(),
                    invoice.getInvoicePrintDate(),
                    invoice.getInvoiceType(),
                    invoice.getQrCode(),
                    invoice.getLogisticSeq(),
                    invoice.getPurchaseSeq());
        }
        invoiceMain();
    }

    public Blob createQRCode2(String invoiceCode, String invoiceType, Long purchaseSeq) throws IOException, SQLException {
        String text = "Invoice Code: " + invoiceCode + "\nInvoice Type: " + invoiceType + "\nPurchase Code: " + purchaseSeq;
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
            // 이미지 파일로 저장
            File qrFile = new File(invoiceCode + ".png");
            ImageIO.write(image, "png", qrFile);
            // 파일을 Blob으로 변환하여 반환
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