package auth;

public interface AuthService {
    void start();
    void stop();
    String getNicknameByLoginAndPassword(String login, String password);
    String changeNickname(String oldNick, String newNick);
    void changePassword(String nickname, String oldPassword, String newPassword);
    void createNewUser(String login, String password, String nickname);
    void deleteUser(String nickname);

}
