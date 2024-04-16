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

@WebSocket
public class WebSocketHandler {
    private final ChessService service;

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(ChessService service) {
        this.service = service;
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
            String playerName = "";
            String playerColor = "";
            if (command.getColor() == ChessGame.TeamColor.WHITE) {
                playerName = myGame.whiteUsername();
                playerColor = "white";
            } else {
                playerName = myGame.blackUsername();
                playerColor = "black";
            }
            connections.add(playerName, session);
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.implementation().getBoard());
            session.getRemote().sendString(gameMessage.toString());
            var message = String.format("%s has joined the game as the %s player.", playerName, playerColor);
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(playerName, notification);
        } catch (DataAccessException e) {
            var message = "Error: game does not exist";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    public void joinObserver(JoinObserverCommand command, Session session) throws IOException {
        try {
            GameData myGame = service.getGame(command.getAuthString(), command.getGameID());
            String playerName = "";
            AuthData data = service.getUserData(command.getAuthString());
            playerName = data.username();
            connections.add(playerName, session);
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.implementation().getBoard());
            session.getRemote().sendString(gameMessage.toString());
            var message = String.format("%s has started observing the game.", playerName);
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(playerName, notification);

        } catch (DataAccessException e) {
            var message = "Error: game does not exist";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    public void leaveGame(LeaveCommand command, Session session) throws IOException {
        try {
            service.deletePlayer(command.getColor(), command.getId(), command.getAuthString());
            AuthData auth = service.getUserData(command.getAuthString());
            connections.remove(auth.username());
            var message = String.format("%s has left the game.", auth.username());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(auth.username(), notification);
        } catch (DataAccessException e) {
            var message = "Error:User not in game";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    public void makeMove(MakeMoveCommand command, Session session) throws IOException {
        try {
            GameData myGameData = service.getGame(command.getAuthString(), command.getGameId());
            AuthData auth = service.getUserData(command.getAuthString());
            ChessGame myGame = myGameData.implementation();
            ChessMove myMove = command.getMove();
            myGame.makeMove(myMove);
            service.updateGame(myGame, command.getGameId(), auth.authToken());
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.getBoard());
            connections.broadcastAll(gameMessage);
            var message = String.format("%s has moved from %s to %s", auth.username(), command.getOgPos(), command.getFinalPos());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(auth.username(), notification);
        } catch (DataAccessException | InvalidMoveException ex) {
            var message = "Error: Invalid Move";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    public void resign(ResignCommand command, Session session) throws IOException {
        try {
            GameData myGameData = service.getGame(command.getAuthString(), command.getGameId());
            AuthData auth = service.getUserData(command.getAuthString());
            ChessGame myGame = myGameData.implementation();
            myGame.endGame();
            service.updateGame(myGame, command.getGameId(), auth.authToken());
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.getBoard());
            connections.broadcastAll(gameMessage);
            var message = String.format("%s has resigned. Game Over!", auth.username());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(auth.username(), notification);
        } catch (DataAccessException ex) {
            var message = "Error:Couldn't resign";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }
}
