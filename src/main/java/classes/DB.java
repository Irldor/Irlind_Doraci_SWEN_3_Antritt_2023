package classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    //Singleton database instance
    private static DB instance;

    //Authentication data
    private static final String url = "jdbc:postgresql://localhost/postgres?currentschema=public";
    private static final String user = "postgres";
    private static final String pwd = "12345678";

    //Singleton instance method
    public static DB getInstance() {
        if (DB.instance == null) {
            DB.instance = new DB();
        }
        return DB.instance;
    }

    //Connection method
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, pwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
