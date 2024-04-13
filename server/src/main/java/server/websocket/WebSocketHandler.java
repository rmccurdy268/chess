package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
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

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(ChessService service){
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
                command = new Gson().fromJson(message, JoinObserverCommand.class);
                connections.add(command.getAuthString(), session);
            }
            case MAKE_MOVE -> command = new Gson().fromJson(message, MakeMoveCommand.class);
            case LEAVE -> command = new Gson().fromJson(message, LeaveCommand.class);
            case RESIGN -> command = new Gson().fromJson(message, ResignCommand.class);
        }
    }

    public void joinPlayer(JoinPlayerCommand command, Session session) throws IOException {
        try{
            GameData myGame = service.getGame(command.getAuthString(), command.getGameId());
            String playerName = "";
            if (Objects.equals(command.getColor(), "white")){
                playerName = myGame.whiteUsername();
            }
            else{
                playerName = myGame.blackUsername();
            }
            connections.add(playerName, session);
            var gameMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, myGame.implementation().getBoard());
            session.getRemote().sendString(gameMessage.toString());
            var message = String.format("%s has joined the game as the %s player.", playerName, command.getColor());
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(playerName,notification);
        }
        catch(DataAccessException e){
            var message = "Error: game does not exist";
            var errMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(errMessage.toString());
        }
    }
}
