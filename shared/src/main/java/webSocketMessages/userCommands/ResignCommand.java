package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    private final int gameID;
    private final String color;
    public ResignCommand(int gameID, String color,int auth) {
        super(CommandType.RESIGN, String.valueOf(auth));
        this.gameID = gameID;
        this.color = color;
    }

    public int getGameId(){
        return gameID;
    }
    public String getColor(){
        return color;
    }
}
