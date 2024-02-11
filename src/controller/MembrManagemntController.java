package controller;

import service.MemberService;
import service.MemberServicelmpl;
import service.ProductService;
import service.ProductServiceImpl;
import util.enumcollect.MemberEnum;
import vo.Member;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class MembrManagemntController {
    private MemberService memberService = new MemberServicelmpl();
    BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));

    private String getCurrentUserId() {
        // 이 메소드는 현재 로그인한 사용자의 ID를 반환합니다.
        // 실제 시나리오에서는 사용자 인증 시스템과 통합하여 사용자의 실제 ID를 반환해야 합니다.
        return "operator007";
    }

    public void memberRead() throws IOException {
        String currentUserId = getCurrentUserId();
        MemberEnum currentUserRole = memberService.getUserRoleById(currentUserId);

        if (currentUserRole == MemberEnum.ADMIN) {
            List<Member> members = memberService.readMember();
            for (Member member : members) {
                System.out.printf("%-4d %-20s %-16s %s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(), member.getMemberRole());
            }
        } else {
            Member member = memberService.getMemberByUserId(currentUserId);
            if (member != null) {
                System.out.printf("%-4d %-20s %-16s %s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(), member.getMemberRole());
            } else {
                System.out.println("회원 정보를 찾을 수 없습니다.");
            }
        }
    }

    public void memberUpdate() throws IOException {
        String currentUserId = getCurrentUserId();
        MemberEnum currentUserRole = memberService.getUserRoleById(currentUserId);

        try {
            memberRead();
            int no;
            if(currentUserRole == MemberEnum.ADMIN){
                System.out.println("수정 원하는 회원 번호를 입력하세요:");
                no = Integer.parseInt(sc.readLine());
            } else {

                Member member = memberService.getMemberByUserId(currentUserId);
                if(member == null){
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

