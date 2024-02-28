package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class ChessService {
    private final GameDAO myGameDAO;
    private final UserDAO myUserDAO;

    public ChessService(){
        myGameDAO = new MemoryGameDAO();
        myUserDAO = new MemoryUserDAO();
    }

    //Registers a user for the first time
    public AuthData registerUser(String username, String password, String email)throws DataAccessException{
        UserData myUserData = myUserDAO.getUser(username);
        if(myUserData == null){
            myUserDAO.createUser(username, password, email);
            String myAuth = myUserDAO.createAuth(username);
            return myUserDAO.getAuthData(myAuth);
        }
        else{
            throw new DataAccessException.AlreadyTakenException();
        }
    }

    //logs in an existing user
    public AuthData login(String username, String password)throws DataAccessException{
        boolean exists = myUserDAO.checkCredentials(username, password);
        if (!exists) {
            throw new DataAccessException.UnauthorizedException();
        }
        UserData myUserData = myUserDAO.getUser(username);
        String myAuth = myUserDAO.getAuthToken(username);

        myAuth = myUserDAO.createAuth(username);
        return myUserDAO.getAuthData(myAuth);
    }

    //logs out an existing user
    public void logout(String authToken)throws DataAccessException{
        if (myUserDAO.checkAuth(authToken)!=null){
            myUserDAO.deleteAuth(authToken);
        }
        else{
            throw new DataAccessException.UnauthorizedException();

        }
    }

    /*
     *
     * GAME FUNCTIONS
     *
     * */

    //creates a new game
    public Integer createGame(String authToken, String gameName)throws DataAccessException{
        if(myUserDAO.checkAuth(authToken) == null){
            throw new DataAccessException.UnauthorizedException();
        }
        return myGameDAO.addGame(gameName);
    }

    //joins game as existing user
    public void joinGame(String authToken, String teamColor, Integer gameID)throws DataAccessException{
        AuthData myUser = myUserDAO.checkAuth(authToken);
        if (myUser == null){
            throw new DataAccessException.UnauthorizedException();
        }
        GameData myGame = myGameDAO.getGame(gameID);
        if(myGame == null){
            throw new DataAccessException.BadRequestException();
        }
        if((teamColor == null)||(teamColor.isEmpty())||(teamColor.equals("empty"))){
            myGameDAO.addObserver(myUser.username(),gameID);
        }
        else if ((teamColor.equals("WHITE"))||(teamColor.equals("BLACK"))){
            if(teamColor.equals("WHITE")){
                if(myGame.whiteUsername()!= null){
                    throw new DataAccessException.AlreadyTakenException();
                }
            }
            else{
                if (myGame.blackUsername() != null) {
                    throw new DataAccessException.AlreadyTakenException();
                }
            }
            myGameDAO.addPlayer(myUser.username(),teamColor,gameID);
        }
        else{
            throw new DataAccessException.BadRequestException();
        }

    }

    //returns a list of all games
    public Collection<GameList> listGames(String authToken)throws DataAccessException{
        AuthData myAuthData = myUserDAO.checkAuth(authToken);
        if(myAuthData == null){
            throw new DataAccessException.UnauthorizedException();
        }
        Collection<GameData> myGames = myGameDAO.getGames().values();
        Collection<GameList> myList = new ArrayList<GameList>();
        for(GameData each:myGames){
            myList.add(new GameList(each.gameID(),each.whiteUsername(), each.blackUsername(), each.gameName()));
        }
        return myList;
    }

    //clear all databases
    public void clear()throws DataAccessException{
        myGameDAO.clearGames();
        myUserDAO.clearUsers();
        myUserDAO.clearAuth();
    }
}
