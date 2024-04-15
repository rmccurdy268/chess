package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    private final int gameID;
    private final String color;
    public LeaveCommand(int gameID, String color, String authToken) {
        super(CommandType.LEAVE,authToken);
        this.gameID = gameID;
        this.color = color;
    }
    public int getId(){
        return gameID;
    }
    public String getColor(){
        return color;
    }
}
