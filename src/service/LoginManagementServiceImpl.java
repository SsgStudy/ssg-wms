package service;


import dao.LoginManagementDAOImpl;

import java.util.Scanner;


public class LoginManagementServiceImpl implements LoginManagementService {

    Scanner in = new Scanner(System.in);

    static final LoginManagementDAOImpl memberService = LoginManagementDAOImpl.getInstance();


    @Override
    public void logIn() {
        System.out.println("[로그인]");
        System.out.print("아이디 입력: ");
        String userid = in.nextLine();
        System.out.print("비밀번호 입력: ");
        String password = in.nextLine();
        System.out.println();

        while (true) {
            int state = memberService.logIn(userid, password);
            if (state == 1) {
                System.out.println("[로그인 성공]");
                System.out.println("ID : " + memberService.getMemberId());
                System.out.println("권한 : " + memberService.getMemberRole());
                break;
            } else {
                System.out.println("정보가 일치하지 않습니다. 다시 로그인 하세요.\n");
                logIn();
            }
        }
    }

    @Override
    public void logOut() {
        memberService.logOut();
        System.out.println("[로그아웃 성공]");

        //로그아웃 확인용
        /*System.out.println("ID : " + memberService.getMemberId());
        System.out.println("권한 : " + memberService.getMemberRole());*/
    }
}


