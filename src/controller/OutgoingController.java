package controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import service.InvoiceService;

import service.OutgoingService;
import util.enumcollect.WaybillTypeEnum;
import vo.*;

public class OutgoingController {
    private static Logger logger = Logger.getLogger(OutgoingController.class.getName());

    private static OutgoingController instance;
    private Scanner sc = new Scanner(System.in);
    private OutgoingService outgoingService;
    private InvoiceService invoiceService;

    public OutgoingController(OutgoingService outgoingService, InvoiceService invoiceService) {
        this.outgoingService = outgoingService;
        this.invoiceService = invoiceService;
    }

    public static OutgoingController getInstance(OutgoingService outgoingService, InvoiceService invoiceService) {
        if (instance == null) {
            instance = new OutgoingController(outgoingService, invoiceService);
        }
        return instance;
    }

    public void outgoingProductMenu() {
        boolean continueMenu = true;
        while (continueMenu) {
            System.out.println("\n1. 출고 지시 목록 조회 | 2. 출고 수정 및 승인 | 3. 메뉴 나가기");
            System.out.print("선택: ");
            int choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1 -> printAllOutgoingInsts();
                case 2 -> updateOutgoingProductMenu();
                case 3 -> {
                    return;
                }
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    public void outgoingProductSubMenu() {
        boolean continueMenu = true;
        while (continueMenu) {
            System.out.println("\n1. 출고 등록 | 2. 서브 메뉴 나가기");
            System.out.print("선택: ");
            int choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1 -> addOutgoingProductList();
                case 2 -> {return;}
                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void printAllOutgoingInsts() {
        List<OutgoingInstVO> outgoingInsts = outgoingService.getAllOutgoingInsts();
        if (outgoingInsts.isEmpty()) {
            System.out.println("출고 지시 목록이 비어있습니다.");
        } else {
            System.out.printf("\n%-20s %-50s\n", "구매 주문 번호", "출고 지시 상태");
            System.out.println("--------------------------------------------------");
            for (OutgoingInstVO inst : outgoingInsts) {
                System.out.printf("%-20d %-50s\n", inst.getShopPurchaseSeq(), inst.getOutgoingInstStatus());
            }
            System.out.println("--------------------------------------------------\n");
        }
        outgoingProductSubMenu();
    }

    public void addOutgoingProductList() {
        try {
            System.out.print("출고 등록할 지시 번호 선택: ");
            String input = sc.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
                outgoingService.addOutgoingProduct(choice);
            } catch (NumberFormatException e) {
                System.out.println("유효하지 않은 입력입니다. 숫자를 입력해주세요.");
            }
        } catch (Exception e) {
            System.out.println("출고 상품 추가 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateOutgoingProductMenu() {
        printAllOutgoings();
        try {
            System.out.print("수정할 출고 상품의 ID를 입력하세요: ");
            Long pkOutgoingId = Long.parseLong(sc.nextLine().trim());

            // 출고 상품에 대한 상품 코드 자동 조회
            String productCd = outgoingService.getProductCodeByOutgoingId(pkOutgoingId);

            // 출고 수량 조회
            int currentQuantity = outgoingService.getOutgoingProductQuantity(pkOutgoingId);
            System.out.println("현재 출고 수량: " + currentQuantity + ". 수정할 수량을 입력하세요 (최대 " + currentQuantity + "): ");
            int newQuantity = Integer.parseInt(sc.nextLine().trim());

            // 출고 일자 입력
            LocalDateTime outgoingDate = null;
            while (outgoingDate == null) {
                System.out.print("출고 일자를 입력하세요 (예: 202401301400): ");
                String outgoingDateInput = sc.nextLine(); // 문자열로 입력 받음
                try {
                    outgoingDate = LocalDateTime.parse(outgoingDateInput, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                } catch (DateTimeParseException e) {
                    System.out.println("잘못된 날짜 형식입니다. 'yyyyMMddHHmm' 형식으로 입력해주세요.");
                }
            }

            // 재고 정보 조회 및 선택
            List<InventoryVO> inventories = outgoingService.getInventoryByProductCodeAndQuantity(productCd, newQuantity);
            if (inventories.isEmpty()) {
                System.out.println("해당 상품에 대한 충분한 재고가 없습니다.");
                return;
            }

            System.out.println("선택 가능한 창고 및 구역 목록:");
            for (int i = 0; i < inventories.size(); i++) {
                InventoryVO inventory = inventories.get(i);
                System.out.println(
                        (i + 1) + ". 창고 코드: " + inventory.getWarehouseCd() + ", 구역 코드: " + inventory.getZoneCd() + ", 재고 수량: "
                                + inventory.getInventoryCnt());
            }
            System.out.print("선택: ");
            int inventoryChoice = Integer.parseInt(sc.nextLine().trim());
            InventoryVO selectedInventory = inventories.get(inventoryChoice - 1);

            // 출고 상품 업데이트 및 출고 상태 WAIT로 변경, 출고 일자 업데이트
            outgoingService.updateOutgoingProduct(pkOutgoingId, newQuantity, selectedInventory.getWarehouseCd(),
                    selectedInventory.getZoneCd());
            outgoingService.updateOutgoingProductStatusAndDate(pkOutgoingId, outgoingDate, "WAIT");

            // 송장 연결
            OutgoingProductVO outgoingProduct = outgoingService.getOutgoingProductRowByOutgoingId(pkOutgoingId);

            int result = promptInvoice(outgoingProduct);

            if (result>0) {
                System.out.println("출고 승인이 성공적으로 업데이트 되었습니다.\n 예정 출고 일자: " + outgoingDate.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } else {
                System.out.println("출고 실패 하였습니다. 다시 시도해주세요.");
            }

        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void printAllOutgoings() {
        try {
            List<OutgoingVO> outgoings = outgoingService.getAllOutgoings();
            if (outgoings.isEmpty()) {
                System.out.println("출고 현황 정보가 없습니다.");
            } else {
                System.out.printf("\n%-10s %-20s %-30s %-10s %-15s %-15s %-15s\n", "ID", "상태", "출고일", "수량", "상품코드", "창고코드", "구역코드");
                for (OutgoingVO outgoing : outgoings) {
                    String formattedDate = outgoing.getOutgoingDate() != null ?
                            outgoing.getOutgoingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";

                    System.out.printf("%-10d %-20s %-30s %-10d %-15s %-15s %-15s\n",
                            outgoing.getOutgoingId(), outgoing.getOutgoingStatus(),
                            formattedDate,
                            outgoing.getOutgoingCnt(), outgoing.getProductCd(),
                            outgoing.getWarehouseCd(), outgoing.getZoneCd());
                }

            }
        } catch (Exception e) {
            System.out.println("출고 현황 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int promptInvoice(OutgoingProductVO outgoingProductVO) {
        Invoice invoice = new Invoice();
        int status = 0;
        Long invoiceSeq = 0L;

        System.out.println("[송장 출력]");
        System.out.println("--".repeat(25));
        System.out.print("송장 종류 입력 (1.일반 | 2.특급 | 3.국제 | 4.등기) ");

        try{
            int ch = Integer.parseInt(sc.nextLine().trim());

            switch (ch) {
                case 1 -> invoice.setInvoiceType(WaybillTypeEnum.STANDARD);
                case 2 -> invoice.setInvoiceType(WaybillTypeEnum.EXPRESS);
                case 3 -> invoice.setInvoiceType(WaybillTypeEnum.INTERNATIONAL);
                case 4 -> invoice.setInvoiceType(WaybillTypeEnum.REGISTERED);
            }

            invoice.setPurchaseSeq(outgoingProductVO.getShopPurchaseSeq());

            try {
                System.out.println("[택배사 선택]");
                System.out.println("--".repeat(25));
                System.out.println("1. 한진택배 | 2.CJ대한통운 | 3.우체국택배 | 4.롯데택배 | 5.로젠택배");
                System.out.print("택배사 선택 : ");
                invoice.setLogisticSeq(Long.parseLong(sc.nextLine()));

                invoiceSeq = invoiceService.registerInvoice(invoice);
                Invoice result = invoiceService.getInvoiceRowByInvoiceSeq(invoiceSeq);

                invoice.setInvoiceCode(result.getInvoiceCode());
                invoice.setInvoicePrintDate(result.getInvoicePrintDate());
                Blob qrCodeImage = invoiceService.createQRCode(invoice, outgoingProductVO);
                status = invoiceService.putQRCode(qrCodeImage, invoiceSeq);

            } catch (NumberFormatException e){
                logger.info("숫자로 입력하세요.");
                e.printStackTrace();
            }
        }catch (IOException | SQLException i){
            i.printStackTrace();
        }

        return status;
    }

}