package dao;

import util.DbConnection;
import util.enumcollect.MemberEnum;
import vo.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MemberManagementDao {
    private static DbConnection instance;
    static Connection conn;
    private PreparedStatement pstmt;


    public MemberManagementDao() {

        try{

            conn = DbConnection.getConnection();

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
    
}
