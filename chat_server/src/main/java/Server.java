import auth.AuthService;
import auth.DbAuthService;
import auth.inMemoryAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private static final int PORT = 8090;
    private AuthService AuthService;
    private Map<String, Handler> handlers;

    public Server() {
        this.AuthService = new DbAuthService();
        this.handlers = new HashMap<>();
    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server start!");
            while (true) {
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");
                new Handler(socket, this).handle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String from, String message) {
        message = String.format("[%s]: %s", from, message);
        for (Handler handler : handlers.values()) {
            handler.sendMessage(message);
        }
    }

    public auth.AuthService getAuthService() {
        return AuthService;
    }

    public synchronized void removeAuthorizedClientFromList(Handler handler) {
        this.handlers.remove(handler.getCurrentUser());
        sendClientOnline();
    }

    public synchronized void addAuthorizedClientToList(Handler handler) {
        this.handlers.put(handler.getCurrentUser(), handler);
        sendClientOnline();
    }

    public void sendClientOnline() {
        StringBuilder sb = new StringBuilder("/list").append(Handler.REGEX);
        for (Handler handler : handlers.values()) {
            sb.append(handler.getCurrentUser()).append(Handler.REGEX);
        }
        String message = sb.toString();
        for (Handler handler : handlers.values()) {
            handler.sendMessage(message);
        }
    }

    public void sendPrivateMassage(String sender, String recipient, String message, Handler senderHandler) {
       Handler handler = handlers.get(recipient);
       if (handler == null) {
           senderHandler.sendMessage(String.format("ERROR:%s recipient not found: %s", Handler.REGEX, recipient));
           return;
       }
                message = String.format("[%s] -> [%s]: %s", sender, recipient, message);
                handler.sendMessage(message);
                senderHandler.sendMessage(message);
                return;

    }

    public boolean isNicknameBusy(String nickname) {
        return this.handlers.containsKey(nickname);
    }
}
