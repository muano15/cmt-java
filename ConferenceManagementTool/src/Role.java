import java.sql.*;
import org.json.*;

public abstract class Role {
    public String role;
    public int conference;

    public static Connection MakeSqlConnection() throws SQLException {
        String url = "jdbc:mysql://10.0.8.63:3306/cmtdb";
        String username = "muano";
        String password = "52852Kh0Kz!N$";
        Connection connection = DriverManager.getConnection(url, username, password);

        System.out.println("connection to SQL Server is established\n");

        return connection;
    }

    public static String Login(String email, String pword) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = MakeSqlConnection();

            // Get the user details
            if (email.contains("@admin.cmt.co.za")) {

                String sqlQuery1 = "select * from ADMIN where ADMIN_EMAIL = ? and ADMIN_PWORD = ?;";
                preparedStatement = connection.prepareStatement(sqlQuery1);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, pword);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    System.out.println("Logged In as an Admin");
                    return User.JSONUserData(resultSet.getInt("ADMIN_ID"));

                } else {
                    System.out.println("incorrect pword or email");
                    return "incorrect pword or email";
                }

            } else {

                String sqlQuery1 = "select * from USER where USER_EMAIL = ? and USER_PWORD = ?;";
                preparedStatement = connection.prepareStatement(sqlQuery1);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, pword);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    System.out.println("Logged In as a USER");
                    return User.JSONUserData(resultSet.getInt("USER_ID"));

                } else {
                    System.out.println("incorrect pword or email");
                    return "incorrect pword or email";
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }

        return "Login did not attempt try/catch\r\n";
    }

    public static String GetAllUsers() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try
        {
            connection = MakeSqlConnection();

            String sqlQuery1 = "select * from USER;";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery1);

            JSONArray allUserArr = new JSONArray();
            while (resultSet.next())
            {
                JSONObject userObj = new JSONObject();
                userObj.put("userId", resultSet.getInt("USER_ID"));
                userObj.put("userName", resultSet.getString("USER_NAME"));
                userObj.put("userEmail", resultSet.getString("USER_EMAIL"));
                allUserArr.put(userObj);
            }

            return allUserArr.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (resultSet != null)
            {
                resultSet.close();
            }
            if (statement != null)
            {
                statement.close();
            }
            if (connection != null)
            {
                connection.close();
            }
        }

        return "no users found";
    }
}
