package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> {
                joinPlayer(command, session);
            }
            case JOIN_OBSERVER -> {
                command = new JoinObserverCommand(command);
                connections.add(command.getAuthString(), session);
            }
            case MAKE_MOVE -> command = new MakeMoveCommand(command);
            case LEAVE -> {
                command = new LeaveCommand(command);
            }
            case RESIGN -> {
                command = new ResignCommand(command);
            }
        }
    }

    public void joinPlayer(UserGameCommand command, Session session){
        connections.add(command.getAuthString(), session);
        var message = String.format("%s is in the shop", );
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(visitorName, notification);
    }
}
