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
    public String registerUser(String username, String password, String email)throws DataAccessException{
        UserData myUserData = myUserDAO.getUser(username);
        if(myUserData == null){
            myUserDAO.createUser(username, password, email);
            return myUserDAO.createAuth(username);
        }
        else{
            throw new DataAccessException("User Exists");
        }

    }

    public AuthData login(String username, String password)throws DataAccessException{
        boolean exists = myUserDAO.checkCredentials(username, password);
        myUserDAO.createAuth(username);
        return myUserDAO.checkAuth(username);
    }

    public Collection<GameData> listGames(String authToken)throws DataAccessException{
        myUserDAO.checkAuth(authToken);
        return myGameDAO.getGames().values();
    }

    public void logout(String authToken)throws DataAccessException{
        myUserDAO.checkAuth(authToken);
        myUserDAO.deleteAuth(authToken);
    }

    public Integer createGame(String authToken, String gameName)throws DataAccessException{
        myUserDAO.checkAuth(authToken);
        return myGameDAO.addGame(gameName);
    }

    public void joinGame(String authToken, String teamColor, Integer gameID)throws DataAccessException{
        AuthData myUser = myUserDAO.checkAuth(authToken);
        GameData myGame = myGameDAO.getGame(gameID);
        myGameDAO.addPlayer(myUser.username(),teamColor,gameID);
    }

    public void clear()throws DataAccessException{
        myGameDAO.clearGames();
        myUserDAO.clearUsers();
        myUserDAO.clearAuth();
    }
}
