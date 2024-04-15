package webSocketMessages.serverMessages;

import chess.ChessBoard;

public class LoadMessage extends ServerMessage {
    private final ChessBoard game;

    public LoadMessage(ServerMessageType type, ChessBoard board) {
        super(type);
        game = board;
    }

    public ChessBoard getBoard(){
        return game;
    }
}

