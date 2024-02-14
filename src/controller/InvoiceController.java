package controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import service.IncomigService;
import service.InvoiceService;
import service.InvoiceServiceImpl;
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

public class InvoiceController {
    private static InvoiceController instance;
    static InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    List<Invoice> invoiceList = new ArrayList<>();

    public static InvoiceController getInstance(InvoiceService invoiceService) {
        if (instance == null) {
            instance = new InvoiceController(invoiceService);
        }
        return instance;
    }

    private static Logger logger = Logger.getLogger(InvoiceController.class.getName());
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void menu() throws IOException, SQLException {
        System.out.println("[송장 관리]");
        System.out.println("--".repeat(25));
        System.out.println("1.송장 조회 | 2.메뉴 나가기");
        System.out.print("메뉴 선택 : ");

        try {
            int cmd = Integer.parseInt(br.readLine().trim());
            switch (cmd) {
                case 1 -> invoiceService.viewInvoice();
                case 2 -> {return;}
            }
        }catch (NumberFormatException e){
            logger.info("숫자로 입력하세요.");
            e.printStackTrace();
        }
    }

    public void viewInvoice() {
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("[송장 목록]");
        System.out.println("--".repeat(25));
        System.out.printf("%4s | %8s | %5s | %20s \t| %4s | %4s\n", "송장코드", "송장날짜", "송장종류", "QR코드", "택배사코드", "발주코드");
        System.out.println("--".repeat(25));
        invoiceList = invoiceService.viewInvoice();
        for (Invoice invoice : invoiceList) {
            System.out.printf("%7s | %10s | %7s | %20s |%4s | %4s\n",
                    invoice.getInvoiceCode(),
                    invoice.getInvoicePrintDate(),
                    invoice.getInvoiceType(),
                    invoice.getQrCode(),
                    invoice.getLogisticSeq(),
                    invoice.getPurchaseSeq());
        }
    }

}
