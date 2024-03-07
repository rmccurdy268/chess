package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySQLGameDAO;
import org.junit.jupiter.api.BeforeEach;

public class GameDAOTests {
    static final MySQLGameDAO testGameDB = new MySQLGameDAO();

    @BeforeEach
    void clear() throws DataAccessException {
        testGameDB.clearGames();
    }
}

