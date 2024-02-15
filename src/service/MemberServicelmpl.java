package service;

import dao.MemberManagementDao;
import dao.MemberManagementDaoImpl;
import util.enumcollect.MemberEnum;
import vo.Member;

import java.util.List;

/**
 * The type Member servicelmpl.
 */
public class MemberServicelmpl implements MemberService {
    private static MemberServicelmpl instance;
    private MemberManagementDao memberDao;

    /**
     * Instantiates a new Member servicelmpl.
     */
    public MemberServicelmpl() {
        this.memberDao = MemberManagementDaoImpl.getInstance();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized MemberServicelmpl getInstance() {
        if (instance == null) {
            instance = new MemberServicelmpl();
        }
        return instance;
    }

    @Override
    public List<Member> getMemberList() {

        return memberDao.getMemberList();
    }

    @Override
    public Member getMemberByUserId(String userId) {

        return memberDao.getMemberByUserId(userId);
    }

    @Override
    public MemberEnum getMemberRoleById(String userId) {

        return memberDao.getMemberRoleById(userId);
    }

    @Override
    public boolean updateMemberInfo(int no, String newId, String newName) {
        Member memberToUpdate = memberDao.getMemberByNo(no);
        if (memberToUpdate != null) {
            memberToUpdate.setMemberId(newId);
            memberToUpdate.setMemberName(newName);
            return memberDao.updateMemberByNo(no, memberToUpdate) > 0;
        }
        return false;
    }

    @Override
    public Member getMemberByNo(int no) {
        return memberDao.getMemberByNo(no);
    }

    @Override
    public void getMemberUpdate(int no, String newId, String newName) {
        try {
            if (!updateMemberInfo(no, newId, newName)) {
                throw new Exception("회원 정보 업데이트 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
