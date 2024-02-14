package controller;

import service.IncomigService;
import service.InvoiceService;
import service.InvoiceServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Logger;

public class InvoiceController {

    private static InvoiceController instance;

    static InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

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
        System.out.println("1.송장 등록 | 2.송장 조회 | 3.메뉴 나가기");
        System.out.print("메뉴 선택 : ");
        Long test=0L;
        try {
            int cmd = Integer.parseInt(br.readLine().trim());
            switch (cmd) {
                case 1 -> invoiceService.registerInvoice(test);
                case 2 -> invoiceService.viewInvoice();
                case 3 -> {return;}
            }
        }catch (NumberFormatException e){
            logger.info("숫자로 입력하세요.");
            e.printStackTrace();
        }
    }
}
