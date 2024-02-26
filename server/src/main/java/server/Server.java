package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.UserData;
import service.ChessService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

    private final ChessService service;

    public Server(){
        service = new ChessService();
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.init();
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.post("/game", this::createGame);
        Spark.delete("/session", this::logout);
        Spark.delete("/db", this::clear);
        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
        var myUserData = new Gson().fromJson(req.body(), UserData.class);
        var myAuthData = service.registerUser(myUserData.username(), myUserData.password(), myUserData.email());
        return new Gson().toJson(myAuthData);
    }

    private Object login(Request req, Response res) throws DataAccessException{
        LoginInfo loginInfo = new Gson().fromJson(req.body(), LoginInfo.class);
        var myAuthData = service.login(loginInfo.username(), loginInfo.password());
        return new Gson().toJson(myAuthData);
    }

    private Object logout(Request req, Response res) throws DataAccessException{
        String authToken = req.headers("Authorization");
        service.logout(authToken);
        res.status(200);
        return "";
    }
    private Object createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        gameWithName gameName = new Gson().fromJson(req.body(), gameWithName.class);
        res.status(200);
        Integer gameInt = service.createGame(authToken, gameName.gameName());
        return new Gson().toJson(new gameWithID(gameInt));
    }

    private Object clear(Request req, Response res) throws DataAccessException{
        service.clear();
        res.status(200);
        return "";
    }
}
