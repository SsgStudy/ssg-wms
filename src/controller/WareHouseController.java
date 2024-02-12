package controller;

import serviceImpl.WareHouseServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Logger;

public class WareHouseController {
    public static void main(String[] args) throws IOException {
        wareHouseMain();
    }
    static WareHouseServiceImpl wareHouseService = new WareHouseServiceImpl();
    private static Logger logger = Logger.getLogger(WareHouseController.class.getName());
    static Scanner sc = new Scanner(System.in);
    public static void wareHouseMain() throws IOException {
        System.out.println("--".repeat(25));
        System.out.println("[창고 관리]");
        System.out.println("--".repeat(25));
        System.out.println("1.창고 등록 | 2. 창고 조회");
        System.out.println("--".repeat(25));
        System.out.print("메뉴 선택 : ");
        try {
            int cmd =sc.nextInt();
            switch (cmd) {
                case 1 -> {
                    wareHouseService.registerWareHouse();
                }
                case 2 -> {
                    wareHouseService.viewWareHouse();
                }
            }
        } catch (NumberFormatException e) {
            logger.info("숫자만 입력하세요.");
            e.printStackTrace();
            wareHouseMain();
        }
    }
}
