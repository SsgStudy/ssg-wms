package service;

import util.enumcollect.MemberEnum;
import vo.Member;

import java.io.BufferedReader;
import java.util.List;

public interface MemberService {

    public List<Member> getMemberList();

    public Member getMemberByUserId(String userId);

    public MemberEnum getMemberRoleById(String userId);

    public boolean updateMemberInfo(int no, String newId, String newName);

    public Member getMemberByNo(int no);

    public void getMemberUpdate(int no, String newId, String newName);



}
