package controller;

import service.ProductServiceImpl;
import service.ProductService;
import util.enumcollect.SalesStatus;
import vo.Category;
import vo.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class ProductManagementController {

    private ProductService productService = new ProductServiceImpl();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    Scanner scanner = new Scanner(System.in);
    private String selectedCategoryCode;

    public void menu(){
        boolean continueMenu = true;
        while (continueMenu) {

            System.out.println("1. 상품 등록\n2. 상품 목록 조회 및 수정\n3. 메뉴 나가기");
            System.out.print("선택: ");
            int manageChoice = scanner.nextInt();

            switch (manageChoice) {
                case 1 -> registerProduct();
                case 2 -> getProductList();
                case 3 -> continueMenu = false;
                default -> System.out.println("옳지 않은 입력입니다.");
            }
        }
    }


    public void showSubCategories() {
        try {
            System.out.println("등록할 상품의 카테고리를 먼저 입력해주세요.");
            System.out.println("[대분류별 카테고리 확인]");
            List<Category> mainCategories = productService.getMainCategories();
            for (Category category : mainCategories) {
                System.out.println(category.getCategoryCode() + ": " + category.getCategoryName());
            }

            System.out.println("대분류 카테고리 번호를 입력하세요: ");
            int mainCategoryNumber = Integer.parseInt(br.readLine().trim());

            List<Category> subCategories = productService.getSubCategoriesByMainCategory(mainCategoryNumber);
            if (subCategories.isEmpty()) {
                System.out.println("선택한 대분류에 속하는 중분류 카테고리가 없습니다.");
                return;
            }

            System.out.println("[중분류별 카테고리 확인]");
            for (Category category : subCategories) {
                System.out.println(category.getCategoryCode() + ": " + category.getCategoryName());
            }
            System.out.println("중분류 카테고리 번호를 입력하세요: ");
            int subCategoryNumber = Integer.parseInt(br.readLine().trim());

            List<Category> detailCategories = productService.getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
            if (detailCategories.isEmpty()) {
                System.out.println("선택한 중분류에 속하는 소분류 카테고리가 없습니다.");
                return;
            }
            System.out.println("[소분류별 카테고리 확인]");
            for (Category category : detailCategories) {
                System.out.println(category.getCategoryCode() + ": " + category.getCategoryName());
            }
            System.out.println("소분류 카테고리 번호를 입력하세요: ");
            int detailCategoryNumber = Integer.parseInt(br.readLine().trim());

            selectedCategoryCode = String.format("%03d", mainCategoryNumber) + "-"
                    + String.format("%03d", subCategoryNumber) + "-"
                    + String.format("%03d", detailCategoryNumber);

        } catch (Exception e) {
            System.out.println("카테고리 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void registerProduct() {

        try {
            Product product = new Product();

            showSubCategories();

            System.out.println("상품코드 입력 규칙은 아래와 같은 형태로 입력해주세요.");
            System.out.println("국가코드 - 식별자 - 제조시기\n" +
                    "ex)\n" +
                    "국가코드 : 3자리 -> 한국 경우 880\n" +
                    "갤럭시 s23 제품 식별자 : 5678\n" +
                    "제조 시기 : 2023년 5월 → 0523");

            System.out.println("상품코드 : ");
            String productCode = br.readLine();
            product.setProductCode(productCode);
            System.out.println("상품명 : ");
            String productName = br.readLine();
            product.setProductName(productName);
            System.out.println("판매가격 : ");
            int productPrice = Integer.parseInt(br.readLine().trim());
            product.setProductPrice(productPrice);
            System.out.println("브랜드 : ");
            String productBrand = br.readLine();
            product.setProductBrand(productBrand);
            System.out.println("원산지 : ");
            String productOrigin = br.readLine();
            product.setProductOrign(productOrigin);
            System.out.println("제조사 : ");
            String productManufactor = br.readLine();
            product.setManufactor(productManufactor);


            product.setCategoryCode(selectedCategoryCode);
            productService.insertProduct(product);

        } catch (Exception e) {
            System.out.println("상품 등록 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getProductList() {

        System.out.println("전체 상품목록 불러오기");
        System.out.println("--".repeat(25));
        System.out.println("productCode \t productname \t productprice \t ");
        System.out.println("--".repeat(25));

        List<Product> productList = productService.getProductList();
        for (Product product : productList) {
            System.out.printf("%-25s %-25s %-10d\n", product.getProductCode(), product.getProductName(), product.getProductPrice());
        }
        System.out.println("--".repeat(25));

        System.out.println("1. 상품 상세보기 | 2. 메뉴 나가기");
        int selectednum = scanner.nextInt();

        switch (selectednum){
            case 1 -> {
                System.out.println("상품번호 : ");
                try{
                    String productcode = br.readLine().trim();
                    getProductByProductCode(productcode);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            case 2 -> {
                return;
            }
            default -> System.out.println("잘못입력하였습니다.");
        }


    }

    public void getProductByProductCode(String productCode) {

        Product product = productService.getProductByProductCode(productCode);

            if(product != null){
                System.out.println("-- 상품 정보 --");
                System.out.printf("상품코드: %s\n", product.getProductCode());
                System.out.printf("상품명: %s\n", product.getProductName());
                System.out.printf("판매가격: %d\n", product.getProductPrice());
                System.out.printf("브랜드: %s\n", product.getProductBrand());
                System.out.printf("원산지: %s\n", product.getProductOrign());
                System.out.printf("제조사: %s\n", product.getManufactor());
                System.out.printf("카테고리 코드: %s\n", product.getCategoryCode());

            }else{
                System.out.println("해당 상품번호의 상품이 존재하지 않습니다.");
            }

            System.out.println("--------------------------------");
            System.out.println("1. 상품정보 수정 | 2. 상품목록으로 돌아가기");

            try{
                String cmd = br.readLine();

                switch (cmd) {
                    case "1" ->  updateProductByProductCode(productCode);
                    case "2" -> getProductList();
                }
            } catch(IOException i){ i.printStackTrace();}
    }

    public int updateProductByProductCode(String productCode){
        int state = 0;

        System.out.println("상품 정보를 수정합니다.");

        try{

            Product updateProduct = new Product();
            System.out.println("상품명 : ");
            updateProduct.setProductName(br.readLine());
            System.out.println("판매가격 : ");
            updateProduct.setProductPrice(Integer.parseInt(br.readLine()));
            System.out.println("브랜드 : ");
            updateProduct.setProductBrand(br.readLine());
            System.out.println("원산지 : ");
            updateProduct.setProductOrign(br.readLine());
            System.out.println("제조사 : ");
            updateProduct.setManufactor(br.readLine());
            System.out.println("판매상태 : (ON_SALE, OFF_SALE)");
            updateProduct.setPrductStatus(SalesStatus.valueOf(br.readLine()));

            System.out.println("-------------------------------------------------------------");
            System.out.println("보조 메뉴 : 1.Ok | 2.Cancel");
            System.out.print("메뉴 선택 : ");
            String menuNum = br.readLine();

            if(menuNum.equals("1")){
                state = productService.updateProductByProductCode(productCode,updateProduct);
                System.out.println("상품 정보 수정 완료");

                Product product = productService.getProductByProductCode(productCode);
                System.out.println("-- 상품 정보 --");
                System.out.printf("상품코드: %s\n", product.getProductCode());
                System.out.printf("상품명: %s\n", product.getProductName());
                System.out.printf("판매가격: %d\n", product.getProductPrice());
                System.out.printf("브랜드: %s\n", product.getProductBrand());
                System.out.printf("원산지: %s\n", product.getProductOrign());
                System.out.printf("제조사: %s\n", product.getManufactor());
                System.out.printf("카테고리 코드: %s\n", product.getCategoryCode());

            }
            else{
                System.out.println("업데이트 취소");
            }


        }catch (IOException i){
            i.printStackTrace();
        }
        return state;



    }

}

