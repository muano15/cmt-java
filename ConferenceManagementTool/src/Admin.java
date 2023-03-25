import java.sql.*;
import java.util.ArrayList;

public class Admin extends Role{

    public void MakeConference(Conference conference) throws SQLException {
        Connection connection = null;
        try {
            connection = MakeSqlConnection();
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            // Sequence of SQL queries

            // Query 1: add a new entry to the CONFERENCE table and retrieve the CONF_ID
            String sqlQuery1 = "insert into CONFERENCE values(?, null, null, null);";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setString(1, conference.confName);
            preparedStatement.executeUpdate();

            String sqlQuery2 = " select * from COFERENCE where CONF_NAME = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setString(1, conference.confName);
            preparedStatement.executeQuery();

            int confId = -1;
            while (resultSet.next()) {
                confId = resultSet.getInt("CONF_ID");
            }

            // Query 2: add the participants to their corresponding tables
            for (int i = 0; i < conference.organisers.size(); i++) {
                String sqlQuery3 = "insert into CONF_ORGANISER values( ?, ?);";
                preparedStatement = connection.prepareStatement(sqlQuery3);
                preparedStatement.setInt(1, confId);
                preparedStatement.setInt(2, conference.organisers.get(i).user_id);
                preparedStatement.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
    public void DeleteConference(int confId) throws SQLException {
        Connection connection = null;
        try {
            connection = MakeSqlConnection();
            PreparedStatement preparedStatement = null;

            // Query1: delete all the rows that have no dependents first
            String sqlQuery1 = "delete from CONF_ORGANISER where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setInt(1, confId);
            preparedStatement.executeUpdate();

            String sqlQuery2 = "delete from CONFERENCE where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setInt(1, confId);
            preparedStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}
