package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return Objects.equals(recurseSet, that.recurseSet) && color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recurseSet, color, type);
    }

    private HashSet<ChessMove> recurseSet;
    private ChessGame.TeamColor color;
    private PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        recurseSet = new HashSet<ChessMove>();
        this.color = pieceColor;
        this.type = type;
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
        HashSet<ChessMove> setOfMoves = new HashSet<ChessMove>();
        switch(board.getPiece(myPosition).getPieceType()){
            case KING:
                return kingOrKnight(myPosition, board);
            case QUEEN:
                return queenMoves(myPosition,board);
            case BISHOP:
                return bishopMoves(myPosition,board);
            case KNIGHT:
                return kingOrKnight(myPosition,board);
            case ROOK:
                return rookMoves(myPosition,board);
            case PAWN:
                return pawnManager(myPosition,board,board.getPiece(myPosition).getTeamColor());
        }
        return setOfMoves;
    }
    //recursive piece functions
    public Collection<ChessMove> bishopMoves(ChessPosition myPosition, ChessBoard board){
        recurse(1,1,myPosition,myPosition,board);
        recurse(1,-1,myPosition,myPosition,board);
        recurse(-1,1,myPosition,myPosition,board);
        recurse(-1,-1,myPosition,myPosition,board);
        HashSet<ChessMove> setOfMoves = new HashSet<ChessMove>(recurseSet);
        clearSet();
        return setOfMoves;
    }

    public Collection<ChessMove> rookMoves(ChessPosition myPosition, ChessBoard board){
        recurse(1,0,myPosition,myPosition,board);
        recurse(0,-1,myPosition,myPosition,board);
        recurse(-1,0,myPosition,myPosition,board);
        recurse(0,1,myPosition,myPosition,board);
        HashSet<ChessMove> setOfMoves = new HashSet<ChessMove>(recurseSet);
        clearSet();
        return setOfMoves;
    }

    public Collection<ChessMove> queenMoves(ChessPosition myPosition, ChessBoard board){
        recurse(1,1,myPosition,myPosition,board);
        recurse(1,-1,myPosition,myPosition,board);
        recurse(-1,1,myPosition,myPosition,board);
        recurse(-1,-1,myPosition,myPosition,board);
        recurse(1,0,myPosition,myPosition,board);
        recurse(0,-1,myPosition,myPosition,board);
        recurse(-1,0,myPosition,myPosition,board);
        recurse(0,1,myPosition,myPosition,board);
        HashSet<ChessMove> setOfMoves = new HashSet<ChessMove>(recurseSet);
        clearSet();
        return setOfMoves;
    }

    //non recursive piece functions
    public Collection<ChessMove> kingOrKnight(ChessPosition myPosition, ChessBoard board){
        ArrayList<Tuple> kingList= new ArrayList<Tuple>();

        kingList.add(new Tuple(1,1));
        kingList.add(new Tuple(1,0));
        kingList.add(new Tuple(1,-1));
        kingList.add(new Tuple(0,1));
        kingList.add(new Tuple(-1,1));
        kingList.add(new Tuple(-1,-1));
        kingList.add(new Tuple(-1,0));
        kingList.add(new Tuple(0,-1));

        ArrayList<Tuple> knightList= new ArrayList<Tuple>();

        knightList.add(new Tuple(2,1));
        knightList.add(new Tuple(1,2));
        knightList.add(new Tuple(2,-1));
        knightList.add(new Tuple(-2,1));
        knightList.add(new Tuple(-1,2));
        knightList.add(new Tuple(1,-2));
        knightList.add(new Tuple(-1,-2));
        knightList.add(new Tuple(-2,-1));

        PieceType type = board.getPiece(myPosition).getPieceType();
        switch(type){
            case KING:
                return tupleListMoves(myPosition,board,kingList);
            case KNIGHT:
                return tupleListMoves(myPosition,board,knightList);
        }
        return new HashSet<ChessMove>();
    }

    public Collection<ChessMove> tupleListMoves(ChessPosition myPosition, ChessBoard board, ArrayList<Tuple> tupleList){
        HashSet<ChessMove> setOfMoves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = 0; i < 8; i++){
            int first = tupleList.get(i).getFirst();
            int second = tupleList.get(i).getSecond();
            ChessPosition newPosition = new ChessPosition(row+first, col+second);
            if (isValid(newPosition)){
                if (isCapturable(myPosition, newPosition, board)){
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    setOfMoves.add(newMove);
                }
            }
        }
        return setOfMoves;
    }

    //PAWN SECTION


    public Collection <ChessMove> pawnManager(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor color){
        HashSet<ChessPosition> listOfPositions = new HashSet<ChessPosition>();
        HashSet<ChessMove> setOfMoves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean hasntMoved;
        if (((myPosition.getRow() == 2)&&(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE)) || ((myPosition.getRow() == 7)&&(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK))) {
            hasntMoved = true;
        } else {
            hasntMoved = false;
        }

        if (color == ChessGame.TeamColor.WHITE){
            if (filterNormalMove(new ChessPosition(row + 1, col), board) != null) {
                setOfMoves.addAll((getPawnMoves(myPosition, new ChessPosition(row + 1, col), color)));
                if (hasntMoved) {
                    if (filterNormalMove(new ChessPosition(row + 2, col), board) != null) {
                        ChessMove newMove = new ChessMove(myPosition, new ChessPosition(row + 2, col), null);
                        setOfMoves.add(newMove);
                    }
                }
            }
            listOfPositions.add(new ChessPosition(row+1, col + 1));
            listOfPositions.add(new ChessPosition(row+1, col - 1));

            for (ChessPosition each : listOfPositions) {
                if (filterCaptureMove(each, color, board) != null) {
                    setOfMoves.addAll((getPawnMoves(myPosition, each, color)));
                }
            }
        }
        if (color == ChessGame.TeamColor.BLACK){
            if(filterNormalMove(new ChessPosition(row-1,col), board)!=null){
                setOfMoves.addAll((getPawnMoves(myPosition, new ChessPosition(row - 1, col), color)));
                if(hasntMoved){
                    if(filterNormalMove(new ChessPosition(row-2,col), board)!=null){
                        ChessMove newMove = new ChessMove(myPosition, new ChessPosition(row-2,col), null);
                        setOfMoves.add(newMove);
                    }
                }
            }
            listOfPositions.add(new ChessPosition(row-1,col-1));
            listOfPositions.add(new ChessPosition(row-1,col+1));

            for(ChessPosition each:listOfPositions){
                if(filterCaptureMove(each,color,board) != null) {
                    setOfMoves.addAll((getPawnMoves(myPosition, each, color)));
                }
            }
        }
        return setOfMoves;
    }
    public HashSet<ChessMove> getPawnMoves(ChessPosition myPosition, ChessPosition newPosition, ChessGame.TeamColor color){
        int promoRow = 1;
        switch(color){
            case WHITE -> promoRow = 8;
            case BLACK -> promoRow = 1;
        }
        HashSet<ChessMove> setOfMoves = new HashSet<ChessMove>();
        if(newPosition.getRow() == promoRow){
            ChessMove oneMove = new ChessMove(myPosition,newPosition,PieceType.QUEEN);
            setOfMoves.add(oneMove);
            ChessMove twoMove = new ChessMove(myPosition,newPosition,PieceType.BISHOP);
            setOfMoves.add(twoMove);
            ChessMove redMove = new ChessMove(myPosition,newPosition,PieceType.ROOK);
            setOfMoves.add(redMove);
            ChessMove blueMove = new ChessMove(myPosition,newPosition,PieceType.KNIGHT);
            setOfMoves.add(blueMove);
        }
        else{
            ChessMove newMove = new ChessMove(myPosition,newPosition,null);
            setOfMoves.add(newMove);
        }
        return setOfMoves;
    }

    public ChessPosition filterNormalMove(ChessPosition newPosition, ChessBoard board){
        if (isValid(newPosition)){
            if (board.getPiece(newPosition) == null){
                return newPosition;
            }
        }
        return null;
    }
    public ChessPosition filterCaptureMove(ChessPosition newPosition, ChessGame.TeamColor color, ChessBoard board){
        if(isValid(newPosition)){
            if(board.getPiece(newPosition) != null) {
                if(color != board.getPiece(newPosition).getTeamColor()){
                    return newPosition;
                }
            }
        }
        return null;
    }

    //HELPER FUNCTIONS
    public void recurse(int rowInc, int colInc, ChessPosition myPosition, ChessPosition ogPosition, ChessBoard board){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(row+rowInc, col+colInc);
        if (isValid(newPosition)){
            if (isCapturable(ogPosition,newPosition,board)){
                if (board.getPiece(newPosition)!= null){
                    if (board.getPiece(newPosition).getTeamColor() != board.getPiece(ogPosition).getTeamColor()){
                        ChessMove newMove = new ChessMove(ogPosition, newPosition, null);
                        recurseSet.add(newMove);
                        return;
                    }
                }
                else{
                    ChessMove newMove = new ChessMove(ogPosition, newPosition, null);
                    recurseSet.add(newMove);
                    recurse(rowInc, colInc, newPosition, ogPosition, board);
                }
            }
            else{
                return;
            }
        }
    }

    public void clearSet(){
        recurseSet.clear();
    }
    public boolean isValid(ChessPosition myPosition){
        if (((myPosition.getRow() >= 9)||(myPosition.getRow() <= 0))||((myPosition.getColumn() >= 9)||(myPosition.getColumn() <= 0))){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean isCapturable(ChessPosition myPosition, ChessPosition nextPosition, ChessBoard board){
        if (board.getPiece(nextPosition) != null){
            if (board.getPiece(nextPosition).getTeamColor() == (board.getPiece(myPosition).getTeamColor())){
                return false;
            }
        }
        return true;
    }

    //TUPLE CLASS
    public class Tuple{
        private int first, second;
        public Tuple(int first, int second){
            this.first= first;
            this.second = second;
        }
        public int getFirst(){
            return first;
        }
        public int getSecond(){
            return second;
        }
    }
}
