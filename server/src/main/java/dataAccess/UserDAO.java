package dataAccess;

import model.AuthData;
import model.UserData;
public interface UserDAO {
    String createUser(String username, String password, String email) throws DataAccessException;

    String createAuth(String username) throws DataAccessException;

    AuthData checkAuth(AuthData auth) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    UserData checkCredentials(String username, String password) throws DataAccessException;

    String getUsername(String auth) throws DataAccessException;

    void clearUsers() throws DataAccessException;
}
