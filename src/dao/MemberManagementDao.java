package dao;

import util.enumcollect.MemberEnum;
import vo.Member;

import java.util.List;

public interface MemberManagementDao {

   public List<Member> readMember();
   public Member getMemberByUserId(String userId);
   public Member getMemberByNo(int no);
   public MemberEnum getUserRoleById(String userId);
   public int update(int no, Member member);


}

