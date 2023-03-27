import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {

    public int USER_ID;
    public String USER_NAME;
    public ArrayList<String> USER_EXPERTISE;
    public ArrayList<String> USER_DOMAIN;
    public ArrayList<Role> USER_ROLES;

    public void GetUser(int USER_ID) throws SQLException {

        Admin admin = new Admin();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = admin.MakeSqlConnection();

            // Get the user details
            String sqlQuery1 = "select * from USER where USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setInt(1, USER_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.USER_ID = resultSet.getInt("USER_ID");
                this.USER_NAME = resultSet.getString("USER_NAME");
            }

            // Get user expertise
            String sqlQuery2 = "select * from USER_EXPERTISE where USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setInt(1, USER_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                USER_EXPERTISE.add(resultSet.getString("EXPERTISE"));
            }

            // Get user domain
            String sqlQuery3 = "select * from USER_DOMAIN where USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery3);
            preparedStatement.setInt(1, USER_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                USER_DOMAIN.add(resultSet.getString("DOMAIN"));
            }

            // Get user roles
            String sqlQuery4 = "select * from USER_ROLES where USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery4);
            preparedStatement.setInt(1, USER_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                if (resultSet.getString("ROLE").equals("admin")) {
                    Admin admin1 = new Admin();
                    admin1.role = "admin";
                    admin1.conference = resultSet.getInt("CONF_ID");
                    USER_ROLES.add(admin1);

                } else if (resultSet.getString("ROLE").equals("organiser")) {
                    Organiser organiser1 = new Organiser();
                    organiser1.role = "organiser";
                    organiser1.conference = resultSet.getInt("CONF_ID");
                    USER_ROLES.add(organiser1);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }
}
