package controller;


import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import service.IncomigService;
import vo.IncomingVO;

public class IncomingController {
    Scanner scanner = new Scanner(System.in);

    private IncomigService incomingService;

    public IncomingController(IncomigService incomingService) {
        this.incomingService = incomingService;
    }

    public List<IncomingVO> getAllIncomingProductsWithDetails() {
        return incomingService.getAllIncomingProductsWithDetails();
    }
    public void incomingProductMenu() throws Exception {
        printAllIncomingProductsWithDetails();
        System.out.println("관리할 입고 상품의 번호를 입력하세요:");
        long seq = scanner.nextLong();
        System.out.println("1. 수정\n2. 승인");
        System.out.print("선택: ");
        int manageChoice = scanner.nextInt();

        switch (manageChoice) {
            case 1 -> updateIncomingProduct(seq, scanner);
            case 2 -> approveIncomingProduct(seq);
            default -> System.out.println("옳지 않은 입력입니다.");
        }
    }

    public void printAllIncomingProductsWithDetails() {
        List<IncomingVO> incomings = getAllIncomingProductsWithDetails();

        System.out.printf("%-10s%-12s%-25s%-20s%-16s%-20s%-10s%-10s%-12s%-10s\n",
                "입고 번호", "입고 상태", "입고 상품 코드", "상품 입고 일자",
                "입고 종류", "입고 매입처", "입고 수량", "입고 단가", "입고 창고", "입고 구역");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (IncomingVO incoming : incomings) {
            String incomingDate = incoming.getIncomingProductDate() != null ? incoming.getIncomingProductDate().toString() : "N/A"; // null일 경우 "N/A"로 표시
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
    public void updateIncomingProduct(long seq, Scanner scanner) throws SQLException {
        System.out.println("수정할 ZONE 코드, 수량, 가격을 입력하세요 (공백으로 구분):");
        String zoneCode = scanner.next();
        int count = scanner.nextInt();
        int price = scanner.nextInt();

        incomingService.updateIncomingProductDetails(seq, zoneCode, count, price);
        printAllIncomingProductsWithDetails();
    }
    public void approveIncomingProduct(long seq) throws Exception {
        incomingService.approveIncomingProduct(seq);
        printAllIncomingProductsWithDetails();
    }

}
