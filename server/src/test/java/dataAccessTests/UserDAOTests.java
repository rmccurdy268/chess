package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    static final MySQLUserDAO testUserDB = new MySQLUserDAO();

    @BeforeEach
    void clear() throws DataAccessException {
        testUserDB.clearUsers();
        testUserDB.clearAuth();
    }
    @Test
    void clearSuccess()throws DataAccessException{
        assertDoesNotThrow(testUserDB::clearUsers);
    }

    //POSITIVE ADD AUTH TEST
    @Test
    void addAuthTest()throws DataAccessException{
        assertEquals("1",testUserDB.createAuth("Rhodric"));
    }

    //NEGATIVE ADD AUTH TEST
    @Test
    void addDuplicateAuth()throws DataAccessException{
        testUserDB.createAuth("Rhodric");
        assertThrows(DataAccessException.class,()->testUserDB.createAuth("Rhodric"));
    }

    @Test
    void addUserTest()throws DataAccessException{
        assertDoesNotThrow(()->testUserDB.createUser("Rhodric","myPassword","myEmail"));
    }

    @Test
    void addUserTwice()throws DataAccessException{
        testUserDB.createUser("Rhodric", "myPassword", "myEmail");
        assertThrows(DataAccessException.class, ()->testUserDB.createUser("Rhodric", "myPassword", "myEmail"));
    }

    @Test
    void getUserData()throws DataAccessException{
        testUserDB.createUser("Rhodric", "myPassword", "myEmail");
        assertDoesNotThrow(()->testUserDB.getUser("Rhodric"));
    }

    @Test
    void getNonexistentUserData()throws DataAccessException{
        testUserDB.createUser("Rhodric", "myPassword", "myEmail");
        assertThrows(DataAccessException.class,()->testUserDB.getUser("Krystyna"));
    }

    @Test
    void checkCorrectCredentials()throws DataAccessException{
        testUserDB.createUser("Rhodric", "myPassword", "myEmail");
        assertDoesNotThrow(()->testUserDB.checkCredentials("Rhodric","myPassword"));
    }

    @Test
    void checkIncorrectCredentials()throws DataAccessException{
        testUserDB.createUser("Rhodric", "myPassword", "myEmail");
        assertFalse(testUserDB.checkCredentials("Fredrick","myPassword"));
        assertFalse(testUserDB.checkCredentials("Rhodric","notMyPassword"));
    }

    @Test
    void deleteAuth()throws DataAccessException{
        testUserDB.createUser("Rhodric","myPassword", "myEmail");
        var auth = testUserDB.getAuthToken("Rhodric");
        assertDoesNotThrow(()->testUserDB.deleteAuth(auth));
    }
}
