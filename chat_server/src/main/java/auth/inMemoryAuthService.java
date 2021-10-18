package auth;

import error.BadRequestException;
import error.UserNotFoundException;
import error.WrongCredentialException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class inMemoryAuthService implements AuthService{
    private List<User> users;

    public inMemoryAuthService() {
        this.users = new ArrayList<>(
                Arrays.asList(
                      new User("log1", "pass", "nick1"),
                        new User("log2", "pass", "nick2")
                )
        );
    }

    @Override
    public void start() {
        System.out.println("Auth service started!");
    }

    @Override
    public void stop() {
        System.out.println("Auth service stopped!");
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (login.equals(user.getLogin())) {
                if (password.equals(user.getPassword())) return user.getNickname();
                else throw new WrongCredentialException("");
            }
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public String changeNickname(String oldNick, String newNick) {
        for (User user : users) {
            if (user.getNickname().equals(newNick)) {
                throw new BadRequestException("This nick busy");
            }
        }
        for (User user : users) {
            if (user.getNickname().equals(oldNick)) {
                user.setNickname(newNick);
                return newNick;
            }
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public void changePassword(String nickname, String oldPassword, String newPassword) {
        for (User user : users) {
            if (user.getNickname().equals(nickname)) {
                if (user.getPassword().equals(oldPassword)) {
                    user.setPassword(newPassword);
                    return;
                } else throw new BadRequestException("Wrong password");
            }

        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public void createNewUser(String login, String password, String nickname) {
        for (User user : users) {
            if (user.getNickname().equals(nickname) || user.getLogin().equals(login)) {
                throw new BadRequestException("This nick or login busy");
            }

        }
        this.users.add(new User(login, password, nickname));
    }

    @Override
    public void deleteUser(String nickname) {
        for (User user : users) {
            if (user.getNickname().equals(nickname)) {
                this.users.remove(user);
            }
        }

    }
}
