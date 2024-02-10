package test;

import controller.OutgoingController;
import dao.OutgoingDAOImpl;
import service.OutgoingService;
import service.OutgoingServiceImpl;

public class OutgoingMain {

    public static void main(String[] args) {
        // DAO 객체 생성
        OutgoingDAOImpl outgoingDAO = new OutgoingDAOImpl();

        // 서비스 객체 생성 및 DAO 객체 주입
        OutgoingServiceImpl outgoingService = new OutgoingServiceImpl(outgoingDAO);

        // 컨트롤러 객체 생성 및 서비스 객체 주입
        OutgoingController outgoingController = new OutgoingController(outgoingService);

        // 출고 관리 메뉴 실행
        outgoingController.outgoingProductMenu();
    }
}
