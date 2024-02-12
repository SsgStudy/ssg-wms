package controller;

import service.LoginManagementService;
import service.LoginManagementServiceImpl;

public class MemberController {
    private final LoginManagementService loginService;

    public MemberController() {
        this.loginService = new LoginManagementServiceImpl();
    }

    public void logIn(String id, String password) {
        int loginResult = loginService.logIn(id, password);
        if (loginResult == 1) {
            System.out.println("로그인 성공");
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
