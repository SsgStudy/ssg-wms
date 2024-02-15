package controller;

import dao.LoginManagementDAOImpl;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import service.IncomigService;
import util.MenuBoxPrinter;
import util.enumcollect.MemberEnum;
import vo.DetailedIncomingVO;
import vo.IncomingVO;

public class IncomingController {

    private static IncomingController instance;
    private LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
    private MemberEnum loginMemberRole;
    private String loginMemberId;

    private IncomigService incomingService;

    Scanner sc = new Scanner(System.in);

    public IncomingController(IncomigService incomingService) {
        this.incomingService = incomingService;

    }

    public static synchronized IncomingController getInstance(IncomigService incomingService) {
        if (instance == null) {
            instance = new IncomingController(incomingService);
        }
        return instance;
    }


    public List<IncomingVO> getAllIncomingProductsWithDetails() {
        return incomingService.getAllIncomingProductsWithDetails();
    }

    //입고 관리 선택 메뉴
    public void incomingProductMenu() throws Exception {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
        boolean continueMenu = true;
        printAllIncomingProductsWithDetails();

        while (continueMenu) {

            String[] menuItems = {
                    "1. 입고 조회\t",
                    "2. 입고 수정\t",
                    "3. 입고 승인\t",
                    "4. 메뉴 나가기\t\t\t"
            };
            MenuBoxPrinter.printMenuBoxWithTitle("입고 관리\t\t", menuItems);
            int manageChoice = sc.nextInt();

            switch (manageChoice) {
                case 1 -> selectFilterOption();
                case 2 -> {
                    System.out.print("\n➔ 수정할 입고 상품의 번호를 입력하세요 : ");
                    long seqToUpdate = sc.nextLong();
                    updateIncomingProduct(seqToUpdate);
                }
                case 3 -> {
                    System.out.print("➔ 승인할 입고 상품의 번호를 입력하세요 : ");
                    long seqToApprove = sc.nextLong();
                    approveIncomingProduct(seqToApprove);
                }
                case 4 -> {
                    return;
                }
                default -> System.out.println("옳지 않은 입력입니다.");
            }
        }
    }

