package server.websocket;

import chess.*;
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
import java.util.HashMap;
import java.util.Objects;

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
                joinObserver(newCommand,session);
            }
            case MAKE_MOVE -> {
                var newCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(newCommand, session);
            }
            case LEAVE -> {
                var newCommand = new Gson().fromJson(message, LeaveCommand.class);
                leaveGame(newCommand, session);
            }
            case RESIGN -> command = new Gson().fromJson(message, ResignCommand.class);
        }
    }

    public void joinPlayer(JoinPlayerCommand command, Session session) throws IOException {
        try {
            GameData myGame = service.getGame(command.getAuthString(), command.getGameId());
            String playerName = "";
            if (Objects.equals(command.getColor(), "white")) {
                playerName = myGame.whiteUsername();
            } else {
                playerName = myGame.blackUsername();
            }
            connections.add(playerName, session);
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.implementation().getBoard());
            session.getRemote().sendString(gameMessage.toString());
            var message = String.format("%s has joined the game as the %s player.", playerName, command.getColor());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(playerName, notification);
        } catch (DataAccessException e) {
            var message = "Error: game does not exist";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }

    public void joinObserver(JoinObserverCommand command, Session session)throws IOException{
        try {
            GameData myGame = service.getGame(command.getAuthString(), command.getGameId());
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
            ChessMove myMove = createChessMove(command.getOgPos(), command.getFinalPos(), command.getPromoPiece());
            myGame.makeMove(myMove);
            service.updateGame(myGame, command.getGameId(), auth.authToken());
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.getBoard());
            connections.broadcastAll(gameMessage);
            var message = String.format("%s has moved from %s to %s", auth.username(), command.getOgPos(), command.getFinalPos());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(auth.username(), notification);
        }
        catch (DataAccessException | InvalidMoveException ex) {
            var message = "Error: Invalid Move";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }
    public ChessMove createChessMove(String ogPos, String finalPos, String promoPiece){
        HashMap<Character, Integer> boardMap = new HashMap<>();
        boardMap.put('a', 1);
        boardMap.put('b', 2);
        boardMap.put('c', 3);
        boardMap.put('d', 4);
        boardMap.put('e', 5);
        boardMap.put('f', 6);
        boardMap.put('g', 7);
        boardMap.put('h', 8);
        int ogFirst = boardMap.get(ogPos.charAt(0));
        int ogSecond = Character.getNumericValue(ogPos.charAt(1));
        int finalFirst = boardMap.get(finalPos.charAt(0));
        int finalSecond = Character.getNumericValue(finalPos.charAt(1));
        ChessPosition firstPos = new ChessPosition(ogSecond, ogFirst);
        ChessPosition secondPos = new ChessPosition(finalSecond, finalFirst);
        ChessPiece.PieceType myType = null;
        switch(promoPiece){
            case "queen"-> myType = ChessPiece.PieceType.QUEEN;
            case "rook"-> myType = ChessPiece.PieceType.ROOK;
            case "knight"-> myType = ChessPiece.PieceType.KNIGHT;
            case "bishop"-> myType = ChessPiece.PieceType.BISHOP;
            case "king"-> myType = ChessPiece.PieceType.KING;
            case "pawn"-> myType = ChessPiece.PieceType.PAWN;
        }
        return new ChessMove(firstPos,secondPos,myType);
    }
}
