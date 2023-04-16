import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class Gateway {
    public static void main(String[] args) throws IOException {
        Gateway gateway = new Gateway();

        InputStreamReader inputStreamReader = null; OutputStreamWriter outputStreamWriter = null; BufferedReader bufferedReader = null; BufferedWriter bufferedWriter = null;

        ResultSet resultSet = null; PreparedStatement preparedStatement = null; Connection connection = null;

        try {
            System.out.println(Authenticate.HashStr("makhokha"));
            System.out.println(Authenticate.HashStr("pword"));
            System.out.println(Authenticate.HashStr(""));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
//        c1ebbee46d8c6128b95feb21c187c2ed2edde97994a9a2bac822f78b71789f29
//        e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855

        ServerSocket ss = new ServerSocket(13000);
        System.out.println("Listening on port " + 13000);

//            ServerSocket ss = new ServerSocket(13000);
//            System.out.println("Listening on port " + 13000);

        while (true)
        {
            Socket soc = ss.accept();
            System.out.println("TCP connection with streams.js established");

            inputStreamReader = new InputStreamReader(soc.getInputStream()); outputStreamWriter = new OutputStreamWriter(soc.getOutputStream()); bufferedReader = new BufferedReader(inputStreamReader); bufferedWriter = new BufferedWriter(outputStreamWriter);

            try
            {
                // Reading in the request and processing it
//                    System.out.println(bufferedReader.readLine());
                System.out.println("waiting for a request message...");
                GatewayString gws = gateway.ProcessRequestStr(bufferedReader.readLine());


                // This is where the business logic will be
                if (gws.action.equals("GetAllUsers"))
                {
                    bufferedWriter.write("<<" + Role.GetAllUsers());
                    bufferedWriter.flush();
                }
                else if (gws.action.equals("GetUserData"))
                {
                    bufferedWriter.write(User.JSONUserData(gws.userId));
                    bufferedWriter.flush();
                }
                else if (gws.action.equals("Login"))
                {
                    bufferedWriter.write("send pword");
                    bufferedWriter.flush();
                    System.out.println("request for a user pword sent, now waiting...");
                    
                    gws.userPword = Authenticate.HashStr(bufferedReader.readLine());

                    bufferedWriter.write(Role.Login(gws.userEmail, gws.userPword));
                    bufferedWriter.flush();
                }
                else if (gws.action.equals("Signup"))
                {
                    bufferedWriter.write("send signup details");
                    bufferedWriter.flush();

                    GatewaySignupString gwss = new GatewaySignupString(bufferedReader.readLine());

//                        System.out.println(
//                                "\nUsername: " + gwss.userName
//                                + "\nUser email: " + gwss.userEmail
//                                + "\nUser password: " + gwss.userHashedPword
//                                + "\nUser expertise: " + gwss.userExpertise
//                                + "\nUser domain: " + gwss.userDomain + "\n");

                    boolean signupStatus = User.CreateUser(gwss.userName, gwss.userEmail, gwss.userExpertise, gwss.userDomain, gwss.userHashedPword);

                    if (signupStatus == true)
                    {
                        bufferedWriter.write("signup successful");
                        bufferedWriter.flush();
                    }
                    else
                    {
                        bufferedWriter.write("signup unsuccessfull");
                        bufferedWriter.flush();
                    }
                }
                else if (gws.role.equals("admin"))
                {
                    if (gws.action.equals("MakeConference"))
                    {
                        bufferedWriter.write(Admin.MakeConference(gws.confName, gws.confOrganisers, gws.confAreachairs, gws.confReviewers, gws.confAuthors, gws.confMode));
                        bufferedWriter.flush();
                    }
                    else if (gws.action.equals("DeleteConference"))
                    {
                        Admin.DeleteConference(gws.confId);
                    }
                    else if (gws.action.equals("RemoveOrganiser"))
                    {
                        for (String org : gws.confOrganisers)
                        {
                            Admin.RemoveOrganiser(gws.confId, Integer.valueOf(org));
                        }
                    }
                    else if (gws.action.equals("AddOrganiser"))
                    {
                        for (String org : gws.confOrganisers)
                        {
                            Admin.AddOrganiser(gws.confId, Integer.valueOf(org));
                        }
                    }
                }
                else if (gws.role.equals("organiser"))
                {

                }
                else if (gws.role.equals("areachair"))
                {

                }
                else if (gws.role.equals("reviewer"))
                {

                }
                else if (gws.role.equals("Author"))
                {

                }
                else
                {
                    // case where role does not exists
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();

            }
            finally
            {
                bufferedReader.close(); bufferedWriter.close(); inputStreamReader.close(); outputStreamWriter.close();
                soc.close();
            }
        }

    }
    public static class GatewayString
    {
        // String formart => userid/useremail::action::confid/confname::role::attributes@confOrg=org1,org2/null/null
        // the character ; represents a null section
        int userId; /* OR */ String userEmail; /* OR */ String userName;
        String action;
        int confId; /* OR */ String confName;
        String role;
        /* This */ String userPword; /* is for the case where the action is Login or Sigup */
        String[] confOrganisers;
        String[] confAreachairs;
        String[] confReviewers;
        String[] confAuthors;
        int confMode;
    }
    public static class GatewaySignupString
    {
        public GatewaySignupString(String gwss)
        {
            // Signup details string => name :: email :: pword :: expertise1,expertise2 :: domain1,domain2
            String[] strArr = gwss.split("::");

            this.userName = strArr[0];
            this.userEmail = strArr[1];
            try {
                this.userHashedPword = Authenticate.HashStr(strArr[2]);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            this.userExpertise = strArr[3];
            this.userDomain = strArr[4];
        }
        String userName;
        String userEmail;
        String userHashedPword;
        String userExpertise;
        String userDomain;
    }
    public static GatewayString ProcessRequestStr(String reqStr) {

        // String formart => useremail|userid|null|username :: action :: confid|null|confname :: null|role :: null|attributes@confOrg=org1,org2/confAch=ach1,ach2/confRev=rev1,rev2/confAth=ath1,ath2/confMode=0
        // the character ; represents a null section

        GatewayString gatewayString = new GatewayString();

        String[] strArr = reqStr.split("::");

        // SECTION 1 - index 0 = useremail | userid | username
        if (strArr[0].contains("@"))
        {
            gatewayString.userEmail = strArr[0];
        }
        else if (strArr[0].contains("1") || strArr[0].contains("1") || strArr[0].contains("2") || strArr[0].contains("3") || strArr[0].contains("4") || strArr[0].contains("5") || strArr[0].contains("6") || strArr[0].contains("7") || strArr[0].contains("8") || strArr[0].contains("9"))
        {
            gatewayString.userId = Integer.valueOf(strArr[0]);
        }
        else if (strArr[0].equals("null"))
        {
            // do nothing
        }
        else
        {
            gatewayString.userName = strArr[0];
        }

        // SECTION 2 - index 1 = action
       gatewayString.action = strArr[1];

        // SECTION 3 - index 2 = confid | null | confname
        if (strArr[2].contains("1") || strArr[2].contains("1") || strArr[2].contains("2") || strArr[2].contains("3") || strArr[2].contains("4") || strArr[2].contains("5") || strArr[2].contains("6") || strArr[2].contains("7") || strArr[2].contains("8") || strArr[2].contains("9"))
        {
            gatewayString.confId = Integer.valueOf(strArr[2]);
        }
        else if (strArr[2].equals("null"))
        {
            // nothing to be done
        }
        else
        {
            gatewayString.confName = strArr[2];
        }

        // SECTION 4 - index 3 = role
        gatewayString.role = strArr[3];

        // SECTION 5 - index 4 = null | attributes
        if (strArr[4].contains("attributes")) {
            String[] attributesArr = strArr[4].split("@")[1].split("/");
            for (int i = 0; i < attributesArr.length; i++)
            {
                System.out.println("attributes array " + attributesArr[i]);
            }

            if (attributesArr[0].contains("confOrg")) {
                String[] orgTempArr = attributesArr[0].split("=")[1].split(",");
                gatewayString.confOrganisers = orgTempArr;
            }

            if (attributesArr[1].contains("confAch")) {
                String[] achTempArr = attributesArr[1].split("=")[1].split(",");
                gatewayString.confAreachairs = achTempArr;
            }

            if (attributesArr[2].contains("confRev")) {
                String[] revTempArr = attributesArr[2].split("=")[1].split(",");
                gatewayString.confReviewers = revTempArr;
            }

            if (attributesArr[3].contains("confAth")) {
                String[] athTempArr = attributesArr[3].split("=")[1].split(",");
                gatewayString.confAuthors = athTempArr;
            }

            if (attributesArr[4].contains("confMode")) {
                String[] modeTempArr = attributesArr[4].split("=");

                gatewayString.confMode = Integer.valueOf(modeTempArr[1]);
            }

        }

        System.out.println("request message: '" + reqStr + "' has been processed" +
                "\naction: " + gatewayString.action + "\n");
        return gatewayString;
    }
}