    //조회 필터 메뉴
    private void selectFilterOption() throws Exception {
        if (!(
                loginMemberRole == MemberEnum.ADMIN ||
                loginMemberRole == MemberEnum.WAREHOUSE_MANAGER ||
                loginMemberRole == MemberEnum.OPERATOR
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        String[] menuItems = {
                "1. 전체 조회\t",
                "2. 년월 조회\t",
                "3. 기간 조회\t",
                "4. 상세 조회\t",
                "5. 메뉴 나가기\t\t\t"
        };
        MenuBoxPrinter.printMenuBoxWithTitle("입고 조회\t\t", menuItems);

        int filterChoice = sc.nextInt();

        switch (filterChoice) {
            case 1 -> printAllIncomingProductsWithDetails();
            case 2 -> promptForMonthlyIncomingProducts();
            case 3 -> promptForIncomingProductsByDateRange();
            case 4 -> promptForDetailedIncomingProduct();
            case 5 -> {
                return;
            }
            default -> System.out.println("옳지 않은 입력입니다.");
        }
    }

    ////////////////////////////////////////////////////////////////
    //기능 프롬프트
    public void updateIncomingProduct(long seq) throws Exception {
        if (!(
                loginMemberRole == MemberEnum.ADMIN ||
                        loginMemberRole == MemberEnum.WAREHOUSE_MANAGER
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        System.out.println("수정 사항 입력");
        System.out.print("\n➔ ZONE 코드 : ");
        String zoneCode = sc.next();
        System.out.print("➔ 수     량 : ");
        int count = sc.nextInt();
        System.out.print("➔ 매입  가격 : ");
        int price = sc.nextInt();

        try {
            incomingService.updateIncomingProductDetails(seq, zoneCode, count, price);
            System.out.println("성공적으로 수정하였습니다.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        printAllIncomingProductsWithDetails();
    }

    public void approveIncomingProduct(long seq) throws Exception {
        if (!(
                loginMemberRole == MemberEnum.ADMIN ||
                        loginMemberRole == MemberEnum.WAREHOUSE_MANAGER ||
                        loginMemberRole == MemberEnum.OPERATOR
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        try {
            incomingService.approveIncomingProduct(seq);
            System.out.println("성공적으로 승인되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        printAllIncomingProductsWithDetails();
    }

    private void promptForMonthlyIncomingProducts() throws Exception {
        System.out.print("\n➔ 년도와 월을 입력하세요 (예: 2023 5) : ");
        int year = sc.nextInt();
        int month = sc.nextInt();
        if (year > 0 && month >= 1 && month <= 12) {
            printIncomingProductsByMonth(year, month);
        } else {
            System.out.println("잘못된 년도 또는 월을 입력하셨습니다. 다시 입력해주세요.");
            promptForMonthlyIncomingProducts();
        }
    }

    private void promptForIncomingProductsByDateRange() throws Exception {
        System.out.println("\n➔ 시작 날짜와 종료 날짜를 입력하세요 (예: 2023-01-01 2023-01-31) : ");
        String startDateStr = sc.next();
        String endDateStr = sc.next();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            if (startDate.isAfter(endDate)) {
                System.out.println("시작 날짜가 종료 날짜보다 뒤에 있습니다. 다시 입력해주세요.");
            } else {
                printIncomingProductsByDateRange(startDate, endDate);
            }
        } catch (DateTimeParseException e) {
            System.out.println("잘못된 날짜 형식입니다. 올바른 형식(yyyy-MM-dd)으로 입력해주세요.");
        }
    }

    private void promptForDetailedIncomingProduct() throws Exception {
        System.out.println("\n➔ 조회할 입고 상품의 번호를 입력하세요 : ");
        long seq = sc.nextLong();
        DetailedIncomingVO detailedIncomingProduct = incomingService.getIncomingProductDetailsWithProductInfo(seq);
        printDetailedIncomingProduct(detailedIncomingProduct);
    }

    ////////////////////////////////////////////////////////////////
    //전체 조회
    public void printAllIncomingProductsWithDetails() throws Exception {
        List<IncomingVO> incomings = getAllIncomingProductsWithDetails();
        printIncomingProductsHeader();
        for (IncomingVO incoming : incomings) {
            printIncomingProduct(incoming);
        }
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    private void printIncomingProductsByMonth(int year, int month) throws Exception {
        List<IncomingVO> incomingProducts = incomingService.getIncomingProductsByMonth(year, month);

        printIncomingProductsHeader();
        for (IncomingVO incoming : incomingProducts) {
            printIncomingProduct(incoming);
        }
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    private void printIncomingProductsByDateRange(LocalDate startDate, LocalDate endDate) throws Exception {
        List<IncomingVO> incomingProducts = incomingService.getIncomingProductsByDateRange(startDate, endDate);

        printIncomingProductsHeader();
        for (IncomingVO incoming : incomingProducts) {
            printIncomingProduct(incoming);
        }
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    private void printDetailedIncomingProduct(DetailedIncomingVO detailedIncomingProduct) {
        printDetailedIncomingProductHeader();
        printDetailedIncomingProductInfo(detailedIncomingProduct);
    }

    ////////////////////////////////////////////////////////////////
    //중복 출력 메소드 분리
    private static void printDetailedIncomingProductInfo(DetailedIncomingVO detailedIncomingProduct) {
        String incomingDate =
                detailedIncomingProduct.getIncomingProductDate() != null ? detailedIncomingProduct.getIncomingProductDate()
                        .toString() : "N/A";
        String productManufactorDate =
                detailedIncomingProduct.getProductManufactorDate() != null ? detailedIncomingProduct.getProductManufactorDate()
                        .toString() : "N/A";
        System.out.printf("%-10d%-12s%-26s%-26s%-18s%-26s%-10d%-10d%-16s%-10s%-20s%-12d%-16s%-20s%-20s%-20s%-20s\n",
                detailedIncomingProduct.getIncomingProductSeq(),
                detailedIncomingProduct.getIncomingProductStatus(),
                detailedIncomingProduct.getProductCode(),
                incomingDate,
                detailedIncomingProduct.getIncomingProductType(),
                detailedIncomingProduct.getIncomingProductSupplierName(),
                detailedIncomingProduct.getIncomingProductCount(),
                detailedIncomingProduct.getIncomingProductPrice(),
                detailedIncomingProduct.getWarehouseCode(),
                detailedIncomingProduct.getZoneCode(),
                detailedIncomingProduct.getProductName(),
                detailedIncomingProduct.getProductPrice(),
                detailedIncomingProduct.getProductBrand(),
                detailedIncomingProduct.getProductOrigin(),
                detailedIncomingProduct.getProductManufactor(),
                detailedIncomingProduct.getProductStatus(),
                productManufactorDate);
        System.out.println(
                "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private static void printDetailedIncomingProductHeader() {
        System.out.printf("%-10s%-12s%-25s%-20s%-16s%-20s%-10s%-10s%-12s%-10s%-20s%-12s%-16s%-20s%-20s%-20s%-20s\n",
                "입고 번호", "입고 상태", "입고 상품 코드", "상품 입고 일자",
                "입고 종류", "입고 매입처", "입고 수량", "입고 단가", "입고 창고", "입고 구역",
                "상품명", "상품가격", "상품 브랜드", "상품 원산지", "생산자", "상품 상태", "생산 일자");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private void printIncomingProductsHeader() {
        System.out.printf("%-10s%-12s%-25s%-20s%-16s%-20s%-10s%-10s%-12s%-10s\n",
                "입고 번호", "입고 상태", "입고 상품 코드", "상품 입고 일자",
                "입고 종류", "입고 매입처", "입고 수량", "입고 단가", "입고 창고", "입고 구역");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private void printIncomingProduct(IncomingVO incoming) {
        String incomingDate = incoming.getIncomingProductDate() != null ? incoming.getIncomingProductDate().toString() : "N/A";
        System.out.printf("%-10d%-12s%-26s%-26s%-18s%-26s%-10d%-10d%-16s%-10s\n",
                incoming.getIncomingProductSeq(),
                incoming.getIncomingProductStatus(),
                incoming.getProductCode(),
                incomingDate,
                incoming.getIncomingProductType(),
                incoming.getIncomingProductSupplierName(),
                incoming.getIncomingProductCount(),
                incoming.getIncomingProductPrice(),
                incoming.getWarehouseCode(),
                incoming.getZoneCode());
    }


}
