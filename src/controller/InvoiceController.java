package controller;

import serviceImpl.InvoiceServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Logger;

public class InvoiceController {
    public static void main(String[] args) throws SQLException, IOException {
        invoiceMain();
    }
    static InvoiceServiceImpl invoiceService = new InvoiceServiceImpl();
    private static Logger logger = Logger.getLogger(InvoiceController.class.getName());
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void invoiceMain() throws IOException, SQLException {
        System.out.println("[송장 관리]");
        System.out.println("--".repeat(25));
        System.out.println("1.송장 등록 | 2.송장 조회");
        System.out.print("메뉴 선택 : ");
        try {
            int cmd = Integer.parseInt(br.readLine().trim());
            switch (cmd) {
                case 1 -> invoiceService.registerInvoice();
                case 2 -> invoiceService.viewInvoice();
            }
        }catch (NumberFormatException e){
            logger.info("숫자로 입력하세요.");
            e.printStackTrace();
            invoiceMain();
        }
    }
}
