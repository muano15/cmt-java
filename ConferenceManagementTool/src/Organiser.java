import java.sql.*;

public class Organiser extends Areachair{

    public static boolean SetDueDate(int confId, String forWhich, String date) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try
        {
            connection = MakeSqlConnection();

            if (forWhich.equals("abstract"))
            {
                String sqlQuery1 = "update CONFERENCE set CONF_ABST_DDAY = ? where CONF_ID = ?;";
                preparedStatement = connection.prepareStatement(sqlQuery1);
                preparedStatement.setDate(1, Date.valueOf(date));
                preparedStatement.setInt(2, confId);

                if (preparedStatement.execute())
                {
                    return true;
                }
            }
            else if (forWhich.equals("paper"))
            {
                String sqlQuery1 = "update CONFERENCE set CONF_PAPER_DDAY = ? where CONF_ID = ?;";
                preparedStatement = connection.prepareStatement(sqlQuery1);
                preparedStatement.setDate(1, Date.valueOf(date));
                preparedStatement.setInt(2, confId);

                if (preparedStatement.execute())
                {
                    return true;
                }
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
