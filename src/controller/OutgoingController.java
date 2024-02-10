package controller;

import java.util.List;
import java.util.Scanner;
import service.OutgoingService;
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
            System.out.println("\n1. 출고 지시 목록 조회\n2. 메뉴 나가기");
            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> printAllOutgoingInsts();
                case 2 -> continueMenu = false;
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
}