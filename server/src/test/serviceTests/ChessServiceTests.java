package serviceTests;

import dataAccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ChessService;

import static org.junit.jupiter.api.Assertions.*;

public class ChessServiceTests {
    static final ChessService service = new ChessService();

    @BeforeEach
    void clear() throws DataAccessException {
        service.clear();
    }

    //POSITIVE REGISTER
    @Test
    void registerUserTest() throws DataAccessException {
        assertDoesNotThrow(() -> {
            service.registerUser("Rhodric", "password", "email@rhodric.click");
        });
    }

    //NEGATIVE REGISTER
    @Test
    void userExists() throws DataAccessException {
        service.registerUser("Rhodric", "password", "email@rhodric.click");
        assertThrows(DataAccessException.class, () -> service.registerUser("Rhodric", "password", "email@rhodric.click"));
    }

    //POSITIVE LOGIN
    @Test
    void loginTest() throws DataAccessException {
        service.registerUser("Rhodric", "password", "email@rhodric.click");
        assertDoesNotThrow(()->service.login("Rhodric", "password"));

    }

    //NEGATIVE LOGIN
    @Test
    void wrongPassword() throws DataAccessException {
        service.registerUser("Rhodric", "password", "email@rhodric.click");
        assertThrows(DataAccessException.class, () -> service.login("Rhodric", "whoopsie"));
    }


    //POSITIVE LOGOUT
    @Test
    void logoutTest() throws DataAccessException {
        assertDoesNotThrow(() -> {
            AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
            service.logout(myAuthData.authToken());
        });
    }

    //NEGATIVE LOGOUT
    @Test
    void wrongAuthLogout() throws DataAccessException {
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        Integer authInt = Integer.valueOf(myAuthData.authToken());
        Integer newAuthINt = authInt+=1;
        assertThrows(DataAccessException.class, ()->service.logout(String.valueOf(newAuthINt)));
    }

    //POSITIVE CREATEGAME
    @Test
    void createGameTest() throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        service.createGame(myAuthData.authToken(), "myGame");
        service.createGame(myAuthData.authToken(), "myGame");
        service.createGame(myAuthData.authToken(), "myGame");
        var games = service.listGames(myAuthData.authToken());
        assertEquals(3,games.size());
    }

    //NEGATIVE CREATEGAME
    @Test
    void createGameWithWrongAuth() throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        String myAuthToken = myAuthData.authToken();
        int auth = Integer.parseInt(myAuthToken);
        auth++;
        int finalAuth = auth;
        assertThrows(DataAccessException.class , ()->service.createGame(Integer.toString(finalAuth), "myGame"));
    }

    //POSITIVE JOINGAME
    @Test
    void joinGameTest()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        service.createGame(myAuthData.authToken(), "myGame");

    }



    /*
    //POSITIVE CLEAR
    @Test
    void clearCheck() throws DataAccessException {
        String myAuth = service.registerUser("Rhodric", "password", "email@rhodric.click");

        service.clear();
    }
     */
}