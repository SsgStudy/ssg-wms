package service;

import vo.Member;

import java.io.BufferedReader;

public interface MemberService {

    void MemberRead();
    void MemberUpdate();

   void updateMemberInfo(BufferedReader sc, Member memberToUpdate, int no);

}
