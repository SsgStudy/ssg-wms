package service;

import util.enumcollect.MemberEnum;
import vo.Member;

import java.io.BufferedReader;
import java.util.List;

/**
 * The interface Member service.
 */
public interface MemberService {

    /**
     * Gets member list.
     *
     * @return the member list
     */
    List<Member> getMemberList();

    /**
     * Gets member by user id.
     *
     * @param userId the user id
     * @return the member by user id
     */
    Member getMemberByUserId(String userId);

    /**
     * Gets member role by id.
     *
     * @param userId the user id
     * @return the member role by id
     */
    MemberEnum getMemberRoleById(String userId);

    /**
     * Update member info boolean.
     *
     * @param no      the no
     * @param newId   the new id
     * @param newName the new name
     * @return the boolean
     */
    boolean updateMemberInfo(int no, String newId, String newName);

    /**
     * Gets member by no.
     *
     * @param no the no
     * @return the member by no
     */
    Member getMemberByNo(int no);

    /**
     * Gets member update.
     *
     * @param no      the no
     * @param newId   the new id
     * @param newName the new name
     */
    void getMemberUpdate(int no, String newId, String newName);
}
