package dataAccess;


import model.GameData;

import java.util.HashMap;

public interface GameDAO {
    HashMap<Integer, GameData> getGames() throws DataAccessException;

    Integer addGame(String name) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void addPlayer(String userName, String teamColor, int gameID) throws DataAccessException;

    void addObserver(String userName, int gameId) throws DataAccessException;

    void clearGames() throws DataAccessException;

}
