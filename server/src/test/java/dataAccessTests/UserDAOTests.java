package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLUserDAO;
import model.UserData;
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
        assertDoesNotThrow(testUserDB::clearAuth);
    }
    //POS AND NEGATIVE CREATE USER TESTS
    @Test
    void addUserTest()throws DataAccessException{
        assertDoesNotThrow(()->testUserDB.createUser("Rhodric","myPassword","myEmail"));
    }

    @Test
    void addUserBadInputs()throws DataAccessException{
        testUserDB.createUser("Rhodric", "myPassword", "myEmail");
        assertThrows(DataAccessException.class, ()->testUserDB.createUser(null, "", ""));
    }
    //POSITIVE ADD AUTH TEST
    @Test
    void addAuthTest()throws DataAccessException{
        assertEquals("1",testUserDB.createAuth("Rhodric"));
    }

    //NEGATIVE ADD AUTH TEST
    @Test
    void addAuthInvalidInput()throws DataAccessException{
        testUserDB.createAuth("Rhodric");
        assertThrows(DataAccessException.class,()->testUserDB.createAuth(null));
    }


    //positive and negative get userData tests

    @Test
    void getUserData()throws DataAccessException{
        testUserDB.createUser("Rhodric", "myPassword", "myEmail");
        UserData myData = testUserDB.getUser("Rhodric");
        assertEquals("Rhodric", myData.username());
        assertEquals("myEmail", myData.email());
    }

    @Test
    void getNonexistentUserData()throws DataAccessException{
        testUserDB.createUser("Rhodric", "myPassword", "myEmail");
        assertNull(testUserDB.getUser("Krystyna"));
    }

    //positive and negative getAuthToken functions
    @Test
    void getCorrectAuthToken()throws DataAccessException{
        testUserDB.createAuth("Rhodric");
        assertDoesNotThrow(()->testUserDB.getAuthToken("Rhodric"));
    }

    @Test
    void getNonexistentAuthToken()throws DataAccessException{
        testUserDB.createAuth("Rhodric");
        assertNull(testUserDB.getAuthToken("Krystyna"));
    }

    //positive and negative get auth data
    @Test
    void getCorrectAuthData()throws DataAccessException{
        testUserDB.createAuth("Rhodric");
        String myAuthToken = testUserDB.getAuthToken("Rhodric");
        assertDoesNotThrow(()->testUserDB.getAuthData(myAuthToken));
    }

    @Test
    void getNonexistentAuthData()throws DataAccessException{
        testUserDB.createAuth("Rhodric");
        assertNull(testUserDB.getAuthData("1000"));
    }

    //positive and negagtive tests for checkCredentials
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

    //2 positive 1 negative for deleteAuth

    @Test
    void deleteAuth()throws DataAccessException{
        testUserDB.createAuth("Rhodric");
        String auth = testUserDB.getAuthToken("Rhodric");
        assertDoesNotThrow(()->testUserDB.deleteAuth(auth));
    }

    @Test
    void deleteNonexistentAuth()throws DataAccessException{
        assertDoesNotThrow(()->testUserDB.deleteAuth("1234"));
    }

    @Test
    void deleteAuthWithBadInput()throws DataAccessException{
        assertThrows(NumberFormatException.class,()->testUserDB.deleteAuth(null));
    }
}
