package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import service.WareHouseService;
import util.MenuBoxPrinter;
import vo.WareHouse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WareHouseController {
    private static WareHouseController instance;
    private static WareHouseService wareHouseService;
    private static Logger logger = Logger.getLogger(WareHouseController.class.getName());
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private List<WareHouse> wareHouseList = new ArrayList<>();

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
            String[] menuItems = {
                    "1. 창고 등록\t",
                    "2. 창고 조회\t",
                    "3. 나가기\t",
            };
            MenuBoxPrinter.printMenuBoxWithTitle("창고 관리\t", menuItems);

            try {
                int cmd = Integer.parseInt(br.readLine());
                switch (cmd) {
                    case 1 -> {
                        registerWareHouse();
                    }
                    case 2 -> {
                        viewWareHouse();
                    }
                    case 3 -> {
                        return;
                    }
                    default -> System.out.println("잘못된 입력입니다.");
                }
            } catch (NumberFormatException e) {
                logger.info("숫자만 입력하세요.");
            }
        }
    }

    public void registerWareHouse() {
        WareHouse wareHouse = new WareHouse();

        try{
            System.out.println("***신규 창고 등록***");
            System.out.print("\n➔ 창고 코드 지정 : ");
            wareHouse.setWarehouseCode(br.readLine());
            System.out.print("\n➔ 창고 명 지정 : ");
            wareHouse.setWarehouseName(br.readLine());
            System.out.print("\n➔ 창고 소재지(도시 국가) 지정 : ");
            wareHouse.setWarehouseLocation(br.readLine());
            System.out.print("\n➔ 창고 종류 지정 : ");
            wareHouse.setWarehouseType(br.readLine());
            System.out.print("\n➔ 창고관리자 코드 입력 : ");
            wareHouse.setMemberSeq(Integer.parseInt(br.readLine()));
            wareHouseService.registerWareHouse(wareHouse);
            System.out.printf("[%s의 등록이 완료되었습니다.]\n", wareHouse.getWarehouseName());

        }catch (IOException i){
            i.printStackTrace();
        }


    }

    public void viewWareHouse() throws IOException {
        String[] menuItems = {
                "1. 전체 조회\t",
                "2. 창고명별 조회\t",
                "3. 소재지별 조회\t",
                "4. 창고종류별 조회\t\t",
                "5. 메뉴 나가기\t\t",
        };
        MenuBoxPrinter.printMenuBoxWithTitle("창고 조회\t", menuItems);

        int cmd = Integer.parseInt(br.readLine().trim());
        switch (cmd) {
            case 1 -> wareHouseTable();
            case 2 -> viewWareHouseByName();
            case 3 -> viewWareHouseByLocation();
            case 4 -> viewWareHouseByType();
            case 5 -> {return;}
            default -> System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
        }
    }

    public void wareHouseTable() {

        try {
            System.out.println();
            System.out.println("--".repeat(25));
            System.out.println("[창고 목록]");
            System.out.println("--".repeat(25));
            System.out.printf("%7s | %12s | %10s | %6s | %3s\n", "창고코드", "창고명", "창고소재지", "창고종류", "관리자코드");
            System.out.println("--".repeat(25));
            wareHouseList = wareHouseService.viewWareHouse();
            for (WareHouse wareHouse : wareHouseList) {
                System.out.printf("%7s | %12s | %10s | %6s | %3s\n",
                        wareHouse.getWarehouseCode(),
                        wareHouse.getWarehouseName(),
                        wareHouse.getWarehouseLocation(),
                        wareHouse.getWarehouseType(),
                        wareHouse.getMemberSeq());
            }
            System.out.println("--".repeat(25));

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void viewWareHouseByName() {
        System.out.println("--".repeat(25));
        System.out.println("[창고 이름별 조회]");
        System.out.print("\n➔ 창고 이름 입력 : ");
        try{
            String name = br.readLine();
            wareHouseList = wareHouseService.viewWareHouseByName(name);
            if (!wareHouseList.isEmpty()) {
                for (WareHouse wareHouse : wareHouseList) {
                    System.out.printf("%7s | %12s | %10s | %6s | %3s\n",
                            wareHouse.getWarehouseCode(),
                            wareHouse.getWarehouseName(),
                            wareHouse.getWarehouseLocation(),
                            wareHouse.getWarehouseType(),
                            wareHouse.getMemberSeq());
                }
            } else {
                System.out.println("일치하는 창고 정보가 없습니다.");
            }
            System.out.println("--".repeat(25));
        }catch (IOException | SQLException i){
            i.printStackTrace();
        }

    }

    public void viewWareHouseByLocation(){
        System.out.println("--".repeat(25));
        System.out.println("[창고 소재지별 조회]");
        System.out.print("\n➔ 창고 소재지 입력 : ");
        try{
            String location = br.readLine();
            wareHouseList = wareHouseService.viewWareHouseByLocation(location);
            if (!wareHouseList.isEmpty()) {
                for (WareHouse wareHouse : wareHouseList) {
                    System.out.printf("%7s | %12s | %10s | %6s | %3s\n",
                            wareHouse.getWarehouseCode(),
                            wareHouse.getWarehouseName(),
                            wareHouse.getWarehouseLocation(),
                            wareHouse.getWarehouseType(),
                            wareHouse.getMemberSeq());
                }
            } else {
                System.out.println("일치하는 창고 정보가 없습니다.");
            }
            System.out.println("--".repeat(25));
        }catch (IOException | SQLException i){
            i.printStackTrace();
        }

    }

    public void viewWareHouseByType() {
        System.out.println("--".repeat(25));
        System.out.println("[창고 종류별 조회]");
        System.out.print("\n➔ 창고 종류 입력 : ");
        try{
            String type = br.readLine();
            wareHouseList = wareHouseService.viewWareHouseByType(type);
            if (!wareHouseList.isEmpty()) {
                for (WareHouse wareHouse : wareHouseList) {
                    System.out.printf("%7s | %12s | %10s | %6s | %3s\n",
                            wareHouse.getWarehouseCode(),
                            wareHouse.getWarehouseName(),
                            wareHouse.getWarehouseLocation(),
                            wareHouse.getWarehouseType(),
                            wareHouse.getMemberSeq());
                }
            } else {
                System.out.println("일치하는 창고 정보가 없습니다.");
            }
            System.out.println("--".repeat(25));
        }catch (IOException | SQLException i){
            i.printStackTrace();
        }

    }

}
