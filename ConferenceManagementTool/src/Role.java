import java.sql.*;

public abstract class Role {
    public String role;
    public int conference;

    public static Connection MakeSqlConnection() throws SQLException {
        String url = "jdbc:mariadb://10.0.5.69:3306/cmtdb";
        String username = "muano";
        String password = "52852Kh0Kz!N$";
        Connection connection = DriverManager.getConnection(url, username, password);

        System.out.println("Connected succussfully");

        return connection;
    }
}
