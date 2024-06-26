package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.ChessService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ChessService service;

    private final static Library library = new Library();

    public WebSocketHandler(ChessService service) {
        this.service = service;
        for(int i = 1; i < 10; i++){
            library.addLobby(i,new Lobby());
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> {
                var newCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
                joinPlayer(newCommand, session);
            }
            case JOIN_OBSERVER -> {
                var newCommand = new Gson().fromJson(message, JoinObserverCommand.class);
                joinObserver(newCommand, session);
            }
            case MAKE_MOVE -> {
                var newCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(newCommand, session);
            }
            case LEAVE -> {
                var newCommand = new Gson().fromJson(message, LeaveCommand.class);
                leaveGame(newCommand, session);
            }
            case RESIGN -> {
                var newCommand = new Gson().fromJson(message, ResignCommand.class);
                resign(newCommand, session);
            }
        }
    }

    public void joinPlayer(JoinPlayerCommand command, Session session) throws IOException {
        try {
            GameData myGame = service.getGame(command.getAuthString(), command.getGameId());
            if (myGame == null) {
                var message = "Error: Game Doesn't exist;";
                var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
                session.getRemote().sendString(errMessage.toString());
                throw new IOException("Game Doesn't Exist");
            }
            AuthData myAuth = service.getUserData(command.getAuthString());
            String playerName = null;
            try{
                playerName = confirmPlayer(command.getColor(), myAuth.username(), myGame);
            }
            catch(IOException ex){
                var message = "Error: Spot not reserved;";
                var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
                session.getRemote().sendString(errMessage.toString());
                throw new IOException("Not reserved");
            }
            String playerColor = "";
            if (playerName == null) {
                var message = "Error: Spot Taken";
                var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
                session.getRemote().sendString(errMessage.toString());
            } else {
                if (command.getColor() == ChessGame.TeamColor.WHITE) {
                    playerName = myGame.whiteUsername();
                    playerColor = "white";
                } else if (command.getColor() == ChessGame.TeamColor.BLACK) {
                    playerName = myGame.blackUsername();
                    playerColor = "black";
                }
                library.add(playerName, session, command.getGameId());
                var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.implementation().getBoard());
                session.getRemote().sendString(gameMessage.toString());
                var message = String.format("%s has joined the game as the %s player.", playerName, playerColor);
                var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
                library.getLobby(command.getGameId()).broadcast(playerName, notification);
            }

        } catch (DataAccessException e) {
            var message = "Error: game does not exist";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    public void joinObserver(JoinObserverCommand command, Session session) throws IOException {
        try {
            GameData myGame = service.getGame(command.getAuthString(), command.getGameID());
            if(myGame==null){
                var message = "Error: game does not exist";
                var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
                session.getRemote().sendString(errMessage.toString());
                throw new IOException("Game Does not Exist");
            }
            String playerName = "";
            AuthData data = service.getUserData(command.getAuthString());
            playerName = data.username();
            library.add(playerName, session, command.getGameID());
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.implementation().getBoard());
            session.getRemote().sendString(gameMessage.toString());
            var message = String.format("%s has started observing the game.", playerName);
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            library.getLobby(command.getGameID()).broadcast(playerName, notification);

        } catch (DataAccessException e) {
            var message = "Error: game does not exist";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    public void leaveGame(LeaveCommand command, Session session) throws IOException {
        AuthData auth =null;
        try {
            auth = service.getUserData(command.getAuthString());
            service.deletePlayer(command.getColor(), command.getId(), command.getAuthString());

        }
        catch (DataAccessException ignored) {
        }
        finally {
            library.getLobby(command.getId()).remove(auth.username());
            var message = String.format("%s has left the game.", auth.username());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            library.getLobby(command.getId()).broadcast(auth.username(), notification);

        }
    }

    public void makeMove(MakeMoveCommand command, Session session) throws IOException {
        var errorMessage = "";
        try {
            GameData myGameData = service.getGame(command.getAuthString(), command.getGameId());
            AuthData auth = service.getUserData(command.getAuthString());
            ChessGame myGame = myGameData.implementation();
            ChessMove myMove = command.getMove();
            ChessGame.TeamColor currentTurn = myGame.getTeamTurn();
            String currentPlayer ="";

            if(currentTurn == ChessGame.TeamColor.WHITE){
                currentPlayer = myGameData.whiteUsername();
            } else if (currentTurn == ChessGame.TeamColor.BLACK){
                currentPlayer = myGameData.blackUsername();
            }
            else{
                errorMessage = "Error: Game is over.";
                throw new InvalidMoveException("Game is over");

            }
            if(!Objects.equals(currentPlayer, auth.username())){
                errorMessage = "Error: You may not make a move when it's not your turn.";
                throw new InvalidMoveException("wrong turn");
            }
            myGame.makeMove(myMove);
            service.updateGame(myGame, command.getGameId(), auth.authToken());
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.getBoard());
            library.getLobby(command.getGameId()).broadcastAll(gameMessage);
            var message = String.format("%s has moved from %s to %s", auth.username(), command.getOgPos(), command.getFinalPos());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            library.getLobby(command.getGameId()).broadcast(auth.username(), notification);
        } catch (DataAccessException | InvalidMoveException ex) {
            if(errorMessage.isEmpty()){
                var message = "Error: Invalid Move";
                }
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    public void resign(ResignCommand command, Session session) throws IOException {
        try {
            GameData myGameData = service.getGame(command.getAuthString(), command.getGameId());
            AuthData auth = service.getUserData(command.getAuthString());
            ChessGame myGame = myGameData.implementation();
            if ((!Objects.equals(auth.username(), myGameData.whiteUsername()))&&(!Objects.equals(auth.username(), myGameData.blackUsername()))){
                throw new DataAccessException("observer can not resign",400);
            }
            if (myGame.getTeamTurn() == ChessGame.TeamColor.NONE){
                throw new DataAccessException("game is already over",400);
            }
            myGame.endGame();
            service.updateGame(myGame, command.getGameId(), auth.authToken());
            var message = String.format("%s has resigned. Game Over!", auth.username());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            library.getLobby(command.getGameId()).notifyAll(notification);
        } catch (DataAccessException ex) {
            var message = "Error:Couldn't resign";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    //helper

    //returns players name if correct, null if not correct
    public String confirmPlayer(ChessGame.TeamColor color, String name, GameData game) throws IOException {
        if(game.whiteUsername() == null){
            throw new IOException("Spot not reserved");
        }
        if (color == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername().equals(name)) {
                return name;
            } else {
                throw new IOException("Spot not reserved");
            }
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername().equals(name)) {
                return name;
            } else {
                throw new IOException("Spot not reserved");
            }
        }
        return null;
    }
}
