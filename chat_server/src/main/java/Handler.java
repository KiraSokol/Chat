import error.UserNotFoundException;
import error.WrongCredentialException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Handler {
    public static final String REGEX = "%#%";
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread handlerThread;
    private Server server;
    private String currentUser;

    public Handler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Handler created");
            this.server = server;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle() {
        handlerThread = new Thread(() -> {
            authorize();
            try {
                while (!Thread.currentThread().isInterrupted() && socket.isConnected()) {
                    String message = in.readUTF();
                    handleMassage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                server.removeAuthorizedClientFromList(this);
            }
        });
        handlerThread.start();
    }

    private void authorize(){
        while (true) {
            try {
                String message = in.readUTF();
               if (message.startsWith("/auth") || message.startsWith("/register")) {
                   if (handleMassage(message) ) break;
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Thread getHandlerThread() {
        return handlerThread;
    }

    public String getCurrentUser() {
        return currentUser;
    }


    public void sendMessage(String message) {
        try {
            this.out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean handleMassage (String message) {
        try {
        String[] parsed = message.split(REGEX);
        switch (parsed[0]) {
            case "/w":
                server.sendPrivateMassage(this.currentUser, parsed[1], parsed[2], this);
                break;
            case "/ALL":
                server.broadcastMessage(this.currentUser, parsed[1]);
                break;
            case "/change_nick":
                String nick = server.getAuthService().changeNickname(this.currentUser, parsed[1]);
                server.removeAuthorizedClientFromList(this);
                this.currentUser = nick;
                server.addAuthorizedClientToList(this);
                sendMessage("/change_nick_ok");
                break;
            case "/change_pass" :
                server.getAuthService().changePassword(this.currentUser, parsed[1], parsed[2]);
                sendMessage("/change_pass_ok");
                break;
            case "/remove" :
                server.getAuthService().deleteUser(this.currentUser);
                this.socket.close();
                break;
            case "/register" :
                server.getAuthService().createNewUser(parsed[1], parsed[2], parsed[3]);
                sendMessage("register_ok:");
                break;
            case "/auth" :
                this.currentUser = server.getAuthService().getNicknameByLoginAndPassword(parsed[1], parsed[2]);
                if (server.isNicknameBusy(currentUser)) {
                    sendMessage("ERROR:" + REGEX + "You are clone");
                } else {
                    this.server.addAuthorizedClientToList(this);
                    sendMessage("authok: " + REGEX + this.currentUser);
                    return true;
                }
                break;
            default:
                sendMessage("ERROR:" + REGEX + "command not found");
            }
        } catch (Exception e) {
            sendMessage("ERROR:" + REGEX + e.getMessage());
        }
        return false;
    }

}




