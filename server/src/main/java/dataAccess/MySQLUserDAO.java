package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO{
    public MySQLUserDAO(){
        try{
            configureDatabase();
        }
        catch(DataAccessException ignored){}
    }

    public void createUser(String username, String password, String email) throws DataAccessException {

    }

    public String createAuth(String username) throws DataAccessException {
        return null;
    }

    public AuthData checkAuth(String auth) throws DataAccessException {
        return null;
    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    public String getAuthToken(String username) throws DataAccessException {
        return null;
    }

    public AuthData getAuthData(String auth) throws DataAccessException {
        return null;
    }

    public boolean checkCredentials(String username, String password) throws DataAccessException {
        return false;
    }

    public void deleteAuth(String auth) throws DataAccessException {

    }

    public void clearUsers() throws DataAccessException {

    }

    public void clearAuth() throws DataAccessException {

    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userDB (
              `authToken` int NOT NULL AUTO_INCREMENT,
              `userName` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
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
