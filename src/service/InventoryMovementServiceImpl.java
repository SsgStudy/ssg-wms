package service;

import dao.InventoryMovementDAOImpl;
import vo.InventoryVO;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryMovementServiceImpl implements InventoryMovementService {
    Scanner in = new Scanner(System.in);
    private InventoryMovementDAOImpl warehouseInventoryMovementDAO = new InventoryMovementDAOImpl();
    private List<InventoryVO> inventoryList = new ArrayList<>();
    private Map<Integer, String> warehouseCodeMap = new HashMap<>();
    private Map<Integer, String> zoneCodeMap = new HashMap<>();

    @Override
    public void selectInventoryForMovement() {

        System.out.println("[재고 이동]");

        inventoryList = warehouseInventoryMovementDAO.getInventory();
        printProductInventoryList(inventoryList);

        System.out.print("번호 선택 : ");
        int selectedInventoryIndex = Integer.parseInt(in.nextLine());

        boolean isSelectedInventoryValid = inventoryList.stream()
                .anyMatch(inventory -> inventory.getInventorySeq() == selectedInventoryIndex);

        if (isSelectedInventoryValid) {
            List<InventoryVO> selectedInventoryList = inventoryList.stream().filter(inventory -> inventory.getInventorySeq() == selectedInventoryIndex).collect(Collectors.toList());

            String selectedWarehouseCode = warehouseCodeOptions(); //창고 선택

            String selectedZoneCode = zoneCodeOptions(selectedWarehouseCode); //구역 선택

            InventoryVO selectedInventory = setSelectedInventory(selectedInventoryList, selectedWarehouseCode, selectedZoneCode);

            int ack = warehouseInventoryMovementDAO.updateInventoryForMovement(selectedInventory);

            if (ack == 0) {
                System.out.println("재고 이동이 실패했습니다.");
            } else {
                printUpdatedInventory(selectedInventoryIndex);
            }

        } else {
            System.out.println("번호를 다시 입력하세요.");
            inventoryList.clear();
            selectInventoryForMovement();

        }
    }

    private InventoryVO setSelectedInventory(List<InventoryVO> selectedInventoryList, String selectedWarehouseIndex, String selectedZoneCode) { //re 보류 -> 언젠가 수정
        InventoryVO selectedInventory = new InventoryVO();

        if (!selectedInventoryList.isEmpty()) {
            for (InventoryVO inventory : selectedInventoryList) {
                selectedInventory.setInventorySeq(inventory.getInventorySeq());
                selectedInventory.setProductCd(inventory.getProductCd());
                selectedInventory.setInventoryCnt(inventory.getInventoryCnt());
                selectedInventory.setWarehouseCd(selectedWarehouseIndex);
                selectedInventory.setZoneCd(selectedZoneCode);
            }

        } else {
            System.out.println("선택된 재고 목록이 없습니다.");
        }
        return selectedInventory;
    }

    private String warehouseCodeOptions() {
        List<String> warehouseCodeList = warehouseInventoryMovementDAO.getWarehouseCode();
        for (int i = 0; i < warehouseCodeList.size(); i++) {
            warehouseCodeMap.put(i + 1, warehouseCodeList.get(i));
        }
        String warehouseCode;

        while (true) {
            System.out.println("\n**이동 창고 선택**");

            System.out.println("-".repeat(150));
            for (Map.Entry<Integer, String> warehouseNumberCode : warehouseCodeMap.entrySet()) {
                System.out.print(warehouseNumberCode.getKey() + ". " + warehouseNumberCode.getValue() + " ".repeat(5));
            }
            System.out.println("\n" + "-".repeat(150));

            System.out.print("번호 선택 : ");
            int warehouseCodeChoice = Integer.parseInt(in.nextLine()); //번호만 저장
            if (warehouseCodeChoice <= 0 || warehouseCodeChoice >= warehouseCodeMap.size() + 1) {
                System.out.println("번호를 다시 입력하세요.");
            } else {
                warehouseCode = warehouseCodeMap.get(warehouseCodeChoice); //번호에 해당하는 창고 코드 저장
                break;
            }
        }
        return warehouseCode;
    }

    private String zoneCodeOptions(String selectedWarehouseIndex) {
        List<String> zoneCodeList = warehouseInventoryMovementDAO.getZoneCode(selectedWarehouseIndex);
        for (int i = 0; i < zoneCodeList.size(); i++) {
            zoneCodeMap.put(i + 1, zoneCodeList.get(i));
        }

        String zoneCode = null;

        while (true) {
            System.out.println("\n** 이동 창고 구역 선택**");

            System.out.println("-".repeat(150));
            for (Map.Entry<Integer, String> warehouseNumberCode : zoneCodeMap.entrySet()) {
                System.out.print(warehouseNumberCode.getKey() + ". " + warehouseNumberCode.getValue() + " ".repeat(5));
            }
            System.out.println("\n" + "-".repeat(150));

            System.out.print("번호 선택 : ");
            int zoneCodeChoice = Integer.parseInt(in.nextLine()); //번호만 저장
            if (zoneCodeChoice <= 0 || zoneCodeChoice >= zoneCodeMap.size() + 1) {
                System.out.println("번호를 다시 입력하세요.");
            } else {
                zoneCode = zoneCodeMap.get(zoneCodeChoice); //번호에 해당하는 창고 코드 저장
                break;
            }
        }
        return zoneCode;
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

    private void printUpdatedInventory(int selectedNumber) {
        List<InventoryVO> updatedInventoryList = warehouseInventoryMovementDAO.getUpdatedInventory(selectedNumber);
        printProductInventoryList(updatedInventoryList);
    }
}
