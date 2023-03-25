import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class Role {
    static String role;
    static Conference conference;

    static Connection MakeSqlConnection() {
        try {
            String url = "jdbc:mysql://10.0.13.88:3306/cmtdb";
            String username = "muano";
            String password = "52852Kh0Kz!N$";
            Connection connection = DriverManager.getConnection(url, username, password);

            return connection;

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
