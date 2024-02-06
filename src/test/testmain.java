package test;

import java.sql.Connection;
import util.DbConnection;

public class testmain {

    public static void main(String[] args) {
        System.out.println("테스트 시작");
        Connection conn;

        try {
            conn = DbConnection.getConnection();
            System.out.println("접속 성공!");
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        } catch (Exception s) {
            s.printStackTrace();
        }
        System.out.println("테스트 종료");
    }
}

