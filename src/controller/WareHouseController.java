package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import service.IncomigService;
import service.WareHouseService;
import service.WareHouseServiceImpl;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

public class WareHouseController {
    private static WareHouseController instance;
    private static WareHouseService wareHouseService;
    private static Logger logger = Logger.getLogger(WareHouseController.class.getName());
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static Scanner sc = new Scanner(System.in);
    public WareHouseController(WareHouseService wareHouseService) {
        this.wareHouseService = wareHouseService;
    }
    public static WareHouseController getInstance(WareHouseService wareHouseService) {
        if (instance == null) {
            instance = new WareHouseController(wareHouseService);
        }
        return instance;
    }

    public void menu() throws IOException {
        boolean running = true;
        while (running) {
            System.out.println("--".repeat(25));
            System.out.println("[창고 관리]");
            System.out.println("--".repeat(25));
            System.out.println("1.창고 등록 | 2. 창고 조회 | 3. 나가기");
            System.out.println("--".repeat(25));
            System.out.print("메뉴 선택 : ");

            try {
                int cmd = Integer.parseInt(br.readLine());
                switch (cmd) {
                    case 1 -> {
                        wareHouseService.registerWareHouse();
                    }
                    case 2 -> {
                        wareHouseService.viewWareHouse();
                    }
                    case 3 -> {
                        running = false;
                    }
                    default -> System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
                }
            } catch (NumberFormatException e) {
                logger.info("숫자만 입력하세요.");
            }
        }
    }

}
