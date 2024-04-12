package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.UserData;
import service.ChessService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Map;

public class Server {

    private final ChessService service;

    public Server(){
            service = new ChessService();
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.webSocket("/connect", Server.class);

        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));

        Spark.staticFiles.location("web");
        Spark.init();
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game",this::joinGame);
        Spark.delete("/session", this::logout);
        Spark.delete("/db", this::clear);
        Spark.exception(DataAccessException.class, this::exceptionHandler);
        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void exceptionHandler(DataAccessException ex, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", ex.getMessage()));
        res.status(ex.StatusCode());
        res.body(body);
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
        var myUserData = new Gson().fromJson(req.body(), UserData.class);
        if((myUserData.username() == null)||(myUserData.password() == null)|| (myUserData.email() == null)){
            throw new DataAccessException.BadRequestException();
        }
        var myAuthData = service.registerUser(myUserData.username(), myUserData.password(), myUserData.email());
        res.status(200);
        return new Gson().toJson(myAuthData);
    }

    private Object login(Request req, Response res) throws DataAccessException{
        LoginInfo loginInfo = new Gson().fromJson(req.body(), LoginInfo.class);
        var myAuthData = service.login(loginInfo.username(), loginInfo.password());
        res.status(200);
        return new Gson().toJson(myAuthData);
    }

    private Object logout(Request req, Response res) throws DataAccessException{
        String authToken = req.headers("Authorization");
        service.logout(authToken);
        res.status(200);
        return "{}";
    }
    private Object createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        GameWithName gameName = new Gson().fromJson(req.body(), GameWithName.class);
        Integer gameInt = service.createGame(authToken, gameName.gameName());
        res.status(200);
        return new Gson().toJson(new GameWithID(gameInt));
    }

    private Object joinGame(Request req, Response res) throws DataAccessException{
        String authToken = req.headers("Authorization");
        JoinTeamInput input = new Gson().fromJson(req.body(),JoinTeamInput.class);
        service.joinGame(authToken,input.playerColor(), input.gameID());
        res.status(200);
        return "{}";
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        res.status(200);
        return new Gson().toJson(new ListArray(service.listGames(authToken)));
    }


    private Object clear(Request req, Response res) throws DataAccessException{
        service.clear();
        res.status(200);
        return "{}";
    }
}
