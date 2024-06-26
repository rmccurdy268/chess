package ui.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    LoadGameHandler loader;
    ErrorHandler error;

    private int auth;


    public WebSocketFacade(String url, NotificationHandler notificationHandler, LoadGameHandler loader, ErrorHandler error) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;
            this.loader = loader;
            this.error = error;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage servMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch(servMessage.getServerMessageType()){
                        case NOTIFICATION -> notificationHandler.notify(new Gson().fromJson(message, Notification.class));
                        case LOAD_GAME -> {
                            LoadMessage loadedMessage = new Gson().fromJson(message, LoadMessage.class);
                            loader.loadGame(loadedMessage);
                        }
                        case ERROR -> error.getError(new Gson().fromJson(message, ErrorMessage.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void setAuth(int auth){
        this.auth = auth;
    }


    //public ChessBoard getChessBoard(int gameId){}


    public void joinPlayer(int gameId, String color, int authToken) throws ResponseException {
        try {
            ChessGame.TeamColor teamColor;
            if(color.equalsIgnoreCase("white")){
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("black")) {
                teamColor = ChessGame.TeamColor.BLACK;
            }
            else{
                throw new ResponseException(500,"Wrong color");
            }
            var command = new JoinPlayerCommand(gameId, teamColor, String.valueOf(authToken));
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinObserver(int gameId, int authToken) throws ResponseException{
        try {
            var command = new JoinObserverCommand(gameId, String.valueOf(authToken));
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String ogPos, String finalPos, String promoPiece, int gameId, int authToken)throws ResponseException{
        try{
            var command = new MakeMoveCommand(ogPos, finalPos, promoPiece, gameId, authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch(IOException ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(int gameId, String color, int auth)throws ResponseException{
        try{
            var command = new ResignCommand(gameId, color, auth);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void leave(int gameId, String color, int authToken)throws ResponseException{
        try{
            var command = new LeaveCommand(gameId, color,String.valueOf(authToken));
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch(IOException ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }
}


