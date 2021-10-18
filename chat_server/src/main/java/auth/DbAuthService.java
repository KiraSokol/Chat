package auth;

import error.UserNotFoundException;

import java.sql.*;

public class DbAuthService implements AuthService {

    private static Connection connection;
    private static Statement statement;



    private static void createDB() throws SQLException {
        statement.execute("create table if not exists users (login text primary key, password text);");
        statement.execute("insert into clients (login, password, username)" + "values ('log1', 'pass', 'user1'), ('log1', 'pass', 'user2');");
    }

    private static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:db/clients.db");
        statement = connection.createStatement();
    }

    public String getClient (String login, String pass) {
        try (PreparedStatement ps = connection.prepareStatement("select username from clients where login = ? and password = ?")) {
            ps.setString(1, login);
            ps.setString(2, pass);
            String sql = "SELECT * FROM clients";
            Boolean isRetrieved = statement.execute(sql);
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                String result = resultSet.getString("username");
                resultSet.close();
                return result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
       throw new UserNotFoundException("User not found");
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return getClient(login, password);
    }

    @Override
    public String changeNickname(String oldNick, String newNick) {
        return null;
    }

    @Override
    public void changePassword(String nickname, String oldPassword, String newPassword) {

    }

    @Override
    public void createNewUser(String login, String password, String nickname) {

    }

    @Override
    public void deleteUser(String nickname) {

    }

    public String changeUsername(String oldName, String newName) {
        try (PreparedStatement ps = connection.prepareStatement("update clients set username = ? where username = ?")) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            if (ps.executeUpdate() >0) return newName;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return oldName;
    }
}