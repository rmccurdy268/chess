package ui;

import exception.ResponseException;
import model.UserData;
import server.LoginInfo;
import ui.websocket.NotificationHandler;

import java.util.Arrays;

public class ChessClient {
    private String visitorName;
    private final ServerFacade server;
    private final String serverURL;
    private State state = State.SIGNEDOUT;
    private int authToken;

    private final NotificationHandler notificationHandler;
    public ChessClient(String serverURL, NotificationHandler handler){
        this.notificationHandler = handler;
        server = new ServerFacade(serverURL,handler);
        this.serverURL = serverURL;

    }
    public String eval(String input){
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> joinObserver(params);

                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String login(String[] params)throws ResponseException{
        if (params.length >= 2) {
            state = State.SIGNEDIN;
            var name = params[0];
            var password = params[1];
            var userData = new LoginInfo(name, password);
            authToken = server.login(userData);

            return String.format("Logged in successfully. %d is your authorization token.", authToken);
        }
        throw new ResponseException(400, "Expected: <name> <password>");
    }

    private String register(String[] params)throws ResponseException{
        if (params.length >= 3) {
            try{
                state = State.SIGNEDIN;
                var name = params[0];
                var password = params[1];
                var email = params[2];
                var userData = new UserData(name, password, email);
                authToken = server.addUser(userData);
                return String.format("Registered successfully. %d is your authorization token.", authToken);
            }
            catch(ResponseException e){
                state = State.SIGNEDOUT;
            }
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    private String logout(String[] params) throws ResponseException{
        assertSignedIn();
        server.logout();
        state = State.SIGNEDOUT;
        authToken = 0;
        return "Successfully logged out";
    }

    private String createGame(String[] params) throws ResponseException{
        assertSignedIn();
        if (params.length == 1){
            var gameName = params[0];
            int gameNum = server.createGame(gameName);
            return String.format("Game created successfully. Game ID is %d.", gameNum);
        }
        throw new ResponseException(400, "Expected: <name>");
    }

    private String listGames() throws ResponseException{
        assertSignedIn();
        return server.listGames();
    }

    private String joinGame(String[] params) throws ResponseException{
        assertSignedIn();
        if (params.length == 2){
            state = State.SIGNEDIN;
            int gameID = Integer.parseInt(params [0]);
            String color = params[1];
            server.joinAsPlayer(gameID, color);
            renderBoard();
            return String.format("Successfully joined as %s player", color);
        }
        throw new ResponseException(400, "Expected: <id> [WHITE | BLACK | <empty>]");
    }

    private String joinObserver(String[] params)throws ResponseException{
        assertSignedIn();
        if (params.length == 1){
            int gameID = Integer.parseInt(params[0]);
            server.joinAsObserver(gameID);
            renderBoard();
            return "Joined game as observer succesfully";
        }
        throw new ResponseException(400, "Expected: <id>");
    }


    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - login <USERNAME> <PASSWORD>
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - quit
                    - help
                    """;
        }
        return """
                - create <NAME>
                - list
                - join <id> [WHITE | BLACK | <empty>]
                - observe <id>
                - logout
                - quit
                - help
                """;
    }

    public void renderBoard(){
        DrawBoard.drawBoard();
    }

    private void assertSignedIn() throws exception.ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new exception.ResponseException(400, "You must sign in");
        }
    }
}
