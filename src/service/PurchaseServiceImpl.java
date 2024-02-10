package service;

import dao.PurchaseDAOImpl;
import vo.PurchaseVO;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PurchaseServiceImpl implements PurchaseService{
    static Scanner sc = new Scanner(System.in);
    private PurchaseDAOImpl dao = new PurchaseDAOImpl();

    @Override
    public void integrateShopPurchases(String startDate, String endDate, List<String> shopName) {
        System.out.println("주문 수집 일자를 입력해주세요 (YYYY-mm-DD YYYY-mm-DD)");
        String date = sc.nextLine();
        String[] dates = date.split(" ");

        System.out.println("쇼핑몰 리스트");
        List<String> shopList = dao.findShopName();
        IntStream.rangeClosed(1, shopList.size())
                .forEach(idx-> System.out.println(idx + ". " + shopList.get(idx - 1)));

        System.out.println("수집할 쇼핑몰의 번호를 입력해주세요. (1 2 3)");
        List<Integer> selectedShopIndexes = List.of(sc.nextLine().split(" "))
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<String> selectedShopNames = selectedShopIndexes.stream()
                .map(idx -> shopList.get(idx - 1))
                .collect(Collectors.toList());

        List<PurchaseVO> purchaseList = dao.findByDateAndShop(dates[0], dates[1], selectedShopNames);

        System.out.println("주문 리스트");
        System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-10s %-10s %n",
                "주문 상태", "주문 수집일", "주문 번호", "쇼핑몰", "상품명", "브랜드", "구매자", "연락처 번호", "주문 수량", "판매 금액");

        System.out.println("----------------------------------------------------------------------------------------------------------------");

        purchaseList.forEach(purchase -> {
            System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-10d %-10d %n",
                    purchase.getShopPurchaseStatus(),
                    purchase.getShopPurchaseDate().toString(),
                    purchase.getShopPurchaseDetailSeq(),
                    purchase.getShopName(),
                    purchase.getProductName(),
                    purchase.getProductBrand(),
                    purchase.getShopPurchaseName(),
                    purchase.getShopPurchaseTel(),
                    purchase.getProductCnt(),
                    purchase.getProductCnt() * purchase.getProductPrice());
        });

        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    @Override
    public void updateOrderToConfirmed() {

    }

}