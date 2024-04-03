package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoard {

    private static final int BOARD_LENGTH = 8;
    private static final int BOARD_WIDTH = 8;

    private static final int HEADER_WIDTH = 10;

    private static final String FORWARD_HEADER = "abcdefgh";

    private static final String BACKWARD_HEADER= "hgfedcba";

    private static final String TOP_DOWN_COL_HEADER = " 12345678";

    private static final String BOTTOM_UP_COL_HEADER = " 87654321";

    private static final char SINGLE_SPACE = ' ';


    public static enum direction{FORWARD, BACKWARD};


    public static void drawBoard(){
        ChessBoard myBoard = new ChessBoard();
        myBoard.resetBoard();
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        printForwardBoard(myBoard, out, direction.FORWARD);
        out.println();
        printBackwardBoard(myBoard, out, direction.BACKWARD);
    }

    /*public static void printBackwardBoard(ChessBoard board, PrintStream out){
        setHeader(out, direction.FORWARD);
        for(int i = 1; i <= BOARD_LENGTH; i++){
            setHeaderColors(out);
            out.print(SINGLE_SPACE);
            out.print(BOTTOM_UP_COL_HEADER.charAt(i));
            out.print(SINGLE_SPACE);
            for (int j = 1; j <= BOARD_WIDTH; j++){
                if(((j % 2 == 1)&&(i % 2 == 1))||((j % 2 == 0)&&(i % 2 == 0))){
                    setBlackSpace(out);
                }
                else{
                    setWhiteSpace(out);
                }
                ChessPiece currentPiece = board.getPiece(new ChessPosition(i,j));
                if(currentPiece == null){
                    out.print(EMPTY.repeat(1));
                }
                else{
                    ChessGame.TeamColor pieceColor = currentPiece.getTeamColor();
                    if (pieceColor == ChessGame.TeamColor.BLACK){
                        setBlackPiece(out);
                    }
                    else{
                        setWhitePiece(out);
                    }
                    switch(currentPiece.getPieceType()){
                        case KING -> {
                            if(pieceColor == ChessGame.TeamColor.WHITE) {
                                out.print(WHITE_KING);
                            }
                            else{
                                out.print(BLACK_KING);
                            }
                        }
                        case QUEEN -> {
                            if(pieceColor == ChessGame.TeamColor.WHITE) {
                                out.print(WHITE_QUEEN);
                            }
                            else{
                                out.print(BLACK_QUEEN);
                            }
                        }
                        case BISHOP -> {
                            if(pieceColor == ChessGame.TeamColor.WHITE) {
                                out.print(WHITE_BISHOP);
                            }
                            else{
                                out.print(BLACK_BISHOP);
                            }
                        }
                        case KNIGHT -> {
                            if(pieceColor == ChessGame.TeamColor.WHITE) {
                                out.print(WHITE_KNIGHT);
                            }
                            else{
                                out.print(BLACK_KNIGHT);
                            }
                        }
                        case ROOK -> {
                            if(pieceColor == ChessGame.TeamColor.WHITE) {
                                out.print(WHITE_ROOK);
                            }
                            else{
                                out.print(BLACK_ROOK);
                            }
                        }
                        case PAWN -> {
                            if(pieceColor == ChessGame.TeamColor.WHITE) {
                                out.print(WHITE_PAWN);
                            }
                            else{
                                out.print(BLACK_PAWN);
                            }
                        }
                    }
                }
            }
            setHeaderColors(out);
            out.print(SINGLE_SPACE);
            out.print(BOTTOM_UP_COL_HEADER.charAt(i));
            out.print(SINGLE_SPACE);
            out.println();
        }
        setHeader(out, direction.FORWARD);
    }
     */



    public static void printForwardBoard(ChessBoard board, PrintStream out, direction boardOrientation){
        setHeader(out, direction.BACKWARD);
        makeMainBoard(board, out, boardOrientation);
        setHeader(out, direction.BACKWARD);
    }

    public static void printBackwardBoard(ChessBoard board, PrintStream out, direction boardOrientation){
        setHeader(out, direction.FORWARD);
        makeMainBoard(board, out, boardOrientation);
        setHeader(out, direction.FORWARD);
    }

    private static void makeMainBoard(ChessBoard board, PrintStream out, direction boardOrientation) {
        String colHeader = "";
        if (boardOrientation == direction.FORWARD){
            colHeader = BOTTOM_UP_COL_HEADER;
            for (int i = BOARD_LENGTH; i >= 1; i--){
                setHeaderColors(out);
                out.print(SINGLE_SPACE);
                out.print(colHeader.charAt(i));
                out.print(SINGLE_SPACE);
                for (int j = BOARD_WIDTH; j>= 1; j--){
                    setBgColor(i,j,out);
                    ChessPiece currentPiece = board.getPiece(new ChessPosition(i,j));
                    if (currentPiece != null){
                        setPiece(currentPiece,out);
                    }
                    else{
                        out.print(EMPTY.repeat(1));
                    }
                }
                setHeaderColors(out);
                out.print(SINGLE_SPACE);
                out.print(colHeader.charAt(i));
                out.print(SINGLE_SPACE);
                out.println();
            }

        }
        else{
            colHeader = BOTTOM_UP_COL_HEADER;
            for (int i = 1; i <= BOARD_LENGTH; i++){
                setHeaderColors(out);
                out.print(SINGLE_SPACE);
                out.print(colHeader.charAt(i));
                out.print(SINGLE_SPACE);
                for (int j = 1; j<= BOARD_WIDTH; j++){
                    setBgColor(i,j,out);
                    ChessPiece currentPiece = board.getPiece(new ChessPosition(i,j));
                    if (currentPiece != null){
                        setPiece(currentPiece,out);
                    }
                    else{
                        out.print(EMPTY.repeat(1));
                    }
                }
                setHeaderColors(out);
                out.print(SINGLE_SPACE);
                out.print(colHeader.charAt(i));
                out.print(SINGLE_SPACE);
                out.println();
            }
        }

    }
    public static void setBgColor(int row, int col, PrintStream out){
        if (row % 2 == col % 2){
            setBlackSpace(out);
        }
        else{
            setWhiteSpace(out);
        }
    }

    public static void setPiece(ChessPiece piece, PrintStream out){
        var pieceColor = piece.getTeamColor();
        switch(pieceColor){
            case BLACK -> setBlackPiece(out);
            case WHITE -> setWhitePiece(out);
        }
        switch(piece.getPieceType()){
            case KING -> setKing(pieceColor,out);
            case QUEEN -> setQueen(pieceColor,out);
            case ROOK -> setRook(pieceColor,out);
            case BISHOP -> setBishop(pieceColor,out);
            case KNIGHT -> setKnight(pieceColor,out);
            case PAWN -> setPawn(pieceColor,out);
        }
    }

    public static void setKing(ChessGame.TeamColor color, PrintStream out){
        switch(color){
            case WHITE -> out.print(WHITE_KING);
            case BLACK -> out.print(BLACK_KING);
        }
    }

    public static void setQueen(ChessGame.TeamColor color, PrintStream out){
        switch(color){
            case WHITE -> out.print(WHITE_QUEEN);
            case BLACK -> out.print(BLACK_QUEEN);
        }
    }

    public static void setBishop(ChessGame.TeamColor color, PrintStream out){
        switch(color){
            case WHITE -> out.print(WHITE_BISHOP);
            case BLACK -> out.print(BLACK_BISHOP);
        }
    }

    public static void setKnight(ChessGame.TeamColor color, PrintStream out){
        switch(color){
            case WHITE -> out.print(WHITE_KNIGHT);
            case BLACK -> out.print(BLACK_KNIGHT);
        }
    }

    public static void setRook(ChessGame.TeamColor color, PrintStream out){
        switch(color){
            case WHITE -> out.print(WHITE_ROOK);
            case BLACK -> out.print(BLACK_ROOK);
        }
    }

    public static void setPawn(ChessGame.TeamColor color, PrintStream out){
        switch(color){
            case WHITE -> out.print(WHITE_PAWN);
            case BLACK -> out.print(BLACK_PAWN);
        }
    }

    public static void setHeader(PrintStream out, direction direction){
        setHeaderColors(out);
        for(int i = 0; i < HEADER_WIDTH; i++){

            if(i == 0 || i == 9){
                out.print(EMPTY.repeat(1));
            }
            else{
                out.print(SINGLE_SPACE);
                if (direction == DrawBoard.direction.FORWARD){
                    out.print(FORWARD_HEADER.charAt(i-1));
                }
                else{
                    out.print(BACKWARD_HEADER.charAt(i-1));
                }
                out.print(SINGLE_SPACE);
            }
        }
        out.println();
    }

    public static void setHeaderColors(PrintStream out){
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_GREEN);
    }
    public static void setWhitePiece(PrintStream out){
        out.print(SET_TEXT_COLOR_BLUE);
    }

    public static void setWhiteSpace(PrintStream out){
        out.print(SET_BG_COLOR_LIGHT_GREY);
    }

    public static void setBlackPiece(PrintStream out){
        out.print(SET_TEXT_COLOR_RED);
    }

    public static void setBlackSpace(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
    }
}
