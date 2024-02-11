package service;

import dao.InventoryQueryDAOImpl;
import vo.CategoryVO;
import vo.ProductInventoryCategoryVO;
import vo.ProductInventoryWarehouseVO;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryQueryServiceImpl implements InventoryQueryService {
    private InventoryQueryDAOImpl inventoryQueryService = new InventoryQueryDAOImpl();
    private List<CategoryVO> categoryList = new ArrayList<>();
    private List<ProductInventoryCategoryVO> productInventoryCategoryList = new ArrayList<>();
    private List<ProductInventoryWarehouseVO> productInventoryWarehouseList = new ArrayList<>();
    Scanner in = new Scanner(System.in);

    //상품 창고별 재고 조회
    @Override
    public void getProductInventoryTotalByWarehouse() {
        productInventoryWarehouseList.clear();
        System.out.println("[창고별 재고 현황]");
        productInventoryWarehouseList = inventoryQueryService.getInventoryTotalByWarehouse();

        List<ProductInventoryWarehouseVO> productInventoryWarehouseList = inventoryQueryService.getInventoryTotalByWarehouse();
        List<String> uniqueProductCodes = productInventoryWarehouseList.stream()
                .map(ProductInventoryWarehouseVO::getProductCode)
                .distinct()
                .collect(Collectors.toList());

        List<String> uniqueWarehouseCodes = productInventoryWarehouseList.stream()
                .map(ProductInventoryWarehouseVO::getWarehouseCode)
                .distinct()
                .collect(Collectors.toList());

        Collections.sort(uniqueWarehouseCodes);

        System.out.println("-".repeat(150));
        System.out.printf("%-20s  %-17s  %-8s  ", "상품 번호", "상품 이름", "총 수량");
        uniqueWarehouseCodes.forEach(warehouseCode -> System.out.printf("%-12s  ", warehouseCode));
        System.out.println();
        System.out.println("-".repeat(150));

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
        System.out.println("-".repeat(150));

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
        System.out.println("-".repeat(150));
    }

    //카테고리별 조회
    @Override //실행
    public void getMainCategories() {
        while (true) {
            categoryList.clear();

            System.out.println("[대분류별 카테고리 확인]");
            categoryList = inventoryQueryService.getMainCategories();

            List<String> categoryNameList = categoryList.stream().map(CategoryVO::getCategoryName).collect(Collectors.toList());

            printCategoryNameList(categoryNameList);

            System.out.print("카테고리 선택 : ");
            int mainCategoryNumber = Integer.parseInt(in.nextLine());
            int categoryNameListSize = categoryNameList.size();
            if (mainCategoryNumber > categoryNameListSize || mainCategoryNumber <= 0)
                System.out.println("번호를 다시 입력하세요.");
            else {
                String categoryName = categoryNameList.get(mainCategoryNumber - 1);
                getInventoryByMainCategory(mainCategoryNumber, categoryName);

                System.out.println("1. 하위 카테고리 선택 | 2. 상위 카테고리 다시 선택 | 3. 조회 종료");
                int menuChoice = Integer.parseInt(in.nextLine());
                switch (menuChoice) {
                    case 1 -> getSubCategoriesByMainCategory(mainCategoryNumber);
                    case 2 -> {
                        getMainCategories();
                        mainCategoryNumber = 0;
                    }
                    case 3 -> System.exit(0);
                    default -> System.out.println("번호를 다시 입력해주세요");

                }
            }
        }
    }

    @Override
    public void getSubCategoriesByMainCategory(int mainCategoryNumber) {
        while (true) {
            categoryList.clear();
            System.out.println("[중분류별 카테고리 확인]");
            categoryList = inventoryQueryService.getSubCategoriesByMainCategory(mainCategoryNumber);

            List<String> categoryNameList = categoryList.stream().map(CategoryVO::getCategoryName).collect(Collectors.toList());

            printCategoryNameList(categoryNameList);

            System.out.print("카테고리 선택 : ");
            int subCategoryNumber = Integer.parseInt(in.nextLine());
            int categoryNameListSize = categoryNameList.size();
            if (subCategoryNumber > categoryNameListSize || subCategoryNumber <= 0) System.out.println("번호를 다시 입력하세요.");
            else {
                String categoryName = categoryNameList.get(subCategoryNumber - 1);
                getInventoryBySubCategory(mainCategoryNumber, subCategoryNumber, categoryName);

                System.out.println("1. 하위 카테고리 선택 | 2. 상위 카테고리 다시 선택 | 3. 조회 종료 ");
                int menuChoice = Integer.parseInt(in.nextLine());
                switch (menuChoice) {
                    case 1 -> getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
                    case 2 -> getSubCategoriesByMainCategory(mainCategoryNumber);
                    case 3 -> System.exit(0);
                    default -> System.out.println("번호를 다시 입력해주세요");
                }
            }
        }
    }

    @Override
    public void getDetailCategoriesBySubCategory(int mainCategoryNumber, int subCategoryNumber) {
        while (true) {
            categoryList.clear();
            System.out.println("[소분류별 카테고리 확인]");
            categoryList = inventoryQueryService.getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
            List<String> categoryNameList = categoryList.stream().map(CategoryVO::getCategoryName).collect(Collectors.toList());
            printCategoryNameList(categoryNameList);

            System.out.print("카테고리 선택 : ");
            int detailCategoryNumber = Integer.parseInt(in.nextLine());
            int categoryNameListSize = categoryNameList.size();
            if (detailCategoryNumber > categoryNameListSize || subCategoryNumber <= 0)
                System.out.println("번호를 다시 입력하세요.");
            else {
                String categoryName = categoryNameList.get(detailCategoryNumber - 1);
                getInventoryByDetailCategory(mainCategoryNumber, subCategoryNumber, detailCategoryNumber, categoryName);


                System.out.println("1. 상위 카테고리 다시 선택 | 2. 카테고리 처음부터 선택 | 3. 조회 종료 ");
                int menuChoice = Integer.parseInt(in.nextLine());
                switch (menuChoice) {
                    case 1 -> getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
                    case 2 -> getMainCategories();
                    case 3 -> System.exit(0);
                    default -> System.out.println("번호를 다시 입력해주세요");
                }
            }
        }
    }

    @Override
    public void getInventoryByMainCategory(int categoryNumber, String categoryName) {
        System.out.printf("[%s 상품 및 재고수량 확인]\n", categoryName);
        productInventoryCategoryList = inventoryQueryService.getInventoryByMainCategory(categoryNumber);
        printProductInventoryCategoryList(productInventoryCategoryList);
    }

    @Override
    public void getInventoryBySubCategory(int mainCategoryNumber, int subCategoryNumber, String categoryName) {
        productInventoryCategoryList.clear();
        System.out.printf("[%s 상품 및 재고수량 확인]\n", categoryName);
        productInventoryCategoryList = inventoryQueryService.getInventoryBySubCategory(mainCategoryNumber, subCategoryNumber);
        printProductInventoryCategoryList(productInventoryCategoryList);
    }

    @Override
    public void getInventoryByDetailCategory(int mainCategoryNumber, int subCategoryNumber, int detailCategoryNumber, String categoryName) {
        productInventoryCategoryList.clear();
        System.out.printf("[%s 상품 및 재고수량 확인]\n", categoryName);
        productInventoryCategoryList = inventoryQueryService.getInventoryByDetailCategory(mainCategoryNumber, subCategoryNumber, detailCategoryNumber);
        printProductInventoryCategoryList(productInventoryCategoryList);


    }

    //리스트 출력 메서드
    private void printCategoryNameList(List<String> categoryNameList) {
        System.out.println("-".repeat(50));
        for (int i = 1; i <= categoryNameList.size(); i++) {
            System.out.print(i + ". " + categoryNameList.get(i - 1) + " ".repeat(5));
        }
        System.out.println("\n" + "-".repeat(50));
    }

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
}
