package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand{

    private final int gameID;
    private final ChessGame.TeamColor playerColor;


    public JoinPlayerCommand(int gameID, ChessGame.TeamColor playerColor, String authToken) {
        super(CommandType.JOIN_PLAYER, authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameId(){
        return gameID;
    }

    public ChessGame.TeamColor getColor(){
        return playerColor;
    }
}
