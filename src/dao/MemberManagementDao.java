package dao;

import util.enumcollect.MemberEnum;
import vo.Member;

import java.util.List;

public interface MemberManagementDao {

   public List<Member> getMemberList();
   public Member getMemberByUserId(String userId);
   public Member getMemberByNo(int no);
   public MemberEnum getMemberRoleById(String userId);
   public int updateMemberByNo(int no, Member member);


}

