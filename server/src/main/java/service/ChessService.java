package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

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
            throw new DataAccessException("User Exists");
        }
    }

    //logs in an existing user
    public AuthData login(String username, String password)throws DataAccessException{
        boolean exists = myUserDAO.checkCredentials(username, password);
        if (!exists) {
            throw new DataAccessException("Wrong username/password");
        }
        UserData myUserData = myUserDAO.getUser(username);
        String myAuth = myUserDAO.getAuthToken(username);
        return myUserDAO.getAuthData(myAuth);
    }

    //logs out an existing user
    public void logout(String authToken)throws DataAccessException{
        if (myUserDAO.checkAuth(authToken)!=null){
            myUserDAO.deleteAuth(authToken);
        }
        else{
            throw new DataAccessException("auth not in database");

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
            throw new DataAccessException("Incorrect AuthToken");
        }
        return myGameDAO.addGame(gameName);
    }

    //joins game as existing user
    public void joinGame(String authToken, String teamColor, Integer gameID)throws DataAccessException{
        AuthData myUser = myUserDAO.checkAuth(authToken);
        GameData myGame = myGameDAO.getGame(gameID);
        myGameDAO.addPlayer(myUser.username(),teamColor,gameID);
    }

    //returns a list of all games
    public Collection<GameData> listGames(String authToken)throws DataAccessException{
        myUserDAO.checkAuth(authToken);
        return myGameDAO.getGames().values();
    }

    //clear all databases
    public void clear()throws DataAccessException{
        myGameDAO.clearGames();
        myUserDAO.clearUsers();
        myUserDAO.clearAuth();
    }
}
