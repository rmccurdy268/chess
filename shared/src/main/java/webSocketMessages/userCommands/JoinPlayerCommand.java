package webSocketMessages.userCommands;

public class JoinPlayerCommand extends UserGameCommand{

    private final int gameId;
    private final String color;


    public JoinPlayerCommand(int gameId, String color, String authToken) {
        super(CommandType.JOIN_PLAYER, authToken);
        this.gameId = gameId;
        this.color = color;
    }

    public int getGameId(){
        return gameId;
    }

    public String getColor(){
        return color;
    }
}
