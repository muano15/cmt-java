import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Conference {

    int CONF_ID;
    String CONF_NAME;
    int CONF_MODE;
    String CONF_ABST_DDAY;
    String CONF_PAPER_DDAY;
    List<String> organisers = new ArrayList<>(); ///////////////////////////////////// changed to string array
    List<String> reviewers = new ArrayList<>();
    List<String> areachairs = new ArrayList<>();
    List<String> authors = new ArrayList<>();
    List<String> submissions = new ArrayList<>();

    public void GetConference(int confId) throws SQLException {

        Admin admin = new Admin();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ResultSet innerResultSet = null;

        try {
            connection = admin.MakeSqlConnection();

            // Get the conferences details
            String sqlQuery1 = "select * from CONFERENCE where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setInt(1, confId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CONF_ID = resultSet.getInt("CONF_ID");
                CONF_NAME = resultSet.getString("CONF_NAME");
                CONF_MODE = resultSet.getInt("CONF_MODE");
                CONF_ABST_DDAY = String.valueOf(resultSet.getDate("CONF_ABST_DDAY"));
                CONF_PAPER_DDAY = String.valueOf(resultSet.getDate("CONF_PAPER_DDAY"));
            }

            // Get the conference members
            String sqlQuery2 = "select * from CONF_ROLE where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setInt(1, confId);
            resultSet = preparedStatement.executeQuery();

            String sqlQuery3 = "select * from USER where USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery3);

            while (resultSet.next()) {
                preparedStatement.setInt(1, resultSet.getInt("USER_ID"));
                innerResultSet = preparedStatement.executeQuery();
                String[] userArr = new String[3];

                while (innerResultSet.next())
                {
                    if (resultSet.getString("ROLE").equals("organiser"))
                    {
                        userArr[0] = String.valueOf(innerResultSet.getInt("USER_ID"));
                        userArr[1] = innerResultSet.getString("USER_NAME");
                        userArr[2] = innerResultSet.getString("USER_EMAIL");
                        organisers.add(String.join("::", userArr));
                    }
                    else if (resultSet.getString("ROLE").equals("areachair"))
                    {
                        userArr[0] = String.valueOf(innerResultSet.getInt("USER_ID"));
                        userArr[1] = innerResultSet.getString("USER_NAME");
                        userArr[2] = innerResultSet.getString("USER_EMAIL");
                        areachairs.add(String.join("::", userArr));
                    }
                    else if (resultSet.getString("ROLE").equals("reviewer"))
                    {
                        userArr[0] = String.valueOf(innerResultSet.getInt("USER_ID"));
                        userArr[1] = innerResultSet.getString("USER_NAME");
                        userArr[2] = innerResultSet.getString("USER_EMAIL");
                        reviewers.add(String.join("::", userArr));
                    }
                    else if (resultSet.getString("ROLE").equals("author"))
                    {
                        userArr[0] = String.valueOf(innerResultSet.getInt("USER_ID"));
                        userArr[1] = innerResultSet.getString("USER_NAME");
                        userArr[2] = innerResultSet.getString("USER_EMAIL");
                        authors.add(String.join("::", userArr));
                    }
                }
            }

            // Get the conference submissions
            String sqlQuery4 = "select * from SUBMISSION where CONF_ID = ?";
            preparedStatement = connection.prepareStatement(sqlQuery4);
            preparedStatement.setInt(1, confId);
            resultSet = preparedStatement.executeQuery();

            preparedStatement = connection.prepareStatement(sqlQuery3);

            while (resultSet.next())
            {
                preparedStatement.setInt(1, resultSet.getInt("USER_ID"));
                innerResultSet = preparedStatement.executeQuery();
                String[] sub = new String[5];

                while (innerResultSet.next())
                {
                    sub[0] = String.valueOf(resultSet.getInt("SUB_ID"));
                    sub[1] = String.valueOf(resultSet.getInt("USER_ID"));
                    sub[2] = String.valueOf(resultSet.getString("ABSTRACT"));
                    sub[3] = String.valueOf(innerResultSet.getString("USER_NAME"));
                    sub[4] = String.valueOf(innerResultSet.getString("USER_EMAIL"));
                    submissions.add(String.join("::", sub));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            innerResultSet.close();
            preparedStatement.close();
            connection.close();
        }
    }
    public static String JSONConferenceData(int CONF_ID) throws SQLException {
        Conference conference = new Conference();
        conference.GetConference(CONF_ID);

        JSONObject confObj = new JSONObject();
        confObj.put("confId", conference.CONF_ID);
        confObj.put("confName", conference.CONF_NAME);
        confObj.put("confMode", conference.CONF_MODE);
        confObj.put("confAbstDday", conference.CONF_ABST_DDAY);
        confObj.put("confPaperDday", conference.CONF_PAPER_DDAY);

        JSONObject confMembers = new JSONObject();

        JSONArray confOrganisers = new JSONArray();
        String[] userStrArr;

        for (String user : conference.organisers)
        {
            JSONObject orgObj = new JSONObject();
            userStrArr = user.split("::");

            orgObj.put("userId", userStrArr[0]);
            orgObj.put("userName", userStrArr[1]);
            orgObj.put("userEmail", userStrArr[2]);

            confOrganisers.put(orgObj);
        }
        confMembers.put("organisers", confOrganisers);

        JSONArray confAreachairs = new JSONArray();
        for (String user : conference.areachairs)
        {
            JSONObject achObj = new JSONObject();
            userStrArr = user.split("::");

            achObj.put("userId", userStrArr[0]);
            achObj.put("userName", userStrArr[1]);
            achObj.put("userEmail", userStrArr[2]);

            confAreachairs.put(achObj);
        }
        confMembers.put("areachairs", confAreachairs);

        JSONArray confReviewers = new JSONArray();
        for (String user : conference.reviewers)
        {
            JSONObject revObj = new JSONObject();
            userStrArr = user.split("::");

            revObj.put("userId", userStrArr[0]);
            revObj.put("userName", userStrArr[1]);
            revObj.put("userEmail", userStrArr[2]);

            confReviewers.put(revObj);
        }
        confMembers.put("reviewers", confReviewers);

        JSONArray confAuthors = new JSONArray();
        for (String user : conference.authors)
        {
            JSONObject athObj = new JSONObject();
            userStrArr = user.split("::");

            athObj.put("userId", userStrArr[0]);
            athObj.put("userName", userStrArr[1]);
            athObj.put("userEmail", userStrArr[2]);

            confAuthors.put(athObj);
        }
        confMembers.put("authors", confAuthors);
        confObj.put("confMembers", confMembers);


        JSONArray submissionsArray = new JSONArray();
        String[] submissionStrArr;
        for (String sub : conference.submissions)
        {
            JSONObject submissionObj = new JSONObject();
            submissionStrArr = sub.split("::");

            submissionObj.put("SUB_ID", submissionStrArr[0]);
            submissionObj.put("USER_ID", submissionStrArr[1]);
            submissionObj.put("ABSTRACT", submissionStrArr[2]);
            submissionObj.put("USER_NAME", submissionStrArr[3]);
            submissionObj.put("USER_EMAIL", submissionStrArr[4]);

            submissionsArray.put(submissionObj);
        }
        confObj.put("confSubmissions", submissionsArray);

        System.out.println("\n");
        System.out.println(confObj.toString());
        System.out.println("\n");

        return "**" + confObj.toString();
    }
}
