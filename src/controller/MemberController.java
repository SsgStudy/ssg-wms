package controller;

import dao.LoginManagementDAOImpl;
import service.LoginManagementService;
import service.LoginManagementServiceImpl;

/**
 * 이 클래스는 로그인을 담당하는 MemberController 컨트롤러입니다.
 * 주로 사용자의 로그인 및 로그아웃과 같은 기능을 수행합니다.
 * 해당 클래스는 Singleton 패턴을 따르며, 하나의 인스턴스만을 생성하여 사용합니다.
 *
 * @author : 김소진
 */
public class MemberController {
    private final LoginManagementService loginService;
    private static MemberController instance;

    /**
     * Instantiates a new Member controller.
     */
    public MemberController() {
        this.loginService = new LoginManagementServiceImpl();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized MemberController getInstance() {
        if (instance == null) {
            instance = new MemberController();
        }
        return instance;
    }

    /**
     * Log in.
     *
     * @param id       the id
     * @param password the password
     */
    public void logIn(String id, String password) {
        int loginResult = loginService.logIn(id, password);
        if (loginResult == 1) {
            System.out.println("\n\n\n\n\n환영합니다 :)\n\n\n");
            LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
            System.out.println("ID: " + loginDao.getMemberId() + "\t권한: " + loginDao.getMemberRole()+"\n");

            MembrManagemntController.getInstance().updateLoginInfo();
        } else if (loginResult == 0) {
            System.out.println("로그인 실패");
        } else {
            System.out.println("로그인 처리 중 오류 발생");
        }
    }

    /**
     * Log out.
     */
    public void logOut() {
        loginService.logOut();
        System.out.println("로그아웃 되었습니다.");
    }
}
