package service;

import dao.MemberManagementDao;
import vo.Member;

import java.util.List;

public class MemberServicelmpl implements MemberService {

    MemberManagementDao memberManagementDao = new MemberManagementDao();

    public void MemberRead() {

        System.out.printf("%-4s%-20s%-16s%s\n", "no","Id","Name","Role","ADMIN");

        List<Member> members = memberManagementDao.readMember();
        for (Member member : members) {
            System.out.printf("%-4s%-20s%-16s%s\n", member.getMemberSeq(), member.getMemberId(), member.getMemberName(), member.getMemberRole());
        }
    }


}
