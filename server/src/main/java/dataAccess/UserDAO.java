package dataAccess;
import model.UserData;
import model.AuthData;
public interface UserDAO {
    void createUser(String username, String password, String email) throws DataAccessException;

    String createAuth(String username) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    String getAuthToken(String username) throws DataAccessException;

    AuthData getAuthData(String auth) throws DataAccessException;

    boolean checkCredentials(String username, String password) throws DataAccessException;

    void deleteAuth(String auth) throws DataAccessException;

    void clearUsers() throws DataAccessException;

    void clearAuth() throws DataAccessException;
}
