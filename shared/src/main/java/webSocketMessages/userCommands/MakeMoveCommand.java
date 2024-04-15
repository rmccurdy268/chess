package webSocketMessages.userCommands;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;

public class MakeMoveCommand extends UserGameCommand{
    String ogPos;
    String finalPos;
    String promoPiece;
    ChessMove move;
    int gameID;
    public MakeMoveCommand(String ogPos, String finalPos, String promoPiece, int gameID, int auth) {
        super(CommandType.MAKE_MOVE,String.valueOf(auth));
        this.ogPos = ogPos;
        this.finalPos = finalPos;
        this.gameID = gameID;
        this.promoPiece = promoPiece;
        this.move = createChessMove(ogPos,finalPos,promoPiece);
    }
    public String getOgPos(){
        return ogPos;
    }

    public String getFinalPos(){
        return finalPos;
    }

    public int getGameId(){
        return gameID;
    }
    public String getPromoPiece(){
        return promoPiece;
    }
    public ChessMove getMove(){return move;}
    public ChessMove createChessMove(String ogPos, String finalPos, String promoPiece){
        HashMap<Character, Integer> boardMap = new HashMap<>();
        boardMap.put('a', 1);
        boardMap.put('b', 2);
        boardMap.put('c', 3);
        boardMap.put('d', 4);
        boardMap.put('e', 5);
        boardMap.put('f', 6);
        boardMap.put('g', 7);
        boardMap.put('h', 8);
        int ogFirst = boardMap.get(ogPos.charAt(0));
        int ogSecond = Character.getNumericValue(ogPos.charAt(1));
        int finalFirst = boardMap.get(finalPos.charAt(0));
        int finalSecond = Character.getNumericValue(finalPos.charAt(1));
        ChessPosition firstPos = new ChessPosition(ogSecond, ogFirst);
        ChessPosition secondPos = new ChessPosition(finalSecond, finalFirst);
        ChessPiece.PieceType myType = null;
        switch(promoPiece){
            case "queen"-> myType = ChessPiece.PieceType.QUEEN;
            case "rook"-> myType = ChessPiece.PieceType.ROOK;
            case "knight"-> myType = ChessPiece.PieceType.KNIGHT;
            case "bishop"-> myType = ChessPiece.PieceType.BISHOP;
            case "king"-> myType = ChessPiece.PieceType.KING;
            case "pawn"-> myType = ChessPiece.PieceType.PAWN;
        }
        return new ChessMove(firstPos,secondPos,myType);
    }
}
