package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import dao.InvoiceDao;
import dao.InvoiceDaoImpl;
import service.IncomigService;
import service.InvoiceService;
import service.InvoiceServiceImpl;

import service.OutgoingService;
import vo.InventoryVO;
import vo.OutgoingInstVO;
import vo.OutgoingProductVO;
import vo.OutgoingVO;

public class OutgoingController {

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
            int choice = sc.nextInt();

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
            int choice = sc.nextInt();

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
            String input = sc.next();
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
            Long pkOutgoingId = sc.nextLong();

            // 출고 상품에 대한 상품 코드 자동 조회
            String productCd = outgoingService.getProductCodeByOutgoingId(pkOutgoingId);

            // 출고 수량 조회
            int currentQuantity = outgoingService.getOutgoingProductQuantity(pkOutgoingId);
            System.out.println("현재 출고 수량: " + currentQuantity + ". 수정할 수량을 입력하세요 (최대 " + currentQuantity + "): ");
            int newQuantity = sc.nextInt();

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
            int inventoryChoice = sc.nextInt();
            InventoryVO selectedInventory = inventories.get(inventoryChoice - 1);

            // 출고 상품 업데이트 및 출고 상태 WAIT로 변경, 출고 일자 업데이트
            outgoingService.updateOutgoingProduct(pkOutgoingId, newQuantity, selectedInventory.getWarehouseCd(),
                    selectedInventory.getZoneCd());
            outgoingService.updateOutgoingProductStatusAndDate(pkOutgoingId, outgoingDate, "WAIT");

            // 송장 연결
            OutgoingProductVO outgoingProduct = outgoingService.getOutgoingProductRowByOutgoingId(pkOutgoingId);
            int result = invoiceService.registerInvoice(outgoingProduct);

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

}