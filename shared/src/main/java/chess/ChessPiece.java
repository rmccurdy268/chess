package chess;

import java.sql.Array;
import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        var piece = board.getPiece(myPosition);
        Set<ChessMove> setOfMoves = new HashSet<ChessMove>();
        switch (piece.getPieceType()){
            case KING:
                break;
            case QUEEN:
                break;
            case BISHOP:
                int colBuffer = col;
                int rowBuffer = row;
                int boardLength = 8;
                for (int i = row+1; i < 9; i++) {

                    colBuffer++;
                    rowBuffer++;
                    ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
                    ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                    setOfMoves.add(newMove);
                    if (colBuffer == 8) {
                        break;
                    }
                }
                colBuffer = col;
                rowBuffer = row;
                for (int i = row-1; i > 0; i--) {
                    colBuffer++;
                    rowBuffer--;
                    ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
                    ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                    setOfMoves.add(newMove);
                    if (colBuffer == 8) {
                        break;
                    }
                }
                colBuffer = col;
                rowBuffer = row;
                for (int i = row+1; i < 9; i++) {

                    colBuffer--;
                    rowBuffer++;
                    ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
                    ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                    setOfMoves.add(newMove);
                    if (colBuffer == 1) {
                        break;
                    }
                }
                colBuffer = col;
                rowBuffer = row;
                for (int i = row-1; i > 0; i--) {
                    colBuffer--;
                    rowBuffer--;
                    ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
                    ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                    setOfMoves.add(newMove);
                    if (colBuffer == 1) {
                        break;
                    }
                }

                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                break;
        }

        return setOfMoves;
    }
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        HashSet<ChessMove> movesSet = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        return movesSet;

    }
}
