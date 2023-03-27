import javax.management.relation.Role;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {

        User organiser1 = new User();
        organiser1.USER_ID = 1;

        User organiser2 = new User();
        organiser2.USER_ID = 2;

        User organiser3 = new User();
        organiser3.USER_ID = 3;

        Conference conference = new Conference();
        conference.CONF_NAME = "conference 3";
//        conference.organisers.add(organiser1);
        conference.organisers.add(organiser2);
        conference.organisers.add(organiser3);

        Admin admin = new Admin();

//        admin.AddOrganiser(4, 2);
//        System.out.println("Added organiser. Now waiting...");

//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        admin.RemoveOrganiser(4, 2);

        admin.MakeConference(conference);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        admin.AddOrganiser(4, 3);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        admin.DeleteConference(4);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        admin.AddOrganiser(4, 1);

//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        admin.RemoveOrganiser(4, 2);

        System.out.println("Done!!!");

//        try {
//            System.out.println("Waiting for clients");
//            ServerSocket ss = new ServerSocket(13000);
//            Socket soc = ss.accept();
//            System.out.println("Connection established");

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}