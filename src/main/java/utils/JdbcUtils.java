package utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class JdbcUtils {
    private static final Connection conn;

    static {
        try {
            InputStream is = new FileInputStream(Objects.requireNonNull(JdbcUtils.class.getResource("/jdbc.properties")).getPath());
            Properties pros = new Properties();
            pros.load(is);

            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");

            Class.forName(driverClass);

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("连接Mysql成功！" + conn);
        } catch (Exception e) {
            System.out.println("连接Mysql失败！");
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
