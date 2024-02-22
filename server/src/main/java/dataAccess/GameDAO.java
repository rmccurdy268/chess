package dataAccess;


import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGames() throws DataAccessException;

    GameData addGame(String name) throws DataAccessException;

    GameData getGame(String gameID) throws DataAccessException;

    String getPlayer(String teamColor, String gameID) throws DataAccessException;

    void addPlayer(String userName, String teamColor, String gameID) throws DataAccessException;

    void clearGames() throws DataAccessException;

}
