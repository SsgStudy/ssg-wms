package controller;

import dao.LoginManagementDAOImpl;
import service.*;
import util.MenuBoxPrinter;
import util.enumcollect.MemberEnum;
import vo.CategoryVO;
import vo.InventoryVO;
import vo.ProductInventoryCategoryVO;
import vo.ProductInventoryWarehouseVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 이 클래스는 재고 관리를 담당하는 InventoryController 컨트롤러입니다.
 * 주로 재고 조회, 조정, 이동과 같은 기능을 수행합니다.
 * 해당 클래스는 Singleton 패턴을 따르며, 하나의 인스턴스만을 생성하여 사용합니다.
 *
 * @author : 김소진
 */
public class InventoryController {
    private LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
    private MemberEnum loginMemberRole;
    private String loginMemberId;
    private Scanner sc = new Scanner(System.in);
    private boolean categoryContinue = true;
    private final InventoryAdjustmentService adjustmentService;
    private final InventoryMovementService movementService;
    private final InventoryQueryService queryService;

    /**
     * Instantiates a new Inventory controller.
     *
     * @param adjustmentService the adjustment service
     * @param movementService   the movement service
     * @param queryService      the query service
     */
    public InventoryController(InventoryAdjustmentService adjustmentService,
                               InventoryMovementService movementService,
                               InventoryQueryService queryService) {
        this.adjustmentService = adjustmentService;
        this.movementService = movementService;
        this.queryService = queryService;
        this.sc = new Scanner(System.in);
    }
    private List<CategoryVO> categoryList = new ArrayList<>();

