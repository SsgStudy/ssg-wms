package controller;

import service.PurchaseServiceImpl;

import java.util.List;
import java.util.Scanner;

public class PurchaseController {
    static Scanner sc = new Scanner(System.in);
    static PurchaseServiceImpl service = new PurchaseServiceImpl();

    public static void main(String[] args) {
        service.integrateShopPurchases("", "", List.of(""));

    }


}
