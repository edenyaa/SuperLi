package Backend.DataAccessLayer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBUtil {

    public static Connection getConnection() throws SQLException {
        String dbPath = new File("transportation.db").getAbsolutePath();
        String url = "jdbc:sqlite:" + dbPath;
//        System.out.println("DB file ➜ " + dbPath);
        return DriverManager.getConnection(url);
    }
}