    /**
     * Menu.
     */
    public void menu() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
        while (true) {
            String[] menuItems = {
                    "1. 재고 조회\t",
                    "2. 재고 조정\t",
                    "3. 재고 이동\t",
                    "4. 메뉴 나가기\t\t\t"
            };
            MenuBoxPrinter.printMenuBoxWithTitle("재고 관리\t\t", menuItems);

            int manageChoice = Integer.parseInt(sc.nextLine());
            switch (manageChoice) {
                case 1 -> inventoryQuerySubMenu();
                case 2 -> adjustInventory();
                case 3 -> moveInventory();
                case 4 -> {
                    return;
                }
                default -> System.out.println("옳지 않은 입력입니다.");
            }
        }
    }

    /**
     * Inventory query sub menu.
     */
    private void inventoryQuerySubMenu() {
        categoryContinue = true;
        while (categoryContinue) {
            String[] menuItems = {
                    "1. 창고별 재고 조회\t\t\t\t",
                    "2. 카테고리별 재고 조회\t\t\t\t",
                    "3. 메뉴 나가기\t\t\t"
            };
            MenuBoxPrinter.printMenuBoxWithTitle("재고 조회\t\t", menuItems);
            int manageChoice = Integer.parseInt(sc.nextLine());
            switch (manageChoice) {
                case 1 -> getProductInventoryTotalByWarehouse();
                case 2 -> getProductInventoryByCategory();
                case 3 -> {
                    return;
                }
                default -> System.out.println("번호를 다시 선택해주세요.");
            }
        }
    }

    /**
     * Gets product inventory total by warehouse.
     */
    public void getProductInventoryTotalByWarehouse() {
        System.out.println("\n***창고별 재고 현황***");
        List<ProductInventoryWarehouseVO> productInventoryWarehouseList = queryService.getProductInventoryTotalByWarehouse();

        List<String> uniqueProductCodes = productInventoryWarehouseList.stream()
                .map(ProductInventoryWarehouseVO::getProductCode)
                .distinct()
                .collect(Collectors.toList());

        List<String> uniqueWarehouseCodes = productInventoryWarehouseList.stream()
                .map(ProductInventoryWarehouseVO::getWarehouseCode)
                .distinct()
                .collect(Collectors.toList());

        Collections.sort(uniqueWarehouseCodes);

        System.out.println("-".repeat(300));
        System.out.printf("%-20s  %-17s  %-8s  ", "상품 번호", "상품 이름", "총 수량");
        uniqueWarehouseCodes.forEach(warehouseCode -> System.out.printf("%-12s  ", warehouseCode));
        System.out.println();
        System.out.println("-".repeat(300));

        Map<String, Integer> productTotalMap = new HashMap<>();
        for (String productCode : uniqueProductCodes) {
            int totalQuantity = productInventoryWarehouseList.stream()
                    .filter(p -> p.getProductCode().equals(productCode))
                    .mapToInt(ProductInventoryWarehouseVO::getInventoryCnt)
                    .sum();
            productTotalMap.put(productCode, totalQuantity);

            System.out.printf("%-20s  %-20s  %-8d  ", productCode,
                    productInventoryWarehouseList.stream()
                            .filter(p -> p.getProductCode().equals(productCode))
                            .findFirst()
                            .map(ProductInventoryWarehouseVO::getProductName)
                            .orElse(""),
                    totalQuantity);

            for (String warehouseCode : uniqueWarehouseCodes) {
                int quantityInWarehouse = productInventoryWarehouseList.stream()
                        .filter(p -> p.getProductCode().equals(productCode) && p.getWarehouseCode().equals(warehouseCode))
                        .mapToInt(ProductInventoryWarehouseVO::getInventoryCnt)
                        .sum();

                String quantityString = quantityInWarehouse == 0 ? "0" : String.valueOf(quantityInWarehouse);
                System.out.printf("%-12s  ", quantityString);
            }
            System.out.println();
        }
        System.out.println("-".repeat(300));

        System.out.printf("%-45s%-10d  ", "합계",
                productTotalMap.values().stream().mapToInt(Integer::intValue).sum());
        uniqueWarehouseCodes.forEach(warehouseCode -> {
            int totalQuantity = productInventoryWarehouseList.stream()
                    .filter(p -> p.getWarehouseCode().equals(warehouseCode))
                    .mapToInt(ProductInventoryWarehouseVO::getInventoryCnt)
                    .sum();
            System.out.printf("%-12d  ", totalQuantity);
        });
        System.out.println();
        System.out.println("-".repeat(300));
        menu();
    }

    /**
     * Gets product inventory by category.
     */
    public void getProductInventoryByCategory() {
        while (categoryContinue) {
            categoryList.clear();
            categoryList = queryService.getMainCategories();

            List<String> categoryNameList = categoryList.stream().map(CategoryVO::getCategoryName).collect(Collectors.toList());
            MenuBoxPrinter.printCategoryNameList("대분류별 카테고리 확인", categoryNameList);

            int mainCategoryNumber = Integer.parseInt(sc.nextLine());
            int categoryNameListSize = categoryNameList.size();
            if (mainCategoryNumber > categoryNameListSize || mainCategoryNumber <= 0)
                System.out.println("번호를 다시 입력하세요.");
            else {
                String categoryName = categoryNameList.get(mainCategoryNumber - 1);
                getInventoryByMainCategory(mainCategoryNumber, categoryName);
                String[] menuItems = {
                        "1. 하위 카테고리 선택\t\t\t\t",
                        "2. 상위 카테고리 재선택\t\t\t\t",
                        "3. 메뉴 나가기\t\t\t"
                };
                MenuBoxPrinter.printMenuBoxWithTitle("대분류별 카테고리\t\t\t\t\t", menuItems);
                int menuChoice = Integer.parseInt(sc.nextLine());
                switch (menuChoice) {
                    case 1 -> getSubCategoriesByMainCategory(mainCategoryNumber);
                    case 2 -> {
                        getProductInventoryByCategory();
                        mainCategoryNumber = 0;
                    }
                    case 3 -> categoryContinue = false;
                    default -> System.out.println("옳지 않은 입력입니다.");

                }
            }
        }
    }

    /**
     * Gets sub categories by main category.
     *
     * @param mainCategoryNumber the main category number
     */
    public void getSubCategoriesByMainCategory(int mainCategoryNumber) {
        while (categoryContinue) {
            categoryList.clear();
            categoryList = queryService.getSubCategoriesByMainCategory(mainCategoryNumber);

            List<String> categoryNameList = categoryList.stream().map(CategoryVO::getCategoryName).collect(Collectors.toList());

            MenuBoxPrinter.printCategoryNameList("중분류별 카테고리\t\t\t\t\t", categoryNameList);

            int subCategoryNumber = Integer.parseInt(sc.nextLine());
            int categoryNameListSize = categoryNameList.size();
            if (subCategoryNumber > categoryNameListSize || subCategoryNumber <= 0) System.out.println("번호를 다시 입력하세요.");
            else {
                String categoryName = categoryNameList.get(subCategoryNumber - 1);
                getInventoryBySubCategory(mainCategoryNumber, subCategoryNumber, categoryName);

                String[] menuItems = {
                        "1. 하위 카테고리 선택\t\t\t\t",
                        "2. 상위 카테고리 재선택\t\t\t\t",
                        "3. 메뉴 나가기\t\t\t"
                };
                MenuBoxPrinter.printMenuBoxWithTitle("중분류별 카테고리\t\t\t\t\t", menuItems);
                int menuChoice = Integer.parseInt(sc.nextLine());
                switch (menuChoice) {
                    case 1 -> getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
                    case 2 -> getSubCategoriesByMainCategory(mainCategoryNumber);
                    case 3 -> categoryContinue = false;
                    default -> System.out.println("옳지 않은 입력입니다.");
                }
            }
        }
    }

    /**
     * Gets detail categories by sub category.
     *
     * @param mainCategoryNumber the main category number
     * @param subCategoryNumber  the sub category number
     */
    public void getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        while (categoryContinue) {
            categoryList.clear();
            categoryList = queryService.getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
            List<String> categoryNameList = categoryList.stream().map(CategoryVO::getCategoryName).collect(Collectors.toList());
            MenuBoxPrinter.printCategoryNameList("소분류별 카테고리\t\t\t\t\t", categoryNameList);

            int detailCategoryNumber = Integer.parseInt(sc.nextLine());
            int categoryNameListSize = categoryNameList.size();
            if (detailCategoryNumber > categoryNameListSize || subCategoryNumber <= 0)
                System.out.println("번호를 다시 입력하세요.");
            else {
                String categoryName = categoryNameList.get(detailCategoryNumber - 1);
                getInventoryByDetailCategory(mainCategoryNumber, subCategoryNumber, detailCategoryNumber, categoryName);


                String[] menuItems = {
                        "1. 하위 카테고리 선택\t\t\t\t",
                        "2. 카테고리 초기화\t\t\t",
                        "3. 메뉴 나가기\t\t\t"
                };
                MenuBoxPrinter.printMenuBoxWithTitle("소분류별 카테고리\t\t\t\t\t", menuItems);
                int menuChoice = Integer.parseInt(sc.nextLine());
                switch (menuChoice) {
                    case 1 -> getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
                    case 2 -> getProductInventoryByCategory();
                    case 3 -> categoryContinue = false;
                    default -> System.out.println("옳지 않은 입력입니다.");
                }
            }
        }
    }

    /**
     * Gets inventory by main category.
     *
     * @param categoryNumber the category number
     * @param categoryName   the category name
     */
    private void getInventoryByMainCategory(int categoryNumber, String categoryName) {
        System.out.printf("\n[%s 상품 및 재고수량 확인]\n", categoryName);
        printProductInventoryCategoryList(queryService.getInventoryByMainCategory(categoryNumber));
    }

    /**
     * Gets inventory by sub category.
     *
     * @param mainCategoryNumber the main category number
     * @param subCategoryNumber  the sub category number
     * @param categoryName       the category name
     */
    private void getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber, String categoryName) {
        System.out.printf("\n[%s 상품 및 재고수량 확인]\n", categoryName);
        printProductInventoryCategoryList(queryService.getInventoryBySubCategory(mainCategoryNumber, subCategoryNumber));
    }

    /**
     * Gets inventory by detail category.
     *
     * @param mainCategoryNumber   the main category number
     * @param subCategoryNumber    the sub category number
     * @param detailCategoryNumber the detail category number
     * @param categoryName         the category name
     */
    private void getInventoryByDetailCategory(int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber, String categoryName) {
        System.out.printf("\n[%s 상품 및 재고수량 확인]\n", categoryName);
        printProductInventoryCategoryList(queryService.getInventoryByDetailCategory(mainCategoryNumber, subCategoryNumber, detailCategoryNumber));

    }

    /**
     * Print category name list.
     *
     * @param categoryNameList the category name list
     */
    private void printCategoryNameList(List<String> categoryNameList) {
        System.out.println("-".repeat(300));
        for (int i = 1; i <= categoryNameList.size(); i++) {
            System.out.print(i + ". " + categoryNameList.get(i - 1) + " ".repeat(5));
        }
        System.out.println("\n" + "-".repeat(300));
    }

    /**
     * Print product inventory category list.
     *
     * @param productInventoryCategoryListList the product inventory category list list
     */
    private void printProductInventoryCategoryList(List<ProductInventoryCategoryVO> productInventoryCategoryListList) {
        System.out.println("-".repeat(100));
        System.out.printf("%-25s%-17s%-13s%-13s%s\n",
                "상품 번호", "상품 이름", "상품 가격", "재고 수량", "카테고리 코드");
        System.out.println("-".repeat(100));
        for (ProductInventoryCategoryVO productInventory : productInventoryCategoryListList) {
            System.out.printf("%-25s%-20s%-15d%-12d%s\n",
                    productInventory.getProductCode(),
                    productInventory.getProductName(),
                    productInventory.getProductPrice(),
                    productInventory.getTotalInventoryCnt(),
                    productInventory.getCategoryCode());
        }
        System.out.println("-".repeat(100));
        System.out.printf("%-60s%d%n", "총 재고 수량", productInventoryCategoryListList.stream().mapToInt(ProductInventoryCategoryVO::getTotalInventoryCnt).sum());
        System.out.println("-".repeat(100));
    }

    /**
     * Adjust inventory.
     * <p>
     * 접근제한 : 회원
     */
    public void adjustInventory() {
        if (!(
                loginMemberRole == MemberEnum.WAREHOUSE_MANAGER ||
                        loginMemberRole == MemberEnum.ADMIN
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        boolean adjustContinue = true;
        while (adjustContinue) {
            String[] menuItems = {
                    "1. 입고 조정\t",
                    "2. 출고 조정\t",
                    "3. 메뉴 나가기\t\t\t"
            };
            MenuBoxPrinter.printMenuBoxWithTitle("재고 조정\t\t", menuItems);

            int adjustmentMenuChoice = Integer.parseInt(sc.nextLine());
            switch (adjustmentMenuChoice) {
                case 1 -> increaseInventory();
                case 2 -> decreaseInventory();
                case 3 -> adjustContinue = false;
                default -> System.out.println("옳지 않은 입력입니다.");
            }
        }
    }

    /**
     * Increase inventory.
     */
    public void increaseInventory() {
        int selectedNumber = selectInventoryNumber();
        System.out.print("\n➔ 조정 재고량 : ");
        int adjustedQuantity = Integer.parseInt(sc.nextLine());
        int ack = adjustmentService.updateIncreaseInventoryQuantity(selectedNumber, adjustedQuantity);

        if (ack == 0) {
            System.out.println("재고 조정에 실패했습니다.");
        } else {
            List<InventoryVO> inventoryList = adjustmentService.getUpdatedInventory(selectedNumber);
            printProductInventoryList(inventoryList);
        }
    }

    /**
     * Decrease inventory.
     */
    public void decreaseInventory() {
        int selectedNumber = selectInventoryNumber();
        System.out.print("\n➔ 조정 재고량 : ");
        int adjustedQuantity = Integer.parseInt(sc.nextLine());
        int currentQuantity = getCurrentQuantity(selectedNumber);

        if (adjustedQuantity <= currentQuantity) {
            int ack = adjustmentService.updateDecreaseInventoryQuantity(selectedNumber, adjustedQuantity);
            if (ack == 0) {
                System.out.println("재고 조정에 실패했습니다.");
            } else {
                List<InventoryVO> inventoryList = adjustmentService.getUpdatedInventory(selectedNumber);
                printProductInventoryList(inventoryList);
            }
        } else {
            System.out.println("조정 후 재고량이 음수가 될 수 없습니다.");
        }
    }

    /**
     * Select inventory number int.
     *
     * @return the int
     */
    private int selectInventoryNumber() {
        List<InventoryVO> inventoryList = adjustmentService.getInventoryInformation();
        printProductInventoryList(inventoryList);
        System.out.print("\n➔ 수정할 재고 번호 선택 : ");
        int selectedInventoryIndex = Integer.parseInt(sc.nextLine());
        if (selectedInventoryIndex <= 0 || selectedInventoryIndex > inventoryList.size() + 1) {
            System.out.println("잘못된 번호입니다. 다시 선택하세요.");
            inventoryList.clear();
            return selectInventoryNumber();
        }
        return selectedInventoryIndex;
    }

    /**
     * Gets current quantity.
     *
     * @param selectedNumber the selected number
     * @return the current quantity
     */
    private int getCurrentQuantity(int selectedNumber) {
        List<InventoryVO> inventoryList = adjustmentService.getInventoryInformation();
        int currentQuantity = inventoryList.stream()
                .filter(inventory -> inventory.getInventorySeq() == selectedNumber)
                .mapToInt(InventoryVO::getInventoryCnt)
                .findFirst()
                .orElse(0);
        return currentQuantity;
    }


    /**
     * Move inventory.
     * <p>
     * 접근제한 : 회원
     */
    public void moveInventory() {
        if (!(
                loginMemberRole == MemberEnum.WAREHOUSE_MANAGER ||
                        loginMemberRole == MemberEnum.ADMIN
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        while (true) {
            List<InventoryVO> inventoryList = movementService.getInventoryInformation();
            printProductInventoryList(inventoryList);
            System.out.print("\n➔ 번호 선택 : ");
            int selectedNumber = Integer.parseInt(sc.nextLine());
            if (selectedNumber <= 0 || selectedNumber > inventoryList.size() + 1) {
                System.out.println("잘못된 번호입니다. 다시 선택하세요.");
                inventoryList.clear();
            } else {
                String selectedWarehouseCode = selectWarehouseCode();
                String selectedZoneCode = selectZoneCode(selectedWarehouseCode);

                List<InventoryVO> selectedInventoryList = inventoryList.stream().filter(inventory -> inventory.getInventorySeq() == selectedNumber).collect(Collectors.toList());

                InventoryVO selectedInventory = setSelectedInventory(selectedInventoryList, selectedWarehouseCode, selectedZoneCode);

                int ack = movementService.updateInventoryMovement(selectedInventory);
                if (ack == 0) {
                    System.out.println("재고 이동이 실패했습니다.");
                } else {
                    getUpdatedInventory(selectedNumber);
                }
                break;
            }
        }
    }

    /**
     * Select warehouse code string.
     *
     * @return the string
     */
    private String selectWarehouseCode() {
        List<String> warehouseCodeList = movementService.getWarehouseCode();
        Map<Integer, String> warehouseCodeMap = new HashMap<>();

        for (int i = 0; i < warehouseCodeList.size(); i++) {
            warehouseCodeMap.put(i + 1, warehouseCodeList.get(i));
        }
        String warehouseCode;

        while (true) {
            System.out.println("\n***이동 창고 선택***");

            System.out.println("-".repeat(300));
            for (Map.Entry<Integer, String> warehouseNumberCode : warehouseCodeMap.entrySet()) {
                System.out.print(warehouseNumberCode.getKey() + ". " + warehouseNumberCode.getValue() + " ".repeat(5));
            }
            System.out.println("\n" + "-".repeat(300));

            System.out.print("\n➔ 번호 선택 : ");
            int warehouseCodeChoice = Integer.parseInt(sc.nextLine());
            if (warehouseCodeChoice <= 0 || warehouseCodeChoice >= warehouseCodeMap.size() + 1) {
                System.out.println("번호를 다시 입력하세요.");
            } else {
                warehouseCode = warehouseCodeMap.get(warehouseCodeChoice);
                break;
            }
        }
        return warehouseCode;
    }

    /**
     * Select zone code string.
     *
     * @param selectedWarehouseCode the selected warehouse code
     * @return the string
     */
    private String selectZoneCode(String selectedWarehouseCode) {
        List<String> zoneCodeList = movementService.getZoneCode(selectedWarehouseCode);
        Map<Integer, String> zoneCodeMap = new HashMap<>();

        for (int i = 0; i < zoneCodeList.size(); i++) {
            zoneCodeMap.put(i + 1, zoneCodeList.get(i));
        }

        String zoneCode = null;

        while (true) {
            System.out.println("\n*** 이동 창고 구역 선택***");

            System.out.println("-".repeat(300));
            for (Map.Entry<Integer, String> warehouseNumberCode : zoneCodeMap.entrySet()) {
                System.out.print(warehouseNumberCode.getKey() + ". " + warehouseNumberCode.getValue() + " ".repeat(5));
            }
            System.out.println("\n" + "-".repeat(300));

            System.out.print("\n➔ 번호 선택 : ");
            int zoneCodeChoice = Integer.parseInt(sc.nextLine());
            if (zoneCodeChoice <= 0 || zoneCodeChoice >= zoneCodeMap.size() + 1) {
                System.out.println("번호를 다시 입력하세요.");
            } else {
                zoneCode = zoneCodeMap.get(zoneCodeChoice);
                break;
            }
        }
        return zoneCode;
    }

    /**
     * Sets selected inventory.
     *
     * @param selectedInventoryList  the selected inventory list
     * @param selectedWarehouseIndex the selected warehouse index
     * @param selectedZoneCode       the selected zone code
     * @return the selected inventory
     */
    private InventoryVO setSelectedInventory(List<InventoryVO> selectedInventoryList, String selectedWarehouseIndex, String selectedZoneCode) {
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

    /**
     * Gets updated inventory.
     *
     * @param selectedNumber the selected number
     */
    private void getUpdatedInventory(int selectedNumber) {
        List<InventoryVO> updatedInventoryList = movementService.getUpdatedInventory(selectedNumber);
        printProductInventoryList(updatedInventoryList);
    }

    /**
     * Print product inventory list.
     *
     * @param inventoryList the inventory list
     */
    private void printProductInventoryList(List<InventoryVO> inventoryList) {
        System.out.println("-".repeat(300));
        System.out.printf("%-20s%-15s%-15s%-13s%-13s%s\n",
                "번호", "상품 번호", "재고 수량", "날짜", "창고", "창고 구역");
        System.out.println("-".repeat(300));
        for (InventoryVO inventory : inventoryList) {
            System.out.printf("%-15s%-25s%-15d%-13s%-13s%s\n",
                    inventory.getInventorySeq(),
                    inventory.getProductCd(),
                    inventory.getInventoryCnt(),
                    inventory.getInventorySlipDate(),
                    inventory.getWarehouseCd(),
                    inventory.getZoneCd());
        }
        System.out.println("-".repeat(300));
    }
}
