package test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import util.DbConnection;

public class Main {

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // DbConnection 인스턴스를 통해 데이터베이스 연결을 얻음
            conn = DbConnection.getInstance().getConnection();

            stmt = conn.createStatement();

            String sql = "SELECT * FROM TB_CATEGORY";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String ctgCd = rs.getString("V_CATEGORY_CD");
                String name = rs.getString("V_CATEGORY_NM");
                String ctgPCd = rs.getString("V_CATEGORY_PARENT_CD");
                System.out.println("ctgCd"+ ctgCd + "name" + name + "pcd" + ctgPCd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 자원 해제
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
