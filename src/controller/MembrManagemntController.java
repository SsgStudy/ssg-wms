package controller;

import dao.LoginManagementDAOImpl;
import service.MemberService;
import service.MemberServicelmpl;
import util.MenuBoxPrinter;
import util.enumcollect.MemberEnum;
import vo.Member;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MembrManagemntController {
    private static MembrManagemntController instance;
    private MemberService memberService = MemberServicelmpl.getInstance();
    private LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();
    private MemberEnum loginMemberRole;
    private String loginMemberId;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private MembrManagemntController() {
        updateLoginInfo();
    }

    public void updateLoginInfo() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
    }

    public static synchronized MembrManagemntController getInstance() {
        if (instance == null) {
            instance = new MembrManagemntController();
        }
        return instance;
    }

    private String getCurrentUserId() {
        return loginMemberId;
    }

    public void menu() throws IOException {
        boolean continueMenu = true;

        while (continueMenu) {

            System.out.println("loginId : " + loginMemberId);
            System.out.println("loginRole : " + loginMemberRole);
            String[] menuItems = {
                    "1. 회원 정보 수정\t\t\t\t",
                    "2. 메뉴 나가기\t\t\t"
            };
            MenuBoxPrinter.printMenuBoxWithTitle("회원 관리\t\t",menuItems);
            int manageChoice = Integer.parseInt(br.readLine());

            switch (manageChoice) {
                case 1 -> memberUpdate();
                case 2 -> {
                    return;
                }
                default -> System.out.println("옳지 않은 입력입니다.");
            }
        }

    }

    public void memberRead() throws IOException {

        if (loginMemberRole == MemberEnum.ADMIN) {
            List<Member> members = memberService.getMemberList();
            for (Member member : members) {
                System.out.printf("%-4d %-20s %-16s %s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(),
                        member.getMemberRole());
            }
        } else {
            Member member = memberService.getMemberByUserId(loginMemberId);
            if (member != null) {
                System.out.printf("%-4d %-20s %-16s %s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(),
                        member.getMemberRole());
            } else {
                System.out.println("회원 정보를 찾을 수 없습니다.");
            }
        }
    }

    public void memberUpdate() {

        try {
            memberRead();
            int no;
            if (loginMemberRole == MemberEnum.ADMIN) {
                System.out.print("\n➔ 수정 원하는 회원 번호를 입력하세요 : ");
                no = Integer.parseInt(br.readLine());
                System.out.println("no = " + no);

            } else {

                System.out.println(loginMemberId);
                Member member = memberService.getMemberByUserId(loginMemberId);
                if (member == null) {
                    System.out.println("회원정보를 찾을 수 없습니다.");
                    return;
                }
                no = member.getMemberSeq();
                System.out.println("본인 정보를 수정합니다.");

            }

            System.out.print("\n➔ 새로운 아이디:");
            String newId = br.readLine();
            System.out.print("➔ 새로운 이름:");
            String newName = br.readLine();

            boolean isUpdated = memberService.updateMemberInfo(no, newId, newName);
            if (isUpdated) {
                System.out.println("회원 정보가 성공적으로 업데이트 되었습니다.");
            } else {
                System.out.println("회원 정보 업데이트에 실패했습니다.");
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println("입력 과정에서 오류가 발생했습니다. 올바른 번호를 입력해주세요.");
        } catch (Exception e) {
            System.out.println("회원 정보 업데이트 중 예상치 못한 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }


}

