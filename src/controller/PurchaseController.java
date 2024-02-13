package controller;

import service.PurchaseServiceImpl;

import java.util.List;
import java.util.Scanner;

public class PurchaseController {
    static Scanner sc = new Scanner(System.in);
    static PurchaseServiceImpl service = new PurchaseServiceImpl();

    public static void main(String[] args) {
        menu();
    }

    public static void menu() {
        System.out.println("1. 주문 수집하기 | 2. 주문 조회하기 | 3. 주문 확정하기 | 4. 주문 클레임 수집하기");
        String ch = sc.nextLine();

        switch (ch) {
            case "1" -> {
                // 빈 상태 -> 신규 주문으로 변경
                service.integrateShopPurchases("", "", List.of(""));
                menu();
            }
            case "2" -> {
                service.readAllPurchases();
                menu();
            }
            case "3" -> {
                service.updatePurchaseToConfirmed();
                menu();
            }
            case "4" -> {
                service.integrateShopClaims();
                service.updatePurchaseToCancel();
                menu();
            }

        }


    }

}
