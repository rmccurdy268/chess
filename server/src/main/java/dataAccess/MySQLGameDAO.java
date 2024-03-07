package dataAccess;

import model.GameData;

import java.sql.SQLException;
import java.util.HashMap;

public class MySQLGameDAO implements GameDAO{

    public MySQLGameDAO(){
        try{
            configureDatabase();
        }
        catch(DataAccessException ex){System.out.print("couldnt configure gameDAO");}
    }

    public HashMap<Integer, GameData> getGames() throws DataAccessException {
        return null;
    }

    public Integer addGame(String name) throws DataAccessException {
        return null;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public void addPlayer(String userName, String teamColor, int gameID) throws DataAccessException {

    }

    public void addObserver(String userName, int gameId) throws DataAccessException {

    }

    public void clearGames() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  gamesDB (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `gameName` varchar(256) NOT NULL,
              `whiteUser` TEXT DEFAULT NULL,
              `blackUser` TEXT DEFAULT NULL,
              `chessGameJson` TEXT NOT NULL,
              `observersJson` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
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
