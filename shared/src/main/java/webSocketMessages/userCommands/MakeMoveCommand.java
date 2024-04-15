package webSocketMessages.userCommands;

public class MakeMoveCommand extends UserGameCommand{
    String ogPos;
    String finalPos;
    String promoPiece;
    int gameId;
    public MakeMoveCommand(String ogPos, String finalPos, String promoPiece, int gameId, int auth) {
        super(CommandType.MAKE_MOVE,String.valueOf(auth));
        this.ogPos = ogPos;
        this.finalPos = finalPos;
        this.gameId = gameId;
        this.promoPiece = promoPiece;
    }
    public String getOgPos(){
        return ogPos;
    }

    public String getFinalPos(){
        return finalPos;
    }

    public int getGameId(){
        return gameId;
    }
    public String getPromoPiece(){
        return promoPiece;
    }
}
