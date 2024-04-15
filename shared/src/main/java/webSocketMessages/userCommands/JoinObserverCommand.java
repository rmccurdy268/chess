package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand{
    private final int gameID;
    public JoinObserverCommand(int gameID, String authToken) {
        super(CommandType.JOIN_OBSERVER, authToken);
        this.gameID = gameID;
    }

    public int getGameID(){
        return gameID;
    }
}
