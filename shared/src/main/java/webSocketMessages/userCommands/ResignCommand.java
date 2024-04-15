package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    private final int gameID;
    public ResignCommand(int gameID, int auth) {
        super(CommandType.RESIGN, String.valueOf(auth));
        this.gameID = gameID;
    }

    public int getGameId(){
        return gameID;
    }
}
