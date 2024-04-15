package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    private final int gameId;
    public ResignCommand(int gameId, int auth) {
        super(CommandType.RESIGN, String.valueOf(auth));
        this.gameId = gameId;
    }

    public int getGameId(){
        return gameId;
    }
}
