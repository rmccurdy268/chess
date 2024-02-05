package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn;
    private Map<ChessPosition, ChessPiece> whitePieces;
    private Map<ChessPosition, ChessPiece> blackPieces;
    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        whitePieces = new HashMap<ChessPosition, ChessPiece>();
        blackPieces = new HashMap<ChessPosition, ChessPiece>();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var initialPosition = move.getStartPosition();
        Collection<ChessMove> possibleMoves = board.getPiece(initialPosition).pieceMoves(board, initialPosition);
        boolean isValid = false;
        for (ChessMove newMove : possibleMoves) {
            if (newMove.equals(move)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new InvalidMoveException();
        } else if (board.getPiece(initialPosition).getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }
        else{
            var movePiece = board.getPiece(initialPosition);
            if (move.getPromotionPiece() == null){
                if(board.getPiece(move.getEndPosition()) != null){
                    board.nullifyPiece(move.getEndPosition());
                }
                board.addPiece(move.getEndPosition(), movePiece);
                if(movePiece.getPieceType() == ChessPiece.PieceType.KING){
                    board.nullifyPiece(initialPosition);
                }
                if(isInCheck(teamTurn)){
                    if(movePiece.getPieceType() == ChessPiece.PieceType.KING){
                        board.addPiece(initialPosition,movePiece);
                    }
                    board.nullifyPiece(move.getEndPosition());
                    throw new InvalidMoveException("This move leaves you in check!");
                }
                board.nullifyPiece(initialPosition);
            }
            else{
                var promoPiece = new ChessPiece(movePiece.getTeamColor(),move.getPromotionPiece());
                if(board.getPiece(move.getEndPosition()) != null){
                    board.nullifyPiece(move.getEndPosition());
                }
                board.addPiece(move.getEndPosition(), promoPiece);
                if(isInCheck(teamTurn)){
                    board.nullifyPiece(move.getEndPosition());
                    throw new InvalidMoveException("You need to move out of check!");
                }
                board.nullifyPiece(initialPosition);
            }
        }
        switch(getTeamTurn()){
            case WHITE -> teamTurn = TeamColor.BLACK;
            case BLACK -> teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        Collection<ChessMove> moveSet = new HashSet<ChessMove>();
        ChessPosition kingPosition = null;
        for (int k = 1; k < 9; k+=1) {
            if (kingPosition!= null){
                break;
            }
            for (int l = 1; l < 9; l += 1) {
                if(board.getPiece(new ChessPosition(k,l))!=null) {
                    if (teamColor == board.getPiece(new ChessPosition(k, l)).getTeamColor()) {
                        if (board.getPiece(new ChessPosition(k, l)).getPieceType() == ChessPiece.PieceType.KING) {
                            kingPosition = new ChessPosition(k, l);
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 1; i < 9; i+=1){
            for (int j = 1; j < 9; j+=1){
                if(board.getPiece(new ChessPosition(i,j))!=null) {
                    if (teamColor != board.getPiece(new ChessPosition(i,j)).getTeamColor()){
                        moveSet = board.getPiece(new ChessPosition(i,j)).pieceMoves(board, new ChessPosition(i,j));
                        for (ChessMove move:moveSet) {
                            if (move.getEndPosition().equals(kingPosition)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> moveSet = new HashSet<ChessMove>();
        if (isInCheck(teamColor)) {
            for (int i = 1; i<9; i++) {
                for (int j = 1; j < 9; j++) {
                    if (board.getPiece(new ChessPosition(i, j)) != null) {
                        TeamColor thisTeamColor = board.getPiece(new ChessPosition(i, j)).getTeamColor();
                        if (thisTeamColor == TeamColor.WHITE) {
                            whitePieces.put(new ChessPosition(i,j), board.getPiece(new ChessPosition(i,j)));
                        } else if (thisTeamColor == TeamColor.BLACK) {
                            blackPieces.put(new ChessPosition(i,j), board.getPiece(new ChessPosition(i,j)));
                        }
                    }
                }
            }
        }


            switch(teamColor){
                case WHITE:
                    for (ChessPosition each:whitePieces.keySet()){
                        moveSet = whitePieces.get(each).pieceMoves(board,each);
                        for (ChessMove every:moveSet){
                            try {
                                makeMove(every);
                                if(!isInCheck(teamColor)){
                                    return false;
                                }
                            } catch (InvalidMoveException e) {}
                        }
                    }
                case BLACK:
                    for (ChessPosition each:blackPieces.keySet()){
                        moveSet = blackPieces.get(each).pieceMoves(board,each);
                        for (ChessMove every:moveSet){
                            boolean replaced = false;
                            ChessPiece pieceToReplace = board.getPiece(every.getEndPosition());
                            try {
                                if(board.getPiece(every.getEndPosition()) != null){
                                    replaced = true;
                                }
                                makeMove(every);
                                if(!isInCheck(teamColor)){
                                    return false;
                                }
                            } catch (InvalidMoveException e) {
                                if (replaced){
                                    board.addPiece(every.getEndPosition(), pieceToReplace);
                                }
                            }
                        }
                    }
            }

        whitePieces.clear();
        blackPieces.clear();
        return isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
