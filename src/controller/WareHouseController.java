package controller;

import dao.LoginManagementDAOImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import service.WareHouseService;
import util.MenuBoxPrinter;
import util.enumcollect.MemberEnum;
import vo.WareHouse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 이 클래스는 창고 관리를 담당하는 WareHouseController 컨트롤러입니다.
 * 주로 창고의 등록 및 조회등을 수행합니다.
 * 또한, 해당 클래스는 Singleton 패턴을 따르고 있어 하나의 인스턴스만을 생성하여 사용합니다.
 *
 * @author : 윤여빈
 */
public class WareHouseController {
    private LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
    private MemberEnum loginMemberRole;
    private String loginMemberId;
    private static WareHouseController instance;
    private static WareHouseService wareHouseService;
    private static Logger logger = Logger.getLogger(WareHouseController.class.getName());
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private List<WareHouse> wareHouseList = new ArrayList<>();

    /**
     * Instantiates a new Ware house controller.
     *
     * @param wareHouseService the warehouse service
     */
    public WareHouseController(WareHouseService wareHouseService) {
        this.wareHouseService = wareHouseService;
    }

    /**
     * Gets instance.
     *
     * @param wareHouseService the warehouse service
     * @return the instance
     */
    public static WareHouseController getInstance(WareHouseService wareHouseService) {
        if (instance == null) {
            instance = new WareHouseController(wareHouseService);
        }
        return instance;
    }

    /**
     * Menu.
     *
     * @throws IOException the io exception
     */
    public void menu() throws IOException {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
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

    /**
     * Register ware house.
     *<p>
     * 접근제한 : 창고관리자
     */
    public void registerWareHouse() {
        if (!(
                loginMemberRole == MemberEnum.ADMIN ||
                        loginMemberRole == MemberEnum.OPERATOR
        )) {
            System.out.println("해당 메뉴를 실행할 권한이 없습니다.\n관리자에게 문의해주세요...");
            return;
        }
        WareHouse wareHouse = new WareHouse();

        try {
            System.out.println("***신규 창고 등록***");
            System.out.print("\n➔ 창고 코드 지정 (예 : KR-CJU-01) : ");
            wareHouse.setWarehouseCode(br.readLine());
            System.out.print("\n➔ 창고 명 지정 (예 : 서울 중앙 창고) : ");
            wareHouse.setWarehouseName(br.readLine());
            System.out.print("\n➔ 창고 소재지(도시 국가) 지정 (예 : 노르웨이) : ");
            wareHouse.setWarehouseLocation(br.readLine());
            System.out.print("\n➔ 창고 종류 지정 (예 : 전자제품) : ");
            wareHouse.setWarehouseType(br.readLine());
            System.out.print("\n➔ 창고관리자 코드 입력 (예 : 6) : ");
            wareHouse.setMemberSeq(Integer.parseInt(br.readLine()));
            wareHouseService.registerWareHouse(wareHouse);
            System.out.printf("[%s의 등록이 완료되었습니다.]\n", wareHouse.getWarehouseName());

        } catch (IOException i) {
            i.printStackTrace();
        }


    }

    /**
     * View warehouse.
     *
     * @throws IOException the io exception
     */
    public void viewWareHouse() throws IOException {
        String[] menuItems = {
                "1. 전체 조회\t",
                "2. 창고 이름별 조회\t",
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
            case 5 -> {
                return;
            }
            default -> System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
        }
    }

    /**
     * Warehouse table.
     */
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

    /**
     * View warehouse by name.
     */
    public void viewWareHouseByName() {
        System.out.println("--".repeat(25));
        System.out.println("[창고 이름별 조회]");
        System.out.print("\n➔ 창고 이름 입력 (예 : 서울 중앙 창고) : ");
        try {
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
        } catch (IOException | SQLException i) {
            i.printStackTrace();
        }

    }

    /**
     * View warehouse by location.
     */
    public void viewWareHouseByLocation() {
        System.out.println("--".repeat(25));
        System.out.println("[창고 소재지별 조회]");
        System.out.print("\n➔ 창고 소재지 입력 (예 : 서울 중구) : ");
        try {
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
        } catch (IOException | SQLException i) {
            i.printStackTrace();
        }

    }

    /**
     * View warehouse by type.
     */
    public void viewWareHouseByType() {
        System.out.println("--".repeat(25));
        System.out.println("[창고 종류별 조회]");
        System.out.print("\n➔ 창고 종류 입력 (예 : 전자제품) : ");
        try {
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
        } catch (IOException | SQLException i) {
            i.printStackTrace();
        }
    }
}
