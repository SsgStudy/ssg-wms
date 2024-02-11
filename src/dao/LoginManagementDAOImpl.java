package dao;

import lombok.Getter;
import util.DbConnection;
import util.enumcollect.MemberEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginManagementDAOImpl implements LoginManagementDAO {

    private static LoginManagementDAOImpl instance;

    private LoginManagementDAOImpl() {
    }

    public static LoginManagementDAOImpl getInstance() {
        if (instance == null) {
            instance = new LoginManagementDAOImpl();
        }
        return instance;
    }


    @Getter
    private String memberId;
    @Getter
    private MemberEnum memberRole;

    Connection conn;

    {
        try {
            conn = DbConnection.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int logIn(String id, String password) {
        int state = 0;
        String sql = new StringBuilder("SELECT * FROM TB_MEMBER WHERE V_MEMBER_ID = ? and V_MEMBER_PW = ?").toString();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, password);


            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                memberId = rs.getString("V_MEMBER_ID");
                memberRole = MemberEnum.valueOf(rs.getString("V_MEMBER_AUTH"));
                state = 1;
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            state = -1;
        }
        return state;
    }

    @Override
    public void logOut() {
        memberId = null;
        memberRole = null;
    }
}
