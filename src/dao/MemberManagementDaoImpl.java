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

    private PreparedStatement pstmt;
    private static MemberManagementDaoImpl instance;
    static Connection conn;

    private MemberManagementDaoImpl() {
        try {
            conn = DbConnection.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized MemberManagementDaoImpl getInstance() {
        if (instance == null) {
            instance = new MemberManagementDaoImpl();
        }
        return instance;
    }


    public List<Member> getMemberList() {
        List<Member> members = new ArrayList<>();

        try {

            String sql = "SELECT * FROM TB_MEMBER";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Member member = new Member();
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

    public Member getMemberByUserId(String userId) {
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

    public MemberEnum getMemberRoleById(String userId) {
        MemberEnum userRole = null;
        String sql = "SELECT V_MEMBER_AUTH FROM TB_MEMBER WHERE V_MEMBER_ID = ?";
        try {
            if (conn == null || conn.isClosed()) {
                conn = DbConnection.getInstance().getConnection();
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String roleStr = rs.getString("V_MEMBER_AUTH");
                userRole = MemberEnum.valueOf(roleStr);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException s) {
            s.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userRole;
    }

    public int updateMemberByNo(int no, Member member) {
        int row = 0;

        String sql = new StringBuilder().append("UPDATE TB_MEMBER SET ")
                .append("V_MEMBER_ID=?, ").append("V_MEMBER_NM=?")
                .append("WHERE PK_MEMBER_SEQ=?").toString();

        try {

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setString(2, member.getMemberName());
            pstmt.setInt(3, member.getMemberSeq());

            row = pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return row;
    }


}
