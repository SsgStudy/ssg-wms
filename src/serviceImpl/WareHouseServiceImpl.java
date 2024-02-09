package serviceImpl;

import daoImpl.WareHouseDaoImpl;
import service.WareHouseService;
import vo.WareHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WareHouseServiceImpl implements WareHouseService {
    BufferedReader sc= new BufferedReader(new InputStreamReader(System.in));
    WareHouseDaoImpl wareHouseDao = new WareHouseDaoImpl();
    List<WareHouse> wareHouseList = new ArrayList<>();

    public void wareHouseRunner() throws IOException {
        wareHouseMain();

    }
    public void wareHouseMain() throws IOException {
        System.out.println("--".repeat(25));
        System.out.println("[창고 관리]");
        System.out.println("--".repeat(25));
        System.out.println("1.창고 등록 | 2. 창고 조회");
        System.out.println("--".repeat(25));
        System.out.println("메뉴 선택 : ");
        int cmd = Integer.parseInt(sc.readLine().trim());
        switch (cmd){
            case 1 -> registerWareHouse();
            case 2 -> viewWareHouse();
        }
    }

    public void wareHouseTable() {//전체조회
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("[창고 목록]");
        System.out.println("--".repeat(25));
        System.out.printf("%7s | %8s | %10s | %6s\n", "창고코드", "창고명", "창고소재지", "창고종류");
        System.out.println("--".repeat(25));
        wareHouseList = wareHouseDao.viewWareHouse();
        for (WareHouse wareHouse : wareHouseList) {
            System.out.printf("%7s | %10s | %9s | %6s\n",
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseCode(),
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
        wareHouse.setWarehouseCode(sc.readLine());
        System.out.printf("창고 명 지정 : ");
        wareHouse.setWarehouseName(sc.readLine());
        System.out.printf("창고 소재지(도시 국가) 지정 : ");
        wareHouse.setWarehouseLocation(sc.readLine());
        System.out.printf("창고 종류 지정 : ");
        wareHouse.setWarehouseType(sc.readLine());
        System.out.printf("창고관리자 코드 : ");
        wareHouse.setMemberSeq(sc.read());
        System.out.printf("[%s의 등록이 완료되었습니다.]", wareHouse.getWarehouseName());
        wareHouseDao.registerWareHouse(wareHouse);
        wareHouseMain();

    }

    @Override
    public void viewWareHouse() throws IOException {
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("1.전체 조회 | 2.창고명별 조회 | 3.소재지별 조회 | 4.창고종류별 조회 | 5. 돌아가기");
        System.out.println("--".repeat(25));
        System.out.printf("메뉴 선택 : ");
        int cmd = Integer.parseInt(sc.readLine().trim());
        switch (cmd) {
            case 1 -> wareHouseTable();
            case 2 -> viewWareHouseByName();
            case 3 -> viewWareHouseByLocation();
            case 4 -> viewWareHouseByType();
            case 5 -> wareHouseMain();
        }
        System.out.println("--".repeat(25));
        viewWareHouse();


    }

    public void viewWareHouseByName() throws IOException {
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("창고명 검색 : ");
        String name = sc.readLine();
        System.out.println("--".repeat(25));
        System.out.printf("%7s | %8s | %10s | %6s | %3s\n", "창고코드", "창고명", "창고소재지", "창고종류", "관리자코드");
        System.out.println("--".repeat(25));
        wareHouseList = wareHouseDao.viewWareHouseByName(name);
        for (WareHouse wareHouse : wareHouseList) {
            System.out.printf("%7s | %10s | %9s | %6s | %3s\n",
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseName(),
                    wareHouse.getWarehouseLocation(),
                    wareHouse.getWarehouseType(),
                    wareHouse.getMemberSeq());
        }
        System.out.println("--".repeat(25));
    }

    public void viewWareHouseByLocation() throws IOException {
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("소재지 검색 : ");
        String location = sc.readLine();
        System.out.println("--".repeat(25));
        System.out.printf("%7s | %8s | %10s | %6s | %3s\n", "창고코드", "창고명", "창고소재지", "창고종류", "관리자코드");
        System.out.println("--".repeat(25));
        wareHouseList = wareHouseDao.viewWareHouseByLocation(location);
        for (WareHouse wareHouse : wareHouseList) {
            System.out.printf("%7s | %10s | %9s | %6s | %3s\n",
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseName(),
                    wareHouse.getWarehouseLocation(),
                    wareHouse.getWarehouseType(),
                    wareHouse.getMemberSeq());
        }
        System.out.println("--".repeat(25));
    }

    public void viewWareHouseByType() throws IOException {
        System.out.println();
        System.out.println("--".repeat(25));
        System.out.println("창고종류 검색 : ");
        String type = sc.readLine();
        System.out.println("--".repeat(25));
        System.out.printf("%7s | %8s | %10s | %6s | %3s\n", "창고코드", "창고명", "창고소재지", "창고종류", "관리자코드");
        System.out.println("--".repeat(25));
        wareHouseList = wareHouseDao.viewWareHouseByType(type);
        for (WareHouse wareHouse : wareHouseList) {
            System.out.printf("%7s | %10s | %9s | %6s | %3s\n",
                    wareHouse.getWarehouseCode(),
                    wareHouse.getWarehouseName(),
                    wareHouse.getWarehouseLocation(),
                    wareHouse.getWarehouseType(),
                    wareHouse.getMemberSeq());
        }
        System.out.println("--".repeat(25));
    }


    @Override
    public void updateWareHouse() {

    }
}
