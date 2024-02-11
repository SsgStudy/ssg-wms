package service;

import util.enumcollect.MemberEnum;
import vo.Member;

import java.io.BufferedReader;
import java.util.List;

public interface MemberService {

    public List<Member> readMember();

    public Member getMemberByUserId(String userId);

    public MemberEnum getUserRoleById(String userId);

    public boolean updateMemberInfo(int no, String newId, String newName);

    public Member getMemberByNo(int no);

    public void memberUpdate(int no, String newId, String newName);



}
