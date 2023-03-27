import java.sql.*;
import java.util.ArrayList;

public class Admin extends Role{

    public void MakeConference(Conference conference) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = MakeSqlConnection();

            // Sequence of SQL queries

            // Query 1: add a new entry to the CONFERENCE table and retrieve the CONF_ID
            String sqlQuery1 = "insert into conference(CONF_NAME) values(?);";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setString(1, conference.CONF_NAME);
            preparedStatement.executeUpdate();

            String sqlQuery2 = " select * from conference where CONF_NAME = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setString(1, conference.CONF_NAME);
            resultSet = preparedStatement.executeQuery();

            int confId = 1;
            while (resultSet.next()) {
                confId = resultSet.getInt("CONF_ID");
            }

            // Query 2: add the participants to their corresponding tables
            for (int i = 0; i < conference.organisers.size(); i++) {
                String sqlQuery3 = "insert into conf_organiser values( ?, ?);";
                preparedStatement = connection.prepareStatement(sqlQuery3);
                preparedStatement.setInt(1, confId);
                preparedStatement.setInt(2, conference.organisers.get(i).USER_ID);
                preparedStatement.executeUpdate();
            }

            System.out.println("Conference created");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }
    }
    public void DeleteConference(int confId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = MakeSqlConnection();

            // Query1: delete all the rows that have no dependents first
            String sqlQuery1 = "delete from conf_organiser where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setInt(1, confId);
            preparedStatement.executeUpdate();

            String sqlQuery2 = "delete from conference where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setInt(1, confId);
            preparedStatement.execute();

            System.out.println("Conference deleted");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }

    public void RemoveOrganiser(int conf_id, int organiser_id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = MakeSqlConnection();

            String sqlQuery = "delete from conf_organiser where CONF_ID = ? and USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, conf_id);
            preparedStatement.setInt(2, organiser_id);
            preparedStatement.execute();

            System.out.println("Organiser removed");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }

    public void AddOrganiser(int conf_id, int organiser_id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = MakeSqlConnection();

            String sqlQuery = "insert into conf_organiser(conf_id, user_id) values(?, ?)";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, conf_id);
            preparedStatement.setInt(2, organiser_id);
            preparedStatement.execute();

            System.out.println("Organiser added");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }
}
