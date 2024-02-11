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
import javax.sql.rowset.serial.SerialException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Hashtable;

public class InvoiceServiceImpl implements InvoiceService {
    InvoiceDaoImpl invoiceDao = new InvoiceDaoImpl();
    BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
    Invoice invoice = new Invoice();

    public void invoiceMain() throws IOException {
        System.out.println("[송장 관리]");
        System.out.println("--".repeat(25));
        System.out.println("1.송장 등록 | 2.송장 조회");
        System.out.print("메뉴 선택 : ");
        int cmd = Integer.parseInt(sc.readLine().trim());
        switch (cmd) {
            case 1 -> registerInvoice();
            case 2 -> viewInvoice();
        }
    }

    @Override
    public void registerInvoice() throws IOException {
        Invoice invoice = new Invoice();
        System.out.println("[송장 등록]");
        System.out.println("--".repeat(25));
        System.out.print("송장 코드 입력 : ");
        invoice.setInvoiceCode(sc.readLine());
        System.out.print("송장 종류 입력 : ");
        invoice.setInvoiceType(WaybillTypeEnum.valueOf(sc.readLine()));
        System.out.print("주문 번호 입력 : ");
        //주문 정보를 받아올 수 있는 부분 작성(아직...이지만 일단 뭐라도 넣어서 기능구현 해보기)
        invoice.setPurchaseCode(Integer.parseInt(sc.readLine().trim()));
        Blob qrCodeImage = createQRCode(invoice.getInvoiceCode(), String.valueOf(invoice.getInvoiceType()), invoice.getPurchaseCode());
        invoice.setQrCode(qrCodeImage);
        System.out.println("[택배사 선택]");
        System.out.println("--".repeat(25));
        System.out.println("1.한진택배 | 2.CJ대한통운 | 3.우체국택배 | 4.롯데택배 | 5.로젠택배");
        System.out.print("택배사 선택 : ");
        invoice.setLogisticCode(Integer.parseInt(sc.readLine().trim()));
        invoiceDao.registerInvoice(invoice);
        invoiceMain();


    }

    public Blob createQRCode(String invoiceCode, String invoiceType, int purchaseCode) {

        // QR 코드로 포함될 전체 텍스트
        String text = "Invoice Code : " + invoiceCode + "\n" +
                "Invoice Type : " + invoiceType + "\n" +
                "Purchase Code : " + purchaseCode;

        // QR 코드 이미지 생성
        int width = 300; // QR 코드의 너비
        int height = 300; // QR 코드의 높이
//        Blob qrCodeImage = createQRCode(invoiceCode, invoiceType, purchaseCode);
//        String filePath = "invoice-qr-code6.png"; // QR코드 이미지 파일 경로

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
    public void viewInvoice() {

    }
}
