package dataAccess;

import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class MySQLUserDAO implements UserDAO{
    public MySQLUserDAO(){
        try{
            configureDatabase();
        }
        catch(DataAccessException ignored){}
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        createAuth(username);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("UPDATE userDB SET password=?, email=? WHERE userName=?")) {
                String hash = hashPassword(password);
                preparedStatement.setString(1, hash);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, username);
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Wrong", 500);
        }
    }

    public String hashPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public String createAuth(String username) throws DataAccessException {
        if(Objects.equals(getAuthToken(username), "0")) {
            try (var conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement("INSERT INTO userDB (username) values (?)", Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.executeUpdate();
                    var resultSet = preparedStatement.getGeneratedKeys();
                    var ID = 0;
                    if (resultSet.next()) {
                        ID = resultSet.getInt(1);
                    }
                    return String.valueOf(ID);
                }
            } catch (SQLException e) {
                throw new DataAccessException("Wrong", 500);
            }
        }
        else{
            throw new DataAccessException("Wrong", 500);
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var authToken = getAuthToken(username);
            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email from userDB WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var name = rs.getString("username");
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(name, password, email);
                    }
                    else {
                        throw new DataAccessException("Whoopsie", 500);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("whoopsie", 500);
        }
    }

    public String getAuthToken(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken FROM userDB WHERE userName=?")) {
                preparedStatement.setString(1, username);
                var resultSet = preparedStatement.executeQuery();

                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }
                return String.valueOf(ID);
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Wrong", 500);
        }
    }

    public AuthData getAuthData(String auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, userName from userDB WHERE authToken = ?")) {
                preparedStatement.setInt(1, Integer.parseInt(auth));
                try (var rs = preparedStatement.executeQuery()) {
                    var authToken = rs.getInt("authToken");
                    var name = rs.getString("userName");
                    return new AuthData(name, auth);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("whoopsie", 500);
        }
    }

    public boolean checkCredentials(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var authToken = getAuthToken(username);
            try (var preparedStatement = conn.prepareStatement("SELECT password from userDB WHERE authToken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var hashedPassword = rs.getString("password");
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        return encoder.matches(password, hashedPassword);
                    }
                    else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("whoopsie", 500);
        }
    }

    public void deleteAuth(String auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE from userDB WHERE authToken=?")) {
                preparedStatement.setInt(1, Integer.parseInt(auth));
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("whoops", 500);
        }
    }

    public void clearUsers() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE userDB")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("no working",500);
        }
    }

    public void clearAuth() throws DataAccessException {
        clearUsers();
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userDB (
              `authToken` int NOT NULL AUTO_INCREMENT,
              `userName` varchar(256) NOT NULL,
              `password` varchar(256) DEFAULT NULL,
              `email` varchar(256) DEFAULT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(`userName`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()), ex.getErrorCode());
        }
    }
}
