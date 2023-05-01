import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Author extends Role {

    public static boolean CreateSubmission(int confId, int userId, String submissionAbstract) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try
        {
            connection = MakeSqlConnection();

            String sqlQuery1 = "insert into SUBMISSION(CONF_ID, USER_ID, ABSTRACT) values(?, ?, ?);";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setInt(1, confId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, submissionAbstract);

            if (preparedStatement.execute())
            {
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            preparedStatement.close();
            connection.close();
        }

        return false;
    }

}
