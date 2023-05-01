import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.*;


public class Gateway {
    public static void main(String[] args) throws IOException {
        Gateway gateway = new Gateway();

        InputStreamReader inputStreamReader = null; OutputStreamWriter outputStreamWriter = null; BufferedReader bufferedReader = null; BufferedWriter bufferedWriter = null;

        ResultSet resultSet = null; PreparedStatement preparedStatement = null; Connection connection = null;

        ServerSocket ss = new ServerSocket(13000);

        while (true)
        {
            System.out.println("\n Waiting for connection");
            Socket soc = ss.accept();
            System.out.println("TCP connection with servers.js established");

            inputStreamReader = new InputStreamReader(soc.getInputStream()); outputStreamWriter = new OutputStreamWriter(soc.getOutputStream()); bufferedReader = new BufferedReader(inputStreamReader); bufferedWriter = new BufferedWriter(outputStreamWriter);

            try
            {
                System.out.println("waiting for a request message...");
                String requestMessage = bufferedReader.readLine();
                GatewayString gws = gateway.ProcessRequestStr(requestMessage);
                System.out.println("step3: request message received and processed");

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
                else if (gws.action.equals("GetConference"))
                {
                    bufferedWriter.write(Conference.JSONConferenceData(gws.confId));
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
                else if (gws.action.equals("CreateSubmission"))
                {
                    bufferedWriter.write("send submission abstract");
                    bufferedWriter.flush();

                    String submissionAbstract = bufferedReader.readLine();

                    System.out.println(submissionAbstract);

                    boolean result = Author.CreateSubmission(gws.confId, gws.userId, submissionAbstract);

                    System.out.println("the result is " + result);

                    if (result)
                    {
                        bufferedWriter.write("submission successful");
                        bufferedWriter.flush();
                    }
                    else
                    {
                        bufferedWriter.write("submission unsuccessful");
                        bufferedWriter.flush();
                    }
                }
                else if (gws.action.equals("MakeConference"))
                {
                    bufferedWriter.write(Admin.MakeConference(gws.confName, gws.confOrganisers, gws.confAreachairs, gws.confReviewers, gws.confAuthors, gws.confMode));
                    bufferedWriter.flush();
                }
                else if (gws.action.equals("SetDueDate"))
                {
                    bufferedWriter.write("for which and date");
                    bufferedWriter.flush();

                    String forWhichAndDate = bufferedReader.readLine();

                    if (Organiser.SetDueDate(gws.confId, forWhichAndDate.split("@")[0], forWhichAndDate.split("@")[1]))
                    {
                        System.out.println("SetDueDate successful");
                        bufferedWriter.write("due date set successfully");
                        bufferedWriter.flush();
                    }
                    else
                    {
                        System.out.println("SetDueDate unsuccessful");
                        bufferedWriter.write("due date set was unsuccessful");
                        bufferedWriter.flush();
                    }
                }
                else if (gws.action.equals("DeleteConference"))
                {
                    Admin.DeleteConference(gws.confId);
                }
                else if (gws.action.equals("RemoveOrganiser"))
                {
                    for (String org : gws.confOrganisers)
                    {
                        Admin.RemoveMember(gws.confId, Integer.valueOf(org));
                    }
                }
                else if (gws.action.equals("AddOrganiser"))
                {
                    for (String org : gws.confOrganisers)
                    {
//                        Admin.AddMember(gws.confId, Integer.valueOf(org));
                    }
                }
                else if (gws.action.equals("UploadFile"))
                {
                    bufferedWriter.write("send file to be uploaded");
                    bufferedWriter.flush();
                    System.out.println("step4: request for file to be uploaded sent, now waiting...");

                    String Uint8ArrayString = bufferedReader.readLine();
                    System.out.println("step8: received file of size " + Uint8ArrayString.length() + " from the client");

                    String file = "muano.txt";
                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
                    fileWriter.write(Uint8ArrayString);
                    fileWriter.close();
                    System.out.println("step9: done writting the bytes to muano.txt file and closing fileWriter");

                    bufferedWriter.write("upload is successful");
                    bufferedWriter.flush();
                    System.out.println("step10: notified the server.js that upload was successful");
                }
                else if (gws.action.equals("DownloadFile"))
                {
                    bufferedWriter.write("what file?");
                    bufferedWriter.flush();
                    System.out.println("step4: request for a file name sent, now waiting...");

                    String fileName = bufferedReader.readLine();
                    System.out.println("step8: received file name is: " + fileName);

                    BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
                    String Uint8ArrayString = fileReader.readLine();
                    double Uint8ArrayStringLength = Uint8ArrayString.length();
                    System.out.println("step9: read the file from " + fileName + " and length is " + Uint8ArrayStringLength);

                    bufferedWriter.write("^^" + String.valueOf((int)Uint8ArrayStringLength) + "^^fileSize");
                    bufferedWriter.flush();
                    System.out.println("step10: fileSize to be downloaded has been sent to servers.js to acknowledge");

                    String acknowledgedSize = bufferedReader.readLine();
                    System.out.println("step12: acknowledgement that fileSize is " + acknowledgedSize + " from servers.js has been received");

                    if (Integer.parseInt(acknowledgedSize) == Uint8ArrayStringLength)
                    {
                        System.out.println("step13: the acknowledgedSize == Uint8ArrayStringLength, hence has nothing went wrong");
                        int batchSize = 8000;

                        double startIndex = 0, finishIndex, numOfLoops;
                        if (Uint8ArrayStringLength > batchSize)
                        {
                            finishIndex = batchSize;
                            numOfLoops = Math.ceil(Uint8ArrayStringLength / batchSize);
                        }
                        else
                        {
                            finishIndex = Uint8ArrayStringLength - 1;
                            numOfLoops = 1;
                        }

                        String subString = "";
                        System.out.println("the numOfLoops is " + numOfLoops);
                        for (int q = 0; q < (int)numOfLoops; q++)
                        {
                            if (Uint8ArrayStringLength - startIndex > batchSize)
                            {
                                subString = Uint8ArrayString.substring((int) startIndex,(int) finishIndex);
                                bufferedWriter.write("^^" + subString);
                                bufferedWriter.flush();
                                System.out.println("step14(i):batch# " + q + " has been sent");

                                String state = bufferedReader.readLine();
                                System.out.println("step14(ii): client says: " + state);

                                startIndex = finishIndex;
                                finishIndex = finishIndex + batchSize;
                            }
                            else
                            {
                                subString = Uint8ArrayString.substring((int) startIndex, ((int) Uint8ArrayStringLength));
                                bufferedWriter.write("^^" + subString);
                                bufferedWriter.flush();
                                System.out.println("step15(i): the last batch has been sent");

                                String state = bufferedReader.readLine();
                                System.out.println("step15(ii): client says: " + state);
                            }
                        }
                    }
                    else
                    {
                        System.out.println("Something deep went wrong");
                    }
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
                System.out.println("Socket is closing");
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
        String[] confOrganisers = {};
        String[] confAreachairs = {};
        String[] confReviewers = {};
        String[] confAuthors = {};
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
