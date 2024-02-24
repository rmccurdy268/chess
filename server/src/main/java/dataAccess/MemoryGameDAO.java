package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    private HashMap<Integer,GameData> gameMap;
    private Integer ogGameID;

    public MemoryGameDAO(){
        gameMap = new HashMap<Integer,GameData>();
    }
    public HashMap<Integer,GameData> getGames(){
        return gameMap;
    }


   public Integer addGame(String name){
       Integer myGameId = ogGameID++;
        gameMap.put(myGameId, new GameData(myGameId,"","",name, new ChessGame()));
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

    public void clearGames() {
        gameMap.clear();
    }
}
