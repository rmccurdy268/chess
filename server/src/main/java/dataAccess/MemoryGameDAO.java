package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer,GameData> gameMap;
    private final HashMap<Integer, HashSet<String>> observerMap;
    private Integer ogGameID;

    public MemoryGameDAO(){
        gameMap = new HashMap<Integer,GameData>();
        observerMap = new HashMap<Integer, HashSet<String>>();
        ogGameID = 1;
    }
    public HashMap<Integer,GameData> getGames(){
        return gameMap;
    }


   public Integer addGame(String name){
       Integer myGameId = ogGameID++;
        gameMap.put(myGameId, new GameData(myGameId,null,null, name, new ChessGame()));
        return getGame(myGameId).gameID();
   }

    public GameData getGame(int gameID){
        return gameMap.get(gameID);
    }

    //write a test to check that these parameters are valid

    //PLEASE REFACTOR ME
    public void addPlayer(String userName, String teamColor, int gameID) {
        if(Objects.equals(teamColor, "WHITE")){
            GameData myData = new GameData(gameID, userName, gameMap.get(gameID).blackUsername(), gameMap.get(gameID).gameName(),gameMap.get(gameID).implementation());
            gameMap.replace(gameID,gameMap.get(gameID), myData);
        } else if (Objects.equals(teamColor, "BLACK")){
            GameData myData = new GameData(gameID,  gameMap.get(gameID).whiteUsername(), userName, gameMap.get(gameID).gameName(),gameMap.get(gameID).implementation());
            gameMap.replace(gameID,gameMap.get(gameID), myData);
        }
    }

    public void addObserver(String username, int gameID){
        observerMap.computeIfAbsent(gameID, k -> new HashSet<String>());
        observerMap.get(gameID).add(username);
    }

    public void clearGames() {
        gameMap.clear();
        observerMap.clear();
    }
}
