package webSocketMessages.serverMessages;

import chess.ChessBoard;

public class LoadMessage extends ServerMessage{
    private ChessBoard myBoard;

    public LoadMessage(ServerMessageType type) {
        super(type);}

}
