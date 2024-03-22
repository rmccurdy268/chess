package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private String visitorName;
    private final ServerFacade server;
    private final String serverURL;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverURL){
        server = new ServerFacade(serverURL);
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
                case "creategame" -> createGame(params);
                case "listgames" -> listGames();
                case "joingame" -> joinGame();
                case "joinObserver" -> joinObserver(params);

                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String login(String[] params){
        return null;
    }

    private String register(String[] params) {
        return null;
    }

    private String logout(String[] params) throws ResponseException{
        assertSignedIn();
        return null;
    }

    private String createGame(String[] params) throws ResponseException{
        assertSignedIn();
        return null;
    }

    private String listGames() throws ResponseException{
        assertSignedIn();
        return null;
    }

    private String joinGame() throws ResponseException{
        assertSignedIn();
        return null;
    }

    private String joinObserver(String[] params)throws ResponseException{
        assertSignedIn();
        return null;
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
    private void assertSignedIn() throws exception.ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new exception.ResponseException(400, "You must sign in");
        }
    }
}
