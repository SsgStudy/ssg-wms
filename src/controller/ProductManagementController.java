package controller;

import dao.LoginManagementDAOImpl;
import service.ProductServiceImpl;
import service.ProductService;
import util.MenuBoxPrinter;
import util.enumcollect.MemberEnum;
import util.enumcollect.SalesStatus;
import vo.Category;
import vo.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * 이 클래스는 상품관리를 담당하는 ProductManagementController 컨트롤러입니다.
 * 주로 상품 등록, 목록 조회 및 수정과 같은 기능을 수행합니다.
 * 해당 클래스는 Singleton 패턴을 따르며, 하나의 인스턴스만을 생성하여 사용합니다.
 *
 * @author : 손혜지
 */
public class ProductManagementController {
    private LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
    private MemberEnum loginMemberRole;
    private String loginMemberId;
    private static ProductManagementController instance;
    private ProductService productService;
    private BufferedReader br;
    private String selectedCategoryCode;

    /**
     * Instantiates a new Product management controller.
     */
    private ProductManagementController() {
        this.productService = ProductServiceImpl.getInstance();
        this.br = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized ProductManagementController getInstance() {
        if (instance == null) {
            instance = new ProductManagementController();
        }
        return instance;
    }

    /**
     * Menu.
     */
    public void menu() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
        boolean continueMenu = true;
        while (continueMenu) {

            String[] menuItems = {
                    "1. 상품 등록\t",
                    "2. 목록 조회 및 수정\t\t\t",
                    "3. 메뉴 나가기\t\t\t"
            };
            MenuBoxPrinter.printMenuBoxWithTitle("상품 관리\t\t", menuItems);

            try {
                int manageChoice = Integer.parseInt(br.readLine().trim());

                switch (manageChoice) {
                    case 1 -> registerProduct();
                    case 2 -> getProductList();
                    case 3 -> {
                        return;
                    }
                    default -> System.out.println("옳지 않은 입력입니다.");

                }
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    /**
     * Show sub categories boolean.
     *
     * @return the boolean
     */
    public boolean showSubCategories() {
        try {
            System.out.println("등록할 상품의 카테고리를 먼저 입력해주세요.");

            System.out.println("\n[대분류별 카테고리 확인]");
            List<Category> mainCategories = productService.getMainCategories();
            for (Category category : mainCategories) {
                System.out.println(category.getCategoryCode() + ": " + category.getCategoryName());
            }

            System.out.print("\n➔ 대분류 카테고리 번호를 입력하세요 : ");

            int mainCategoryNumber = Integer.parseInt(br.readLine().trim());

            List<Category> subCategories = productService.getSubCategoriesByMainCategory(mainCategoryNumber);
            if (subCategories.isEmpty()) {
                System.out.println("선택한 대분류에 속하는 중분류 카테고리가 없습니다.");
                return false;
            }

            System.out.println("\n[중분류별 카테고리 확인]");
            for (Category category : subCategories) {
                System.out.println(category.getCategoryCode() + ": " + category.getCategoryName());
            }
            System.out.print("\n➔ 중분류 카테고리 번호를 입력하세요 : ");

            int subCategoryNumber = Integer.parseInt(br.readLine().trim());

            List<Category> detailCategories = productService.getDetailCategoriesBySubCategory(mainCategoryNumber, subCategoryNumber);
            if (detailCategories.isEmpty()) {
                System.out.println("선택한 중분류에 속하는 소분류 카테고리가 없습니다.");
                return false;
            }
            System.out.println("\n[소분류별 카테고리 확인]");
            for (Category category : detailCategories) {
                System.out.println(category.getCategoryCode() + ": " + category.getCategoryName());
            }
            System.out.print("\n➔ 소분류 카테고리 번호를 입력하세요 : ");

            String detailCategoryNumber = br.readLine().trim();

            List<String> lastThreeCharsList = detailCategories.stream()
                    .map(s -> s.getCategoryCode().substring(s.getCategoryCode().length() - 3))
                    .collect(Collectors.toList());

            boolean isValidDetailCategory = lastThreeCharsList.stream()
                    .anyMatch(c -> c.equals(detailCategoryNumber));

            if (!isValidDetailCategory) {
                System.out.println("선택한 소분류에 해당하는 카테고리가 존재하지 않습니다. 메인 메뉴로 돌아갑니다.");
                return false;
            }

            selectedCategoryCode = String.format("%03d", mainCategoryNumber) + "-"
                    + String.format("%03d", subCategoryNumber) + "-" + detailCategoryNumber;


        } catch (Exception e) {
            System.out.println("카테고리 조회 중 오류가 발생했습니다 - " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Register product.
     * <p>
     * 접근제한 : 창고관리자
     */
    public void registerProduct() {
        if (!(
                loginMemberRole == MemberEnum.ADMIN ||
                        loginMemberRole == MemberEnum.OPERATOR
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        try {
            Product product = new Product();

            boolean ctgContinue = showSubCategories();
            if (!ctgContinue) {
                return;
            }

            System.out.println("상품코드 입력 규칙은 아래와 같은 형태로 입력해주세요.");
            System.out.println("국가코드 - 식별자 - 제조시기\n" +
                    "ex)\n" +
                    "국가코드 : 3자리 -> 한국 경우 880\n" +
                    "갤럭시 s23 제품 식별자 : 5678\n" +
                    "제조 시기 : 2023년 5월 → 0523");

            System.out.print("\n➔ 상품코드 : ");
            String productCode = br.readLine();
            product.setProductCode(productCode);
            System.out.print("\n➔ 상품명 : ");
            String productName = br.readLine();
            product.setProductName(productName);
            System.out.print("\n➔ 판매가격 : ");
            int productPrice = Integer.parseInt(br.readLine().trim());
            product.setProductPrice(productPrice);
            System.out.print("\n➔ 브랜드 : ");
            String productBrand = br.readLine();
            product.setProductBrand(productBrand);
            System.out.print("\n➔ 원산지 : ");
            String productOrigin = br.readLine();
            product.setProductOrign(productOrigin);
            System.out.print("\n➔ 제조사 : ");
            String productManufactor = br.readLine();
            product.setManufactor(productManufactor);


            product.setCategoryCode(selectedCategoryCode);
            productService.insertProduct(product);

        } catch (Exception e) {
            System.out.println("상품 등록 중 오류가 발생했습니다 - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets product list.
     */
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

        String[] menuItems = {
                "1. 상품 상세보기\t\t\t",
                "2. 메뉴 나가기\t\t\t",
        };
        MenuBoxPrinter.printMenuBoxWithTitle("전체 상품 목록\t\t\t\t", menuItems);

        try {
            int selectednum = Integer.parseInt(br.readLine().trim());
            switch (selectednum) {
                case 1 -> {
                    System.out.print("\n➔ 상품번호 : ");
                    try {
                        String productcode = br.readLine().trim();
                        getProductByProductCode(productcode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case 2 -> {
                    return;
                }
                default -> System.out.println("잘못 입력하였습니다.");
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Gets product by product code.
     * <p>
     * 접근제한 : 창고관리자
     * <p>
     * @param productCode the product code
     */
    public void getProductByProductCode(String productCode) {

        Product product = productService.getProductByProductCode(productCode);

        if (product != null) {
            System.out.println("-- 상품 정보 --");
            System.out.printf("상품코드 : %s\n", product.getProductCode());
            System.out.printf("상품명 : %s\n", product.getProductName());
            System.out.printf("판매가격 : %d\n", product.getProductPrice());
            System.out.printf("브랜드 : %s\n", product.getProductBrand());
            System.out.printf("원산지 : %s\n", product.getProductOrign());
            System.out.printf("제조사 : %s\n", product.getManufactor());
            System.out.printf("카테고리 코드 : %s\n", product.getCategoryCode());

        } else {
            System.out.println("해당 상품번호의 상품이 존재하지 않습니다.");
        }

        System.out.println("--------------------------------");
        String[] menuItems2 = {
                "1. 상품 정보 수정\t\t\t\t",
                "2. 상세 조회 나가기\t\t\t\t",
        };
        MenuBoxPrinter.printMenuBoxWithTitle("상품 상세조회\t\t\t\t\t", menuItems2);

        try {
            int cmd = Integer.parseInt(br.readLine().trim());

            switch (cmd) {
                case 1 -> {
                    if (!(
                            loginMemberRole == MemberEnum.ADMIN ||
                                    loginMemberRole == MemberEnum.OPERATOR
                    )) {
                        System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
                    } else {
                        updateProductByProductCode(productCode);
                    }
                }
                case 2 -> getProductList();
            }
        } catch (IOException i) {
            i.printStackTrace();
        }

    }

    /**
     * Update product by product code int.
     *
     * @param productCode the product code
     * @return the int
     */
    public int updateProductByProductCode(String productCode) {
        int state = 0;

        System.out.println("상품 정보를 수정합니다.");

        try {

            Product updateProduct = new Product();
            System.out.print("\n➔ 상품명 : ");
            updateProduct.setProductName(br.readLine());
            System.out.print("\n➔ 판매가격 : ");
            updateProduct.setProductPrice(Integer.parseInt(br.readLine()));
            System.out.print("\n➔ 브랜드 : ");
            updateProduct.setProductBrand(br.readLine());
            System.out.print("\n➔ 원산지 : ");
            updateProduct.setProductOrign(br.readLine());
            System.out.print("\n➔ 제조사 : ");
            updateProduct.setManufactor(br.readLine());
            System.out.print("\n➔ 판매상태 (ON_SALE, OFF_SALE) : ");
            updateProduct.setPrductStatus(SalesStatus.valueOf(br.readLine()));

            String[] menuItems2 = {
                    "1. OK\t",
                    "2. Cancel\t",
            };
            MenuBoxPrinter.printMenuBoxWithTitle("상품 정보를 수정하시겠습니까?\t", menuItems2);

            int menuNum = Integer.parseInt(br.readLine().trim());

            if (menuNum == 1) {
                state = productService.updateProductByProductCode(productCode, updateProduct);
                System.out.println("상품 정보 수정 완료");

                Product product = productService.getProductByProductCode(productCode);
                System.out.println("-- 상품 정보 --");
                System.out.printf("상품코드 : %s\n", product.getProductCode());
                System.out.printf("상품명 : %s\n", product.getProductName());
                System.out.printf("판매가격 : %d\n", product.getProductPrice());
                System.out.printf("브랜드 : %s\n", product.getProductBrand());
                System.out.printf("원산지 : %s\n", product.getProductOrign());
                System.out.printf("제조사 : %s\n", product.getManufactor());
                System.out.printf("카테고리 코드 : %s\n", product.getCategoryCode());

            } else {
                System.out.println("업데이트 취소");
            }


        } catch (IOException i) {
            i.printStackTrace();
        }
        return state;
    }
}

