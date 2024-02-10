package service;

import dao.MemberManagementDao;
import dao.MemberManagementDaoImpl;
import util.enumcollect.MemberEnum;
import vo.Member;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class MemberServicelmpl implements MemberService {

    private MemberManagementDaoImpl Memberdao = new MemberManagementDaoImpl();
    BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));

    private String getCurrentUserId() {

        return "operator006";

    }

    public void MemberRead() {

        // 현재 로그인한 사용자의 아이디와 권한을 가져옴
        String currentUserId = getCurrentUserId();
        MemberEnum currentUserRole = Memberdao.getUserRoleById(currentUserId);

        System.out.printf("%-4s%-20s%-16s%s\n", "no", "Id", "Name", "Role", "ADMIN");

        if (currentUserRole == MemberEnum.ADMIN) {
            List<Member> members = Memberdao.readMember();
            for (Member member : members) {
                System.out.printf("%-4s%-20s%-16s%s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(), member.getMemberRole());
            }

        } else {
            Member member = Memberdao.getMemberByUserId(currentUserId);
            if (member != null) {
                System.out.printf("%-4s%-20s%-16s%s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(), member.getMemberRole());
            }
        }

    }

    public void MemberUpdate() {

        // 현재 로그인한 사용자의 아이디를 가져옴
        String currentUserId = getCurrentUserId();
        MemberEnum currentUserRole = Memberdao.getUserRoleById(currentUserId);

        if (currentUserRole == MemberEnum.ADMIN) {
            MemberRead();

            try {

                System.out.println("수정원하는 회원번호를 선택하세요");
                int no = Integer.parseInt(sc.readLine());

                // 선택한 회원 번호의 정보를 조회합니다.
                Member memberToUpdate = Memberdao.getMemberByNo(no);

                if (memberToUpdate == null) {
                    System.out.println("해당 번호의 회원을 찾을 수 없습니다.");
                    return;
                }

                updateMemberInfo(sc, memberToUpdate, no);

            } catch (IOException e) {
                System.out.println("입력 중 오류가 발생했습니다.");
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("유효한 번호를 입력해주세요.");
            } catch (Exception e) {
                System.out.println("회원 정보 수정 중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        } else {
            //ADMIN이 아닌 경우
            MemberRead();

            try {
                Member memberToUpdate = Memberdao.getMemberByUserId(currentUserId);
                if (memberToUpdate != null) {
                    updateMemberInfo(sc, memberToUpdate, memberToUpdate.getMemberSeq());
                } else {
                    System.out.println("회원 정보를 찾을 수 없습니다.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateMemberInfo(BufferedReader sc, Member memberToUpdate, int no) {
        System.out.println("회원 정보를 수정");
        try {
            System.out.println("아이디 : ");
            String newId = sc.readLine();
            System.out.println("이름 : ");
            String newName = sc.readLine();

            memberToUpdate.setMemberId(newId);
            memberToUpdate.setMemberName(newName);

            // 데이터베이스에 회원 정보를 업데이트합니다.
            int updatedRows = Memberdao.update(no, memberToUpdate);

            if (updatedRows > 0) {
                System.out.println("회원 정보가 성공적으로 업데이트 되었습니다.");
            } else {
                System.out.println("회원 정보 업데이트에 실패했습니다.");
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }


}
