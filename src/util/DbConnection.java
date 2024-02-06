package util;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {

    private static Connection conn;

    public static Connection getConnection() throws Exception {
        if (conn == null || conn.isClosed() || !conn.isValid(1)) {
            Properties prop = loadDatabaseProperties();
            String url = prop.getProperty("database.url");
            String user = prop.getProperty("database.user");
            String pw = prop.getProperty("database.password");
            String driverClass = prop.getProperty("database.driver");

            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, user, pw);
        }
        return conn;
    }

    private static Properties loadDatabaseProperties() throws Exception {
        Properties prop = new Properties();
        InputStream inputStream = DbConnection.class.getClassLoader().getResourceAsStream("config/database.properties");
        if (inputStream == null) {
            throw new Exception("Unable to find database.properties");
        }
        prop.load(inputStream);
        return prop;
    }

    public static void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
        conn = null;
    }
}