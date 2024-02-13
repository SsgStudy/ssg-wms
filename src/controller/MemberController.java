package controller;

import dao.LoginManagementDAOImpl;
import service.LoginManagementService;
import service.LoginManagementServiceImpl;

public class MemberController {
    private final LoginManagementService loginService;
    private static MemberController instance;

    public MemberController() {
        this.loginService = new LoginManagementServiceImpl();
    }
    public static synchronized MemberController getInstance() {
        if (instance == null) {
            instance = new MemberController();
        }
        return instance;
    }
    public void logIn(String id, String password) {
        int loginResult = loginService.logIn(id, password);
        if (loginResult == 1) {
            System.out.println("로그인 성공");
            // 로그인 성공 후 로그인한 사용자의 정보 출력
            LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
            System.out.println("로그인한 사용자 ID: " + loginDao.getMemberId());
            System.out.println("로그인한 사용자 권한: " + loginDao.getMemberRole());

            // 로그인 정보 업데이트
            MembrManagemntController.getInstance().updateLoginInfo();
        } else if (loginResult == 0) {
            System.out.println("로그인 실패");
        } else {
            System.out.println("로그인 처리 중 오류 발생");
        }
    }

    public void logOut() {
        loginService.logOut();
        System.out.println("로그아웃 되었습니다.");
    }
}
