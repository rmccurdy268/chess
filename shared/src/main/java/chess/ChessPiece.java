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
    private final ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
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
        return color;
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
                return kingMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            case BISHOP:
                int colBuffer = col;
                int rowBuffer = row;
                for (int i = row+1; i < 9; i++) {

                    colBuffer++;
                    rowBuffer++;

                    ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
                    if (isOccupied(board,endPosition)){
                        if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())){
                            ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                            setOfMoves.add(newMove);
                            break;
                        }
                        else{
                            break;
                        }
                    }
                    ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                    setOfMoves.add(newMove);
                    if ((colBuffer == 8) || (rowBuffer == 8)) {
                        break;
                    }

                }
                colBuffer = col;
                rowBuffer = row;
                for (int i = row-1; i > 0; i--) {
                    colBuffer++;
                    rowBuffer--;
                    ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
                    if (isOccupied(board,endPosition)){
                        if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())){
                            ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                            setOfMoves.add(newMove);
                            break;
                        }
                        else{
                            break;
                        }
                    }
                    ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                    setOfMoves.add(newMove);
                    if (colBuffer == 8 || rowBuffer == 1) {
                        break;
                    }
                }
                colBuffer = col;
                rowBuffer = row;
                for (int i = row+1; i < 9; i++) {

                    colBuffer--;
                    rowBuffer++;
                    ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
                    if (isOccupied(board,endPosition)){
                        if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())){
                            ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                            setOfMoves.add(newMove);
                            break;
                        }
                        else{
                            break;
                        }
                    }
                    ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                    setOfMoves.add(newMove);
                    if (colBuffer == 1 || rowBuffer == 8) {
                        break;
                    }
                }
                colBuffer = col;
                rowBuffer = row;
                for (int i = row-1; i > 0; i--) {
                    colBuffer--;
                    rowBuffer--;
                    ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
                    if (isOccupied(board,endPosition)){
                        if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())){
                            ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                            setOfMoves.add(newMove);
                            break;
                        }
                        else{
                            break;
                        }
                    }
                    ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                    setOfMoves.add(newMove);
                    if (colBuffer == 1 || rowBuffer == 1) {
                        break;
                    }


                }

                break;
            case KNIGHT:
                return knightMoves(board, myPosition);

            case ROOK:
                return rookMoves(board, myPosition);
            case PAWN:
                break;
        }

        return setOfMoves;
    }
    public static boolean isOccupied(ChessBoard board, ChessPosition position){
        return board.getPiece(position) != null;
    }

    public Collection <ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        Set<ChessMove> setOfMoves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int colBuffer = col + 1;
        int rowBuffer = row;

        if (isValid(rowBuffer, colBuffer)){
            ChessPosition tempPosition = new ChessPosition(rowBuffer, colBuffer);
            if (isOccupied(board, tempPosition)){
                if ((board.getPiece(tempPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                    ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                    setOfMoves.add(newMove);
                }
            }
            else{
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                setOfMoves.add(newMove);
            }
        }
        colBuffer = col + 1;
        rowBuffer = row + 1;

        if (isValid(rowBuffer, colBuffer)){
            ChessPosition tempPosition = new ChessPosition(rowBuffer, colBuffer);
            if (isOccupied(board, tempPosition)){
                if ((board.getPiece(tempPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                    ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                    setOfMoves.add(newMove);
                }
            }
            else{
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                setOfMoves.add(newMove);
            }
        }

        colBuffer = col;
        rowBuffer = row + 1;

        if (isValid(rowBuffer, colBuffer)){
            ChessPosition tempPosition = new ChessPosition(rowBuffer, colBuffer);
            if (isOccupied(board, tempPosition)){
                if ((board.getPiece(tempPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                    ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                    setOfMoves.add(newMove);
                }
            }
            else{
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                setOfMoves.add(newMove);
            }
        }

        colBuffer = col - 1;
        rowBuffer = row + 1;

        if (isValid(rowBuffer, colBuffer)){
            ChessPosition tempPosition = new ChessPosition(rowBuffer, colBuffer);
            if (isOccupied(board, tempPosition)){
                if ((board.getPiece(tempPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                    ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                    setOfMoves.add(newMove);
                }
            }
            else{
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                setOfMoves.add(newMove);
            }
        }

        colBuffer = col - 1;
        rowBuffer = row;

        if (isValid(rowBuffer, colBuffer)){
            ChessPosition tempPosition = new ChessPosition(rowBuffer, colBuffer);
            if (isOccupied(board, tempPosition)){
                if ((board.getPiece(tempPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                    ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                    setOfMoves.add(newMove);
                }
            }
            else{
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                setOfMoves.add(newMove);
            }
        }
        colBuffer = col - 1;
        rowBuffer = row - 1;

        if (isValid(rowBuffer, colBuffer)){
            ChessPosition tempPosition = new ChessPosition(rowBuffer, colBuffer);
            if (isOccupied(board, tempPosition)){
                if ((board.getPiece(tempPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                    ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                    setOfMoves.add(newMove);
                }
            }
            else{
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                setOfMoves.add(newMove);
            }
        }

        colBuffer = col;
        rowBuffer = row - 1;

        if (isValid(rowBuffer, colBuffer)){
            ChessPosition tempPosition = new ChessPosition(rowBuffer, colBuffer);
            if (isOccupied(board, tempPosition)){
                if ((board.getPiece(tempPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                    ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                    setOfMoves.add(newMove);
                }
            }
            else{
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                setOfMoves.add(newMove);
            }
        }

        colBuffer = col + 1;
        rowBuffer = row - 1;

        if (isValid(rowBuffer, colBuffer)){
            ChessPosition tempPosition = new ChessPosition(rowBuffer, colBuffer);
            if (isOccupied(board, tempPosition)){
                if ((board.getPiece(tempPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                    ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                    setOfMoves.add(newMove);
                }
            }
            else{
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                setOfMoves.add(newMove);
            }
        }

        return setOfMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        Set <ChessMove> setOfMoves = new HashSet<>();
        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        if (isValid(row + 2, col + 1)){
            ChessPosition testPosition = new ChessPosition(row + 2, col  + 1);
            if (moveCheck(board, myPosition, testPosition)){
                ChessMove newMove = new ChessMove(myPosition, testPosition, null);
                setOfMoves.add(newMove);
            }
        }
        if (isValid(row + 1, col + 2)){
            ChessPosition testPosition = new ChessPosition(row + 1, col  + 2);
            if (moveCheck(board, myPosition, testPosition)){
                ChessMove newMove = new ChessMove(myPosition, testPosition, null);
                setOfMoves.add(newMove);
            }
        }

        if (isValid(row - 2, col + 1)){
            ChessPosition testPosition = new ChessPosition(row - 2, col  + 1);
            if (moveCheck(board, myPosition, testPosition)){
                ChessMove newMove = new ChessMove(myPosition, testPosition, null);
                setOfMoves.add(newMove);
            }
        }

        if (isValid(row + 2, col - 1)){
            ChessPosition testPosition = new ChessPosition(row + 2, col  - 1);
            if (moveCheck(board, myPosition, testPosition)){
                ChessMove newMove = new ChessMove(myPosition, testPosition, null);
                setOfMoves.add(newMove);
            }
        }

        if (isValid(row - 2, col - 1)){
            ChessPosition testPosition = new ChessPosition(row - 2, col  - 1);
            if (moveCheck(board, myPosition, testPosition)){
                ChessMove newMove = new ChessMove(myPosition, testPosition, null);
                setOfMoves.add(newMove);
            }
        }
        if (isValid(row - 1, col + 2)){
            ChessPosition testPosition = new ChessPosition(row - 1, col  + 2);
            if (moveCheck(board, myPosition, testPosition)){
                ChessMove newMove = new ChessMove(myPosition, testPosition, null);
                setOfMoves.add(newMove);
            }
        }

        if (isValid(row + 1, col - 2)){
            ChessPosition testPosition = new ChessPosition(row + 1, col  - 2);
            if (moveCheck(board, myPosition, testPosition)){
                ChessMove newMove = new ChessMove(myPosition, testPosition, null);
                setOfMoves.add(newMove);
            }
        }

        if (isValid(row - 1, col - 2)){
            ChessPosition testPosition = new ChessPosition(row - 1, col  - 2);
            if (moveCheck(board, myPosition, testPosition)){
                ChessMove newMove = new ChessMove(myPosition, testPosition, null);
                setOfMoves.add(newMove);
            }
        }
        return setOfMoves;
    }

    //QUEEN HERE
    public Collection <ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int colBuffer = col;
        int rowBuffer = row;
        Set<ChessMove> setOfMoves = new HashSet<>();
        boolean valid = true;
        while (valid){
            rowBuffer++;
            colBuffer++;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)){
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            rowBuffer--;
            colBuffer--;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)){
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            rowBuffer++;
            colBuffer--;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)){
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            rowBuffer--;
            colBuffer++;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)){
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            rowBuffer++;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)){
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            rowBuffer--;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)){
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            colBuffer++;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)){
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            colBuffer--;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)) {
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }
        return setOfMoves;
    }

    Collection <ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int colBuffer = col;
        int rowBuffer = row;
        Set<ChessMove> setOfMoves = new HashSet<>();
        boolean valid = true;

        while (valid){
            rowBuffer++;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)) {
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            rowBuffer--;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)) {
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            colBuffer++;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)) {
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }

        colBuffer = col;
        rowBuffer = row;
        while (valid){
            colBuffer--;
            if (!isValid(rowBuffer, colBuffer)){
                break;
            }
            ChessPosition endPosition = new ChessPosition(rowBuffer, colBuffer);
            if (moveCheck(board, myPosition, endPosition)) {
                ChessMove newMove = new ChessMove(myPosition, endPosition, null);
                setOfMoves.add(newMove);
                if(isOccupied(board, endPosition)) {
                    if ((board.getPiece(endPosition).getTeamColor()) != (board.getPiece(myPosition).getTeamColor())) {
                        break;
                    }
                }
            }
            else{
                break;
            }

        }
        return setOfMoves;
    }




    public static boolean moveCheck(ChessBoard board, ChessPosition ogPosition, ChessPosition endPosition){
        if (isOccupied(board, endPosition)){
            if (board.getPiece(endPosition).getTeamColor() != board.getPiece(ogPosition).getTeamColor()){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return true;
        }
    }

    public static boolean isValid(int row, int col) {
        if (((row <= 0)||(row >= 9))||((col <= 0)||(col >= 9))){
            return false;
        }
        else{
            return true;
        }
    }
}
