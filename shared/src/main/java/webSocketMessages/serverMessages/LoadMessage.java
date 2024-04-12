package webSocketMessages.serverMessages;

import chess.ChessBoard;
import com.google.gson.Gson;

public class LoadMessage extends ServerMessage{
    private ChessBoard myBoard;

    public LoadMessage(ServerMessageType type) {
        super(type);
        myBoard =
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
