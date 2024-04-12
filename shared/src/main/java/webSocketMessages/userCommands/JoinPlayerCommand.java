package webSocketMessages.userCommands;

public class JoinPlayerCommand extends UserGameCommand{

    private int gameId;
    private String color;

    private String authToken;

    public JoinPlayerCommand(int gameId, String color, String authToken) {
        super(CommandType.JOIN_PLAYER, authToken);
        this.gameId = gameId;
        this.color = color;
    }
}
