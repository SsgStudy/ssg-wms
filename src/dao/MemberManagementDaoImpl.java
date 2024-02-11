package dao;

import util.DbConnection;
import util.enumcollect.MemberEnum;
import vo.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberManagementDaoImpl implements MemberManagementDao {
    private static DbConnection instance;
    static Connection conn;
    private PreparedStatement pstmt;


    public MemberManagementDaoImpl() {

        try{

            conn = DbConnection.getInstance().getConnection();

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    public List<Member> readMember() {
        List<Member> members = new ArrayList<>();

        try {

            String sql = "SELECT * FROM TB_MEMBER";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Member member =  new Member();
                member.setMemberSeq(rs.getInt("PK_MEMBER_SEQ"));
                member.setMemberId(rs.getString("V_MEMBER_ID"));
                member.setMemberName(rs.getString("V_MEMBER_NM"));
                member.setMemberRole(MemberEnum.valueOf(rs.getString("V_MEMBER_AUTH")));
                members.add(member);
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return members;

    }

    public Member getMemberByUserId(String userId){
        Member member = null;
        String sql = new StringBuilder().append("SELECT * FROM TB_MEMBER ")
                .append("WHERE V_MEMBER_ID = ?").toString();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                member = new Member();
                member.setMemberSeq(rs.getInt("PK_MEMBER_SEQ"));
                member.setMemberId(rs.getString("V_MEMBER_ID"));
                member.setMemberName(rs.getString("V_MEMBER_NM"));
                member.setMemberRole(MemberEnum.valueOf(rs.getString("V_MEMBER_AUTH")));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }

    public Member getMemberByNo(int no) {
        Member member = null;
        String sql = "SELECT * FROM TB_MEMBER WHERE PK_MEMBER_SEQ=?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, no);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                member = new Member();
                member.setMemberSeq(rs.getInt("PK_MEMBER_SEQ"));
                member.setMemberId(rs.getString("V_MEMBER_ID"));
                member.setMemberName(rs.getString("V_MEMBER_NM"));
                member.setMemberRole(Enum.valueOf(MemberEnum.class, rs.getString("V_MEMBER_AUTH")));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }

    public MemberEnum getUserRoleById(String userId) {
        MemberEnum userRole = null;
        String sql = new StringBuilder().append("SELECT V_MEMBER_AUTH ")
                .append("FROM TB_MEMBER WHERE V_MEMBER_ID = ?").toString();

        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                String roleStr = rs.getString("V_MEMBER_AUTH");
                userRole = MemberEnum.valueOf(roleStr);
            }

        }catch (SQLException s){
            s.printStackTrace();
        }

        return userRole;

    }
    public int update(int no, Member member){
        int row=0;

        String sql = new StringBuilder().append("UPDATE TB_MEMBER SET ")
               .append("V_MEMBER_ID=?, ").append("V_MEMBER_NM=?")
                .append("WHERE PK_MEMBER_SEQ=?").toString();

        try{

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId() );
            pstmt.setString(2,member.getMemberName());
            pstmt.setInt(3,member.getMemberSeq());

            row = pstmt.executeUpdate();
            pstmt.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return row;
    }


}
