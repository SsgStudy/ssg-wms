package dao;

import util.DbConnection;
import util.enumcollect.MemberEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginManagementDAOImpl {

    private static volatile LoginManagementDAOImpl instance;
    private String memberId;
    private MemberEnum memberRole;

    private LoginManagementDAOImpl() {
        try {
            Connection conn = DbConnection.getInstance().getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static LoginManagementDAOImpl getInstance() {
        if (instance == null) {
            synchronized (LoginManagementDAOImpl.class) {
                if (instance == null) {
                    instance = new LoginManagementDAOImpl();
                }
            }
        }
        return instance;
    }

    public int logIn(String id, String password) {
        String sql = "SELECT * FROM TB_MEMBER WHERE V_MEMBER_ID = ? AND V_MEMBER_PW = ?";
        try ( Connection conn = DbConnection.getInstance().getConnection();

                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                this.memberId = rs.getString("V_MEMBER_ID");
                this.memberRole = MemberEnum.valueOf(rs.getString("V_MEMBER_AUTH"));
                return 1; // 로그인 성공
            }
            return 0; // 로그인 실패
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // 오류 발생
        }
    }

    public void logOut() {
        this.memberId = null;
        this.memberRole = null;
    }

    public String getMemberId() {
        return memberId;
    }

    public MemberEnum getMemberRole() {
        return memberRole;
    }
}
