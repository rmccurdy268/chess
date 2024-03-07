package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLGameDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    static final MySQLGameDAO testGameDB = new MySQLGameDAO();


    @BeforeEach
    void clear() throws DataAccessException {
        testGameDB.clearGames();
    }

    @Test
    void addNewGame()throws DataAccessException{
        assertDoesNotThrow(()->testGameDB.addGame("MyGame"));
    }
}

