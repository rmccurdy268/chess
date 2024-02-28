package java.serviceTests;

import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ChessService;
import service.GameList;

import java.util.ArrayList;
import java.util.Collection;

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
        int authInt = Integer.parseInt(myAuthData.authToken());
        Integer newAuthINt = authInt+=1;
        assertThrows(DataAccessException.class, ()->service.logout(String.valueOf(newAuthINt)));
    }

    //POSITIVE CREATE GAME
    @Test
    void createGameTest() throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        service.createGame(myAuthData.authToken(), "myGame");
        service.createGame(myAuthData.authToken(), "myGame");
        service.createGame(myAuthData.authToken(), "myGame");
        var games = service.listGames(myAuthData.authToken());
        assertEquals(3,games.size());
    }

    //NEGATIVE CREATE GAME
    @Test
    void createGameWithWrongAuth() throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        String myAuthToken = myAuthData.authToken();
        int auth = Integer.parseInt(myAuthToken);
        auth++;
        int finalAuth = auth;
        assertThrows(DataAccessException.class , ()->service.createGame(Integer.toString(finalAuth), "myGame"));
    }

    //POSITIVE JOIN GAME TESTS
    @Test
    void joinGameAsPlayerTest()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        Integer gameId = service.createGame(myAuthData.authToken(), "myGame");
        assertDoesNotThrow(()->service.joinGame(myAuthData.authToken(), "WHITE",gameId));
    }

    @Test
    void joinGameAsBothPlayers()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        Integer gameId = service.createGame(myAuthData.authToken(), "myGame");
        assertDoesNotThrow(()->service.joinGame(myAuthData.authToken(), "WHITE",gameId));
        assertDoesNotThrow(()->service.joinGame(myAuthData.authToken(), "BLACK",gameId));
    }

    //NEGATIVE JOIN GAME TESTS
    @Test
    void joinGameWithInvalidAuth()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        Integer gameId = service.createGame(myAuthData.authToken(), "myGame");
        int fakeAuthInt = Integer.parseInt(myAuthData.authToken());
        String fakeAuthString = String.valueOf(fakeAuthInt + 1000);
        assertThrows(DataAccessException.class, ()->service.joinGame(fakeAuthString,"WHITE",gameId));
    }


    //POSITIVE JOIN GAME AS OBSERVER TESTS
    @Test
    void joinGameAsObserver()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        Integer gameId = service.createGame(myAuthData.authToken(), "myGame");
        assertDoesNotThrow(()->service.joinGame(myAuthData.authToken(),null,gameId));
    }

    @Test
    void joinMultipleObservers()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        Integer gameId = service.createGame(myAuthData.authToken(), "myGame");
        assertDoesNotThrow(()->service.joinGame(myAuthData.authToken(),null,gameId));
        AuthData newAuth = service.registerUser("Robert", "password", "email@rhodric.click");
        assertDoesNotThrow(()->service.joinGame(newAuth.authToken(),null,gameId));
    }

    //NEGATIVE JOINGAMEASOBSERVER
    @Test
    void joinGameWithBadTeamInput()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        Integer gameId = service.createGame(myAuthData.authToken(), "myGame");
        assertThrows(DataAccessException.class, ()->service.joinGame(myAuthData.authToken(),"thisiswrong",gameId));
    }

    @Test
    void joinGameWithBadGameNumber()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        Integer gameId = service.createGame(myAuthData.authToken(), "myGame") + 1;
        assertThrows(DataAccessException.class, ()->service.joinGame(myAuthData.authToken(),"WHITE",gameId));
    }

    //POSITIIVE LIST TEST
    @Test
    void listAllGames()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        ArrayList<Integer> listOfGameIds = new ArrayList<Integer>(5);
        Integer gameId0 = service.createGame(myAuthData.authToken(), "myGame");
        Integer gameId1 = service.createGame(myAuthData.authToken(), "myGame");
        Integer gameId2 = service.createGame(myAuthData.authToken(), "myGame");
        Integer gameId3 = service.createGame(myAuthData.authToken(), "myGame");
        Integer gameId4 = service.createGame(myAuthData.authToken(), "myGame");
        listOfGameIds.add(gameId0);
        listOfGameIds.add(gameId1);
        listOfGameIds.add(gameId2);
        listOfGameIds.add(gameId3);
        listOfGameIds.add(gameId4);
        Collection<GameList> testList = service.listGames(myAuthData.authToken());
        assertEquals(5, testList.size());
        for (GameList listItem:testList){
            assertTrue(listOfGameIds.contains(listItem.gameID()));
        }
    }

    //NEGATIVE GAME LIST- WRONG AUTH TOKEN
    @Test
    void listWrongAuth()throws DataAccessException{
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        int fakeAuthInt = Integer.parseInt(myAuthData.authToken());
        String fakeAuthString = String.valueOf(fakeAuthInt + 1000);
        service.createGame(myAuthData.authToken(), "myGame");
        assertThrows(DataAccessException.class,()->service.listGames(fakeAuthString));
    }




    //POSITIVE CLEAR
    @Test
    void clearCheck() throws DataAccessException {
        AuthData myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        AuthData myAuthData1 = service.registerUser("Jarrett", "password", "email@rhodric.click");
        AuthData myAuthData2 = service.registerUser("Madison", "password", "email@rhodric.click");
        AuthData myAuthData3 = service.registerUser("Kamrynn", "password", "email@rhodric.click");

        service.createGame(myAuthData.authToken(), "myGame");
        service.createGame(myAuthData.authToken(), "myGame");
        service.createGame(myAuthData.authToken(), "myGame");
        service.createGame(myAuthData.authToken(), "myGame");
        service.createGame(myAuthData.authToken(), "myGame");
        service.clear();
        AuthData finalMyAuthData = myAuthData;
        assertThrows(DataAccessException.class, ()->service.listGames(finalMyAuthData.authToken()));
        myAuthData = service.registerUser("Rhodric", "password", "email@rhodric.click");
        assertEquals(0,service.listGames(myAuthData.authToken()).size());
    }
}
