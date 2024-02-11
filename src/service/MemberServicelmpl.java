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

    private MemberManagementDao memberDao = new MemberManagementDaoImpl();


    public List<Member> readMember() {

        return memberDao.readMember();
    }

    public Member getMemberByUserId(String userId) {
        // 특정 사용자 ID로 회원 정보 조회
        return memberDao.getMemberByUserId(userId);
    }

    public MemberEnum getUserRoleById(String userId) {
        // 사용자 ID로 사용자의 역할 조회
        return memberDao.getUserRoleById(userId);
    }

    public boolean updateMemberInfo(int no, String newId, String newName) {
        // 회원 정보 업데이트 로직
        Member memberToUpdate = memberDao.getMemberByNo(no);
        if (memberToUpdate != null) {
            memberToUpdate.setMemberId(newId);
            memberToUpdate.setMemberName(newName);
            return memberDao.update(no, memberToUpdate) > 0;
        }
        return false;
    }

    public Member getMemberByNo(int no) {
        // 회원 번호로 회원 정보 조회
        return memberDao.getMemberByNo(no);
    }

    public void memberUpdate(int no, String newId, String newName) {

        try{
            // 회원 정보 업데이트
            if (!updateMemberInfo(no, newId, newName)) {
                throw new Exception("회원 정보 업데이트 실패");
            }

        }catch (Exception e){
           e.printStackTrace();
        }
    }

}
