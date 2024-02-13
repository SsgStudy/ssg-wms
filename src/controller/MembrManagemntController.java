package controller;

import dao.LoginManagementDAOImpl;
import service.MemberService;
import service.MemberServicelmpl;
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
    BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
    Scanner scanner = new Scanner(System.in);

    private MembrManagemntController() {
        updateLoginInfo(); // 로그인 정보 초기화
    }

    public void updateLoginInfo() {
        this.loginMemberRole = loginDao.getMemberRole();
        this.loginMemberId = loginDao.getMemberId();
        System.out.println("로그인 유지 정보 업데이트: ID = " + loginMemberId + ", Role = " + loginMemberRole);
    }

    public static synchronized MembrManagemntController getInstance() {
        if (instance == null) {
            instance = new MembrManagemntController();
        }
        return instance;
    }

    private String getCurrentUserId() {
        // 현재 로그인한 사용자의 ID를 반환합니다.
        return loginMemberId; // loginDao를 통해 실제 로그인한 사용자 ID를 반환하도록 수정
    }

    public void menu() throws IOException {
        boolean continueMenu = true;

        while (continueMenu) {

            System.out.println("loginId : " + loginMemberId);
            System.out.println("loginRole : " + loginMemberRole);
            System.out.println("1. 회원정보 수정\n 2. 메뉴 나가기");
            System.out.print("선택: ");
            int manageChoice = scanner.nextInt();

            switch (manageChoice) {
                case 1 -> memberUpdate();
                case 2 -> continueMenu = false;
                default -> System.out.println("옳지 않은 입력입니다.");
            }
        }

    }

    public void memberRead() throws IOException {
        String currentUserId = getCurrentUserId();
        MemberEnum currentUserRole = memberService.getMemberRoleById(currentUserId);

        if (currentUserRole == MemberEnum.ADMIN) {
            List<Member> members = memberService.getMemberList();
            for (Member member : members) {
                System.out.printf("%-4d %-20s %-16s %s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(),
                        member.getMemberRole());
            }
        } else {
            Member member = memberService.getMemberByUserId(currentUserId);
            if (member != null) {
                System.out.printf("%-4d %-20s %-16s %s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(),
                        member.getMemberRole());
            } else {
                System.out.println("회원 정보를 찾을 수 없습니다.");
            }
        }
    }

    public void memberUpdate() throws IOException {
        String currentUserId = getCurrentUserId();
        MemberEnum currentUserRole = memberService.getMemberRoleById(currentUserId);

        try {
            memberRead();
            int no;
            if (currentUserRole == MemberEnum.ADMIN) {
                System.out.println("수정 원하는 회원 번호를 입력하세요:");
                no = Integer.parseInt(sc.readLine());
            } else {

                Member member = memberService.getMemberByUserId(currentUserId);
                if (member == null) {
                    System.out.println("회원정보를 찾을 수 없습니다.");
                    return;
                }
                no = member.getMemberSeq();
                System.out.println("본인 정보를 수정합니다.");

            }

            System.out.println("새로운 아이디:");
            String newId = sc.readLine();
            System.out.println("새로운 이름:");
            String newName = sc.readLine();

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

