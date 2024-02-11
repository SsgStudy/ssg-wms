package controller;

import service.ProductServiceImpl;
import service.ProductService;
import util.enumcollect.SalesStatus;
import vo.Category;
import vo.Product;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementController {

    private ProductService productService = new ProductServiceImpl();
    BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
    private String selectedCategoryCode;
    public void showSubCategories(){
            try{
                System.out.println("등록할 상품의 카테고리를 먼저 입력해주세요.");
                System.out.println("[대분류별 카테고리 확인]");
                List<Category> mainCategories = productService.getMainCategories();
                for (Category category : mainCategories) {
                    System.out.println(category.getCategoryCode() + ": " + category.getCategoryName());
                }

                System.out.println("대분류 카테고리 번호를 입력하세요: ");
                int mainCategoryNumber = Integer.parseInt(sc.readLine().trim());

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
                int subCategoryNumber = Integer.parseInt(sc.readLine().trim());

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
                int detailCategoryNumber = Integer.parseInt(sc.readLine().trim());

                selectedCategoryCode = "00" +mainCategoryNumber + "-" + "00" + subCategoryNumber + "-" + "00" + detailCategoryNumber;

            }catch (Exception e){
                System.out.println("카테고리 조회 중 오류가 발생했습니다: " + e.getMessage());
                e.printStackTrace();
            }

        }

        public void registerProduct() {

            try{
                Product product = new Product();
                System.out.println("상품코드 입력 규칙은 아래와 같은 형태로 입력해주세요.");
                System.out.println("국가코드 - 식별자 - 제조시기\n" +
                        "ex)\n" +
                        "국가코드 : 3자리 -> 한국 경우 880\n" +
                        "갤럭시 s23 제품 식별자 : 5678\n" +
                        "제조 시기 : 2023년 5월 → 0523");

                System.out.println("상품코드 : ");
                String productCode = sc.readLine();
                product.setProductCode(productCode);
                System.out.println("상품명 : ");
                String productName = sc.readLine();
                product.setProductName(productName);
                System.out.println("판매가격 : ");
                int productPrice = Integer.parseInt(sc.readLine().trim());
                product.setProductPrice(productPrice);
                System.out.println("브랜드 : ");
                String productBrand = sc.readLine();
                product.setProductBrand(productBrand);
                System.out.println("원산지 : ");
                String productOrigin = sc.readLine();
                product.setProductOrign(productOrigin);
                System.out.println("제조사 : ");
                String productManufactor = sc.readLine();
                product.setManufactor(productManufactor);


                product.setCategoryCode(selectedCategoryCode);
                productService.productRegistration(product);

            }catch (Exception e) {
                System.out.println("상품 등록 중 오류가 발생했습니다: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

