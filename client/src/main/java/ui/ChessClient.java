package ui;

import chess.ChessBoard;
import exception.ResponseException;
import model.UserData;
import server.LoginInfo;
import ui.websocket.LoadGameHandler;
import ui.websocket.NotificationHandler;
import webSocketMessages.serverMessages.LoadMessage;

import java.util.Arrays;
import java.util.Objects;

public class ChessClient {
    private String visitorName;
    private final ServerFacade server;
    private final String serverURL;
    private State state = State.SIGNEDOUT;
    private int authToken;
    private loadHandler loader;
    private String currentColor;
    private ChessBoard currentBoard;
    private int currentGameId;

    private final NotificationHandler notificationHandler;
    public ChessClient(String serverURL, NotificationHandler handler){
        this.notificationHandler = handler;
        loader = new loadHandler();
        server = new ServerFacade(serverURL,handler, loader);
        this.serverURL = serverURL;

    }

    class loadHandler implements LoadGameHandler {

        @Override
        public void loadGame(LoadMessage message) {
            currentBoard = message.getBoard();
            if (Objects.equals(currentColor, "white")){
                DrawBoard.printSpace();
                DrawBoard.printWhiteDown(message.getBoard());
            }
            else{
                DrawBoard.printSpace();
                DrawBoard.printBlackDown(message.getBoard());
            }
        }
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
                case "leave" -> leaveGame();
                case "redrawboard" ->redrawBoard();
                case "makemove" -> makeMove(params);
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
            currentColor = color;
            currentGameId = gameID;
            state = State.INGAME;
            return String.format("Successfully joined as %s player", color);
        }
        throw new ResponseException(400, "Expected: <id> [WHITE | BLACK | <empty>]");
    }

    private String joinObserver(String[] params)throws ResponseException{
        assertSignedIn();
        if (params.length == 1){
            state = State.INGAME;
            int gameID = Integer.parseInt(params[0]);
            server.joinAsObserver(gameID);
            return "Joined game as observer succesfully";
        }
        throw new ResponseException(400, "Expected: <id>");
    }

    private String leaveGame()throws ResponseException{
        assertInGame();
        server.leave(currentGameId, currentColor);
        currentGameId = 0;
        return "You left the game.";
    }

    private String redrawBoard()throws ResponseException{
        assertInGame();
        if (currentColor == "black"){
            DrawBoard.printBlackDown(currentBoard);
        }
        else{
            DrawBoard.printWhiteDown(currentBoard);
        }
        return "";
    }

    private String makeMove(String[] params)throws ResponseException{
        assertInGame();
        if (params.length == 3){
            String ogPos = params[0];
            String finalPos = params[1];
            String promoPiece = params[2];
            server.makeMove(ogPos, finalPos, promoPiece, currentGameId);
            return "Move made successfully";
        }
        throw new ResponseException(400, "Expected: <currentPosition> <desiredPosition> <promotionPiece>");
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
        else if (state == State.INGAME){
            return """
                    - makeMove <currentPosition> <desiredPosition> <promotionPiece>
                    - showMoves <position>
                    - redrawBoard
                    - resign
                    - leave
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

    private void assertInGame() throws exception.ResponseException {
        if (state != State.INGAME) {
            throw new exception.ResponseException(400, "You must join a game to use this command");
        }
    }
}
