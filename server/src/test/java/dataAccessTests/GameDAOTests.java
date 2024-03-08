package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    static final MySQLGameDAO testGameDB = new MySQLGameDAO();



    @BeforeEach
    void clear() throws DataAccessException {
        testGameDB.clearGames();
    }

    //positive and negative add game tests
    @Test
    void addNewGame()throws DataAccessException{
        testGameDB.addGame("MyGame");
        testGameDB.addGame("MyGame");
        testGameDB.addGame("MyGame");
        assertEquals(3, testGameDB.getGames().size());
    }

    @Test
    void addGameWithInvalidInput()throws DataAccessException{
        assertThrows(DataAccessException.class, ()->testGameDB.addGame(null));
    }

    //positive and negative get games list tests
    @Test
    void listGamesSuccess()throws DataAccessException{
        testGameDB.addGame("MyGame");
        testGameDB.addGame("MyGame");
        testGameDB.addGame("MyGame");
        assertDoesNotThrow(testGameDB::getGames);
    }

    @Test
    void listGamesFailure()throws DataAccessException{
        HashMap<Integer, GameData> myList = testGameDB.getGames();
        assertEquals(0,myList.size());
    }

    //positive and negative get single game tests
    @Test
    void getSingleGameSuccess()throws DataAccessException{
        int gameId = testGameDB.addGame("MyGame");
        GameData myGameData = testGameDB.getGame(gameId);
        assertEquals("MyGame", myGameData.gameName());
    }

    @Test
    void getNonexistentGame()throws DataAccessException{
        GameData myGameData = testGameDB.getGame(1000);
        assertNull(myGameData);
    }

    //add player to game positive and negative

    @Test
    void successfullyAddPlayer()throws DataAccessException{
        int gameId = testGameDB.addGame("MyGame");
        assertDoesNotThrow(()->testGameDB.addPlayer("Rhodric","WHITE",gameId));
    }

    @Test
    void addPlayerWithWrongTeamColor()throws DataAccessException{
        int gameID = testGameDB.addGame("MyGame");
        assertThrows(DataAccessException.class, ()->testGameDB.addPlayer("Rhodric","yellow",gameID));
    }

    // positive and negative add observer tests
    @Test
    void successfullyAddObserver()throws DataAccessException{
        int gameID = testGameDB.addGame("MyGame");
        assertDoesNotThrow(()->testGameDB.addObserver("Rhodric",gameID));
    }

    @Test
    void joinNonexistentGameAsObserver()throws DataAccessException{
        assertThrows(DataAccessException.class,()->testGameDB.addObserver("Rhodric",27));
    }

    @Test
    void clearSuccess() throws DataAccessException {
        int gameID = testGameDB.addGame("MyGame");
        testGameDB.clearGames();
        assertEquals(0, testGameDB.getGames().size());
    }
}

