package serviceImpl;

import daoImpl.WareHouseDaoImpl;
import service.WareHouseService;
import vo.WareHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class WareHouseServiceImpl implements WareHouseService {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    WareHouseDaoImpl wareHouseDao = new WareHouseDaoImpl();
    List<WareHouse> wareHouseList = new ArrayList<>();
    private static Logger logger = Logger.getLogger(WareHouseServiceImpl.class.getName());


    public void wareHouseMain() throws IOException {
        System.out.println("--".repeat(25));
        System.out.println("[창고 관리]");
        System.out.println("--".repeat(25));
        System.out.println("1.창고 등록 | 2. 창고 조회");
        System.out.println("--".repeat(25));
        System.out.print("메뉴 선택 : ");
        try {
            int cmd = Integer.parseInt(br.readLine());
            switch (cmd) {
                case 1 -> registerWareHouse();
                case 2 -> viewWareHouse();
            }
            br.skip(br.lines().count());
        } catch (NumberFormatException e) {
            logger.info("숫자만 입력하세요.");
            e.printStackTrace();
            wareHouseMain();
        }
    }

    public void wareHouseTable() {//전체조회
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("[창고 목록]");
        System.out.println("--".repeat(25));
        System.out.printf("%7s | %12s | %10s | %6s | %3s\n", "창고코드", "창고명", "창고소재지", "창고종류", "관리자코드");
        System.out.println("--".repeat(25));
        wareHouseList = wareHouseDao.viewWareHouse();
        for (WareHouse wareHouse : wareHouseList) {
            System.out.printf("%7s | %12s | %9s | %6s |%3s\n",
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseName(),
                    wareHouse.getWarehouseLocation(),
                    wareHouse.getWarehouseType(),
                    wareHouse.getMemberSeq());
        }
        System.out.println("--".repeat(25));

    }

    @Override
    public void registerWareHouse() throws IOException {
        WareHouse wareHouse = new WareHouse();

        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("[신규 창고 등록]");
        System.out.println("--".repeat(25));
        System.out.printf("창고 코드 지정 : ");
        wareHouse.setWarehouseCode(br.readLine());
        System.out.printf("창고 명 지정 : ");
        wareHouse.setWarehouseName(br.readLine());
        System.out.printf("창고 소재지(도시 국가) 지정 : ");
        wareHouse.setWarehouseLocation(br.readLine());
        System.out.printf("창고 종류 지정 : ");
        wareHouse.setWarehouseType(br.readLine());
        wareHouse.setMemberSeq(wareHouse.getMemberSeq());
        System.out.printf("[%s의 등록이 완료되었습니다.]\n", wareHouse.getWarehouseName());
        System.out.println("창고관리자 코드 : " + wareHouse.getMemberSeq());
        wareHouseDao.registerWareHouse(wareHouse);
        wareHouseMain();
        br.skip(br.lines().count());

    }

    @Override
    public void viewWareHouse() throws IOException {
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("[창고 조회]");
        System.out.println("--".repeat(25));
        System.out.println("1.전체 조회 | 2.창고명별 조회 | 3.소재지별 조회 | 4.창고종류별 조회 | 5. 돌아가기");
        System.out.println("--".repeat(25));
        System.out.printf("메뉴 선택 : ");
        try {
            int cmd = Integer.parseInt(br.readLine().trim());
            switch (cmd) {
                case 1 -> wareHouseTable();
                case 2 -> viewWareHouseByName();
                case 3 -> viewWareHouseByLocation();
                case 4 -> viewWareHouseByType();
                case 5 -> wareHouseMain();
            }
        } catch (NumberFormatException e) {
            logger.info("숫자만 입력하세요.");
            e.printStackTrace();
            viewWareHouse();
        }
        viewWareHouse();
    }


    public void viewWareHouseByName() throws IOException {

        System.out.println("--".repeat(25));
        System.out.println("[창고 이름별 조회]");
        wareHouseList = wareHouseDao.viewWareHouseByNameMain();
        for (int i = 0; i < wareHouseList.size(); i++) {
            WareHouse wareHouse = wareHouseList.get(i);
            System.out.printf("%2d | %20s |\n",
                    i + 1,
                    wareHouse.getWarehouseName());
        }
        System.out.print("창고명 선택 : ");
        int selectedIndex = Integer.parseInt(br.readLine());
        WareHouse selectedWareHouse = wareHouseList.get(selectedIndex - 1);
        System.out.println("선택된 창고 정보:");
        System.out.println("창고명: " + selectedWareHouse.getWarehouseName());
        System.out.println("--".repeat(25));
        System.out.printf("%7s | %12s | %10s | %6s | %3s\n", "창고코드", "창고명", "창고소재지", "창고종류", "관리자코드");
        System.out.println("--".repeat(25));
        wareHouseList = wareHouseDao.viewWareHouseByName(selectedWareHouse.getWarehouseName());
        for (WareHouse wareHouse : wareHouseList) {
            System.out.printf("%7s | %12s | %9s | %6s | %3s\n",
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseName(),
                    wareHouse.getWarehouseLocation(),
                    wareHouse.getWarehouseType(),
                    wareHouse.getMemberSeq());
        }


    }

    public void viewWareHouseByLocation() throws IOException {
        System.out.println("--".repeat(25));
        System.out.println("[창고 소재지별 조회]");
        wareHouseList = wareHouseDao.viewWareHouseByLocationMain();
        for (int i = 0; i < wareHouseList.size(); i++) {
            WareHouse wareHouse = wareHouseList.get(i);
            System.out.printf("%2d | %15s |\n",
                    i + 1,
                    wareHouse.getWarehouseLocation());
        }
        System.out.print("소재지 선택 : ");
        int selectedIndex = Integer.parseInt(br.readLine());
        WareHouse selectedWareHouse = wareHouseList.get(selectedIndex - 1);
        System.out.println("선택된 창고 정보:");
        System.out.println("위치: " + selectedWareHouse.getWarehouseLocation());
        System.out.println("--".repeat(25));
        System.out.printf("%7s | %12s | %10s | %6s | %3s\n", "창고코드", "창고명", "창고소재지", "창고종류", "관리자코드");
        System.out.println("--".repeat(25));
        wareHouseList = wareHouseDao.viewWareHouseByLocation(selectedWareHouse.getWarehouseLocation());
        for (WareHouse wareHouse : wareHouseList) {
            System.out.printf("%7s | %12s | %9s | %6s | %3s\n",
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseName(),
                    wareHouse.getWarehouseLocation(),
                    wareHouse.getWarehouseType(),
                    wareHouse.getMemberSeq());
        }
    }

    public void viewWareHouseByType() throws IOException {

        System.out.println("--".repeat(25));
        System.out.println("[창고 종류별 조회]");
        wareHouseList = wareHouseDao.viewWareHouseByTypeMain();
        for (int i = 0; i < wareHouseList.size(); i++) {
            WareHouse wareHouse = wareHouseList.get(i);
            System.out.printf("%2d | %10s |\n",
                    i + 1,
                    wareHouse.getWarehouseType());
        }
        System.out.println("--".repeat(25));
        System.out.print("창고종류 선택 : ");
        int selectedIndex = Integer.parseInt(br.readLine());
        WareHouse selectedWareHouse = wareHouseList.get(selectedIndex - 1);
        System.out.println("선택된 창고 정보:");
        System.out.println("창고종류: " + selectedWareHouse.getWarehouseType());
        System.out.println("--".repeat(25));
        System.out.printf("%7s | %12s | %10s | %6s | %3s\n", "창고코드", "창고명", "창고소재지", "창고종류", "관리자코드");
        System.out.println("--".repeat(25));
        wareHouseList = wareHouseDao.viewWareHouseByType(selectedWareHouse.getWarehouseType());
        for (WareHouse wareHouse : wareHouseList) {
            System.out.printf("%7s | %12s | %9s | %6s | %3s\n",
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseName(),
                    wareHouse.getWarehouseLocation(),
                    wareHouse.getWarehouseType(),
                    wareHouse.getMemberSeq());
        }

    }
}
