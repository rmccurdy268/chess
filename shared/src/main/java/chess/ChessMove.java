package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private ChessPosition endPosition;
    private ChessPosition startPosition;
    private ChessPiece.PieceType promoPiece;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.endPosition = endPosition;
        this.startPosition = startPosition;
        this.promoPiece = promotionPiece;

    }

    @Override
    public String toString() {
        String promoPiece;
        switch (getPromotionPiece()){
            case QUEEN:
                promoPiece =  "Q";
            case ROOK:
                promoPiece = "R";
            case BISHOP:
                promoPiece = "B";
            case KNIGHT:
                promoPiece = "N";
            default:
                promoPiece = "null";
        }

        return startPosition.toString() + "->" + endPosition.toString() + " (" + promoPiece + ").";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(endPosition, chessMove.endPosition) && Objects.equals(startPosition, chessMove.startPosition) && promoPiece == chessMove.promoPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(endPosition, startPosition, promoPiece);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return endPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition(){
        return startPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promoPiece;
    }
}
