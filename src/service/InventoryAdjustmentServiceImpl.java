package service;

import dao.InventoryAdjustmentDAOImpl;
import vo.InventoryVO;


import java.util.*;

public class InventoryAdjustmentServiceImpl implements InventoryAdjustmentService{
    Scanner in = new Scanner(System.in);

    private InventoryAdjustmentDAOImpl inventoryAdjustmentService = new InventoryAdjustmentDAOImpl();

    //조정 선택
    @Override
    public void adjustmentOptions() {
        System.out.println("[재고 조정]");
        System.out.println("\n**구분 선택**");
        System.out.println("-".repeat(50));
        System.out.println("1. 입고 조정 | 2. 출고 조정");
        System.out.println("-".repeat(50));

        System.out.print("번호 선택 : ");
        int adjustmentMenuChoice = Integer.parseInt(in.nextLine());

        if (adjustmentMenuChoice == 1 || adjustmentMenuChoice == 2) {

            switch (adjustmentMenuChoice) {
                case 1 -> increaseInventory();
                case 2 -> decreaseInventory();
            }
        } else {
            System.out.println("번호를 다시 입력하세요");
            adjustmentOptions();
        }
    }

    private int selectInventoryNumber() {
        List<InventoryVO> inventoryList = inventoryAdjustmentService.getInventory();
        printProductInventoryList(inventoryList);

        System.out.print("수정할 재고 번호 선택 : ");
        int selectedNumber = Integer.parseInt(in.nextLine());

        boolean isValidNumber = inventoryList.stream().anyMatch(inventory -> inventory.getInventorySeq() == selectedNumber);
        if (!isValidNumber) {
            System.out.println("잘못된 번호입니다. 다시 선택하세요.");
            inventoryList.clear();
            return selectInventoryNumber();
        }
        return selectedNumber;
    }


    private void printUpdatedInventory(int selectedNumber) {
        List<InventoryVO> updatedInventoryList = inventoryAdjustmentService.getUpdatedInventory(selectedNumber);
        printProductInventoryList(updatedInventoryList);
    }

    private void increaseInventory() {
        int selectedNumber = selectInventoryNumber();
        System.out.print("조정 재고량 : ");
        int adjustedQuantity = Integer.parseInt(in.nextLine());

        int ack = inventoryAdjustmentService.updateIncreaseInventoryQuantity(selectedNumber, adjustedQuantity);

        if (ack == 0) {
            System.out.println("재고 조정 실패");
        } else {
            printUpdatedInventory(selectedNumber);
        }

    }


    private void decreaseInventory() {

        int selectedNumber;
        while (true) {
            selectedNumber = selectInventoryNumber();
            if (selectedNumber != -1) {
                break;
            }
        }

        System.out.print("조정 재고량 : ");
        int adjustedQuantity = Integer.parseInt(in.nextLine());
        int currentQuantity = getCurrentQuantity(selectedNumber); //기존 재고량

        if (adjustedQuantity <= currentQuantity) {
            int ack = inventoryAdjustmentService.updateDecreaseInventoryQuantity(selectedNumber, adjustedQuantity);
            if (ack == 0) {
                System.out.println("재고 조정 실패");
            } else {
                printUpdatedInventory(selectedNumber);
            }
        } else {
            System.out.println("기존 수량보다 많이 차감할 수 없습니다."); //re 멘트 수정

        }
    }

    private int getCurrentQuantity(int selectedNumber) {
        List<InventoryVO> inventoryList = inventoryAdjustmentService.getInventory(); //최신 데이터를 가져야 함
        int currentQuantity = inventoryList.stream()
                .filter(inventory -> inventory.getInventorySeq() == selectedNumber)
                .mapToInt(InventoryVO::getInventoryCnt)
                .findFirst()
                .orElse(0);
        return currentQuantity;

    }

    private void printProductInventoryList(List<InventoryVO> inventoryList) {

        System.out.println("-".repeat(100));
        System.out.printf("%-20s%-15s%-15s%-13s%-13s%s\n",
                "번호", "상품 번호", "재고 수량", "날짜", "창고", "창고 구역");
        System.out.println("-".repeat(100));
        for (InventoryVO inventory : inventoryList) {
            System.out.printf("%-15s%-25s%-15d%-13s%-13s%s\n",
                    inventory.getInventorySeq(),
                    inventory.getProductCd(),
                    inventory.getInventoryCnt(),
                    inventory.getInventorySlipDate(),
                    inventory.getWarehouseCd(),
                    inventory.getZoneCd());
        }
        System.out.println("-".repeat(100));
    }
}
