package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.*;
import ui.websocket.ErrorHandler;
import ui.websocket.LoadGameHandler;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class ServerFacade {
    private final String serverUrl;
    private int auth;
    private WebSocketFacade ws;
    private LoadGameHandler loader;
    private ErrorHandler error;
    private HashMap<Character,Integer> boardMap;


    private final NotificationHandler notificationHandler;

    public ServerFacade(String url, NotificationHandler handler, LoadGameHandler loader, ErrorHandler error){
        serverUrl = url;
        notificationHandler = handler;
        this.error = error;
        this.loader = loader;
    }
    public int addUser(UserData data)throws ResponseException {
        var path = "/user";
        AuthData myAuth = this.makeRequest("POST", path, data, AuthData.class, false);
        auth = Integer.parseInt(myAuth.authToken());
        return auth;
    }

    public int login(LoginInfo data)throws ResponseException {
        var path = "/session";
        AuthData myAuth = this.makeRequest("POST", path, data, AuthData.class, false);
        auth = Integer.parseInt(myAuth.authToken());
        return auth;
    }

    public void logout()throws ResponseException{
        var path = "/session";
        this.makeRequest("DELETE", path,null,null, true);
    }

    public int createGame(String gameName)throws ResponseException{
        var path = "/game";
        GameWithName mygame = new GameWithName(gameName);
        GameWithID myID = this.makeRequest("POST", path, mygame, GameWithID.class, true);
        return myID.gameID();
    }

    public String listGames() throws ResponseException{
        var path = "/game";
        ListArray games = this.makeRequest("GET", path, null, ListArray.class,true);
        return games.makeString();
    }

    public void leave(int gameId, String color)throws ResponseException{
        ws.leave(gameId,color,auth);
    }

    public void joinAsPlayer(int gameID, String color) throws ResponseException {
        try{
            var path = "/game";
            JoinTeamInput input = new JoinTeamInput(color, gameID);
            this.makeRequest("PUT", path, input, null, true);
        }
        catch(ResponseException ignored){}
        finally{
            ws = new WebSocketFacade(serverUrl, notificationHandler, loader, error);
            ws.joinPlayer(gameID, color,auth);
        }

    }

    public void joinAsObserver(int gameID) throws ResponseException{
        var path = "/game";
        JoinTeamInput input = new JoinTeamInput(null, gameID);
        this.makeRequest("PUT", path, input, null, true);
        ws = new WebSocketFacade(serverUrl, notificationHandler, loader, error);
        ws.joinObserver(gameID,auth);
    }

    public void makeMove(String ogPos, String finalPos, String promoPiece, int gameId) throws ResponseException {
        ws = new WebSocketFacade(serverUrl, notificationHandler, loader, error);
        ws.makeMove(ogPos, finalPos, promoPiece, gameId, auth);
    }

    public void resign(int gameId, String color) throws ResponseException {
        ws = new WebSocketFacade(serverUrl,notificationHandler, loader, error);
        ws.resign(gameId, color, auth);
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, false);
    }






    //HTTP FUNCTIONS


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, boolean needsAuth) throws exception.ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeBody(request, http, needsAuth, auth);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new exception.ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http, boolean needsAuth, int authToken) throws IOException {
        if (needsAuth){
            http.addRequestProperty("Authorization", String.valueOf(authToken));
        }
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");

            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, exception.ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new exception.ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
