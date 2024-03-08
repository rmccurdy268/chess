package dataAccess;

import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.sql.Statement;

public class MySQLUserDAO implements UserDAO{
    public MySQLUserDAO(){
        try{
            configureDatabase();
        }
        catch(DataAccessException ignored){}
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO userDB (userName,password,email) VALUES(?,?,?)")) {
                String hash = hashPassword(password);
                preparedStatement.setString(2, hash);
                preparedStatement.setString(3, email);
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Wrong", 500);
        }
    }

    private String hashPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public String createAuth(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO authDB (username) values (?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();
                var resultSet = preparedStatement.getGeneratedKeys();
                var ID = 0;
                while (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }
                return String.valueOf(ID);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Wrong", 500);
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email from userDB WHERE userName = ?")) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var name = rs.getString("username");
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(name, password, email);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public String getAuthToken(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken FROM authDB WHERE userName=?")) {
                preparedStatement.setString(1, username);
                var resultSet = preparedStatement.executeQuery();
                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt("authToken");
                }
                if (ID == 0){
                    return null;
                }
                return String.valueOf(ID);
            }
        }
        catch (SQLException e) {
            return null;
        }
    }

    public AuthData getAuthData(String auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT userName from authDB WHERE authToken = ?")) {
                try{
                    int val = Integer.parseInt(auth);
                }
                catch(NumberFormatException ex){
                    throw new DataAccessException.UnauthorizedException();
                }
                preparedStatement.setInt(1, Integer.parseInt(auth));
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()){
                        var name = rs.getString(1);
                        return new AuthData(name, auth);
                    }

                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public boolean checkCredentials(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT password from userDB WHERE userName = ?")) {
                preparedStatement.setString(1, username);
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
            throw new DataAccessException("weird error in authorization", 500);
        }
    }

    public void deleteAuth(String auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authDB WHERE authToken=?")) {
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
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE authDB")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("no working",500);
        }
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userDB (
              `id` int NOT NULL AUTO_INCREMENT,
              `userName` varchar(256) NOT NULL,
              `password` varchar(256) DEFAULT NULL,
              `email` varchar(256) DEFAULT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(`userName`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] authCreateStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authDB (
              `authToken` int NOT NULL AUTO_INCREMENT,
              `userName` varchar(256) NOT NULL,
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
            for (var statement : authCreateStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()), ex.getErrorCode());
        }
    }
}
