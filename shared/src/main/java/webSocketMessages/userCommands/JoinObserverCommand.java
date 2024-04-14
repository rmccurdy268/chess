package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand{
    private final int gameId;
    public JoinObserverCommand(int gameId, String authToken) {
        super(CommandType.JOIN_OBSERVER, authToken);
        this.gameId = gameId;
    }

    public int getGameId(){
        return gameId;
    }
}
