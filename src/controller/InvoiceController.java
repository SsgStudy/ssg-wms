package controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import dao.LoginManagementDAOImpl;
import service.IncomigService;
import service.InvoiceService;
import service.InvoiceServiceImpl;
import util.MenuBoxPrinter;
import util.enumcollect.MemberEnum;
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
 * 이 클래스는 송장 관리를 담당하는 InvoiceController 컨트롤러입니다.
 * 주로 송장 등록, 조회와 같은 기능을 수행합니다.
 * 해당 클래스는 Singleton 패턴을 따르며, 하나의 인스턴스만을 생성하여 사용합니다.
 *
 * @author : 윤여빈
 */
public class InvoiceController {
    private LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
    private MemberEnum loginMemberRole;
    private String loginMemberId;
    private static InvoiceController instance;
    static InvoiceService invoiceService;
    static boolean isRunning = true;
    static int cmd;

    /**
     * Instantiates a new Invoice controller.
     *
     * @param invoiceService the invoice service
     */
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
    }
    List<Invoice> invoiceList = new ArrayList<>();

    /**
     * Gets instance.
     *
     * @param invoiceService the invoice service
     * @return the instance
     */
    public static InvoiceController getInstance(InvoiceService invoiceService) {
        if (instance == null) {
            instance = new InvoiceController(invoiceService);
        }
        return instance;
    }

    private static Logger logger = Logger.getLogger(InvoiceController.class.getName());

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Menu.
     *
     * @throws IOException  the io exception
     * @throws SQLException the sql exception
     */
    public static void menu() throws IOException, SQLException {
        System.out.println("[송장 관리]");
        System.out.println("--".repeat(25));
        System.out.println("1.송장 조회 | 2.메뉴 나가기");
        System.out.print("메뉴 선택 : ");

        cmd = Integer.parseInt(br.readLine().trim());

        while (isRunning) {
            try {
                switch (cmd) {
                    case 1 -> instance.viewInvoice();
                    case 2 -> {
                        isRunning = false;
                    }
                }
            } catch (NumberFormatException e) {
                logger.info("숫자로 입력하세요.");
                e.printStackTrace();
            }
        }
    }

    /**
     * View invoice.
     */
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

        {
            String[] menuItems1 = {
                    "1. 송장 다시 조회하기\t\t\t\t",
                    "2. 메뉴 나가기\t\t\t\t",
            };

            MenuBoxPrinter.printMenuBoxWithTitle("송장 조회\t\t\t\t\t", menuItems1);

            try {
                int cmd = Integer.parseInt(br.readLine().trim());

                switch (cmd) {
                    case 1 -> System.out.println("다시 조회 중...");
                    case 2 -> isRunning = false;
                }
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }
}
