package webSocketMessages.serverMessages;

import chess.ChessBoard;

public class LoadMessage extends ServerMessage {
    private final ChessBoard myBoard;

    public LoadMessage(ServerMessageType type, ChessBoard board) {
        super(type);
        myBoard = board;
    }

    public ChessBoard getBoard(){
        return myBoard;
    }
}

