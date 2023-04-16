package org.example;

import java.io.IOException;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.*;

@ServerEndpoint("/eg")
public class Main {

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("OnOpen method called");
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("OnMessage method called");
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("OnClose method called");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("OnError method called");
    }
}