package controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import service.OutgoingService;
import vo.InventoryVO;
import vo.OutgoingInstVO;
import vo.OutgoingVO;

public class OutgoingController {


    private Scanner scanner = new Scanner(System.in);
    private OutgoingService outgoingService;

    public OutgoingController(OutgoingService outgoingService) {
        this.outgoingService = outgoingService;
    }

    public void outgoingProductMenu() {
        boolean continueMenu = true;
        while (continueMenu) {
            System.out.println("\n1. 출고 지시 목록 조회\n2. 출고 수정 및 승인\n3. 메뉴 나가기");
            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> printAllOutgoingInsts();
                case 2 -> updateOutgoingProductMenu();
                case 3 -> continueMenu = false;
                default -> System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
    }

    public void outgoingProductSubMenu() {
        boolean continueMenu = true;
        while (continueMenu) {
            System.out.println("\n1. 출고 등록\n2. 서브 메뉴 나가기");
            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> addOutgoingProductList();
                case 2 -> continueMenu = false;
                default -> System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
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
            String input = scanner.next();
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
        try {
            System.out.print("수정할 출고 상품의 ID를 입력하세요: ");
            Long pkOutgoingId = scanner.nextLong();

            // 출고 상태를 WAIT로 변경하고, 출고일자를 현재로 설정
            outgoingService.updateOutgoingProductStatusAndDate(pkOutgoingId, LocalDateTime.now(), "WAIT");

            // 출고 수량 조회
            int currentQuantity = outgoingService.getOutgoingProductQuantity(pkOutgoingId);
            System.out.println("현재 출고 수량: " + currentQuantity + ". 수정할 수량을 입력하세요 (최대 " + currentQuantity + "): ");
            int newQuantity = scanner.nextInt();

            // 재고 정보 조회 및 선택
            System.out.print("상품 코드를 입력하세요: ");
            String productCd = scanner.next();
            List<InventoryVO> inventories = outgoingService.getInventoryByProductCodeAndQuantity(productCd, newQuantity);
            if (inventories.isEmpty()) {
                System.out.println("해당 상품에 대한 충분한 재고가 없습니다.");
                return;
            }

            System.out.println("선택 가능한 창고 및 구역 목록:");
            for (int i = 0; i < inventories.size(); i++) {
                InventoryVO inventory = inventories.get(i);
                System.out.println((i + 1) + ". 창고 코드: " + inventory.getWarehouseCd() + ", 구역 코드: " + inventory.getZoneCd() + ", 재고 수량: " + inventory.getInventoryCnt());
            }
            System.out.print("선택: ");
            int inventoryChoice = scanner.nextInt();
            InventoryVO selectedInventory = inventories.get(inventoryChoice - 1);

            // 출고 정보 업데이트 메서드 호출 (가정)
            outgoingService.updateOutgoingProduct(pkOutgoingId, newQuantity, selectedInventory.getWarehouseCd(), selectedInventory.getZoneCd());
            System.out.println("출고 상품이 성공적으로 업데이트되었습니다.");

        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}