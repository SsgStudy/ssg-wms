package controller;


import java.util.List;
import service.IncomigService;
import vo.IncomingVO;

public class IncomingController {

    private IncomigService incomigService;

    public IncomingController(IncomigService incomigService) {
        this.incomigService = incomigService;
    }

    public List<IncomingVO> getAllIncomingProductsWithDetails() {
        return incomigService.getAllIncomingProductsWithDetails();
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
}
