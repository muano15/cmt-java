import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Conference {

    int CONF_ID;
    String CONF_NAME;
    String CONF_TYPE;
    ArrayList<User> organisers = new ArrayList<>();
//    ArrayList<User> reviewers = new ArrayList<>();
//    ArrayList<User> areachairs = new ArrayList<>();
//    ArrayList<User> topics = new ArrayList<>();

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
                this.CONF_ID = resultSet.getInt("CONF_ID");
                this.CONF_NAME = resultSet.getString("CONF_NAME");
                this.CONF_TYPE = resultSet.getString("CONF_TYPE");
            }

            // Get the conference organisers
            String sqlQuery2 = "select * from CONF_ORGANISER where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setInt(1, CONF_ID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User organiser = new User();
                organiser.GetUser(resultSet.getInt("USER_ID"));
                organisers.add(organiser);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }
}
