import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Conference {

    int CONF_ID;
    String CONF_NAME;
    int CONF_MODE;
    List<User> organisers = new ArrayList<>(); ///////////////////////////////////// changed to string array
    List<User> reviewers = new ArrayList<>();
    List<User> areachairs = new ArrayList<>();
    List<User> authors = new ArrayList<>();

    public void GetConference(int CONF_ID) throws SQLException {

        Admin admin = new Admin();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = admin.MakeSqlConnection();

            // Get the conferences details
            String sqlQuery1 = "select * from CONFERENCE where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setInt(1, CONF_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
//                this.CONF_ID = resultSet.getInt("CONF_ID");
                this.CONF_NAME = resultSet.getString("CONF_NAME");
                this.CONF_MODE = resultSet.getInt("CONF_TYPE");
            }

            // Get the conference members
            String sqlQuery2 = "select * from CONF_ROLE where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setInt(1, CONF_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();

                if (resultSet.getString("ROLE") == "organiser")
                {
                    user.USER_ID = resultSet.getInt("USER_ID");
                    organisers.add(user);
                }
                else if (resultSet.getString("ROLE") == "areachair")
                {
                    user.USER_ID = resultSet.getInt("USER_ID");
                    areachairs.add(user);
                }
                else if (resultSet.getString("ROLE") == "reviewer")
                {
                    user.USER_ID = resultSet.getInt("USER_ID");
                    reviewers.add(user);
                }
                else if (resultSet.getString("ROLE") == "author")
                {
                    user.USER_ID = resultSet.getInt("USER_ID");
                    authors.add(user);
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
