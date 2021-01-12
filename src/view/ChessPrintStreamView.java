package view;

import chess.ChessColor;
import chess.ChessImpl;
import chess.GameException;
import chessBoardGame.ChessBoard;
import chessBoardGame.ChessPiece;
import chessBoardGame.ChessPosition;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChessPrintStreamView implements PrintStreamView {
    private final ChessBoard board;

    public ChessPrintStreamView(ChessBoard board) {
        this.board = board;
    }

    @Override
    public void print(PrintStream ps) throws IOException {

    }

  public static void printChess(ChessImpl gameEngine, List<ChessPiece> capturedPieces) throws GameException {
        printBoard(gameEngine.getPieces());
        System.out.println();
        capturedPieces(capturedPieces);

    }

    public static void printBoard(ChessPiece[][] pieces) {
        for (int i = 0; i < pieces.length; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < pieces.length; j++) {
                printPiece(pieces[i][j], false);
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
    }

    public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves) {
        for (int i = 0; i < pieces.length; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < pieces.length; j++) {
                printPiece(pieces[i][j], possibleMoves[i][j]);
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
    }

    private static void printPiece(ChessPiece piece, boolean background) {
        if (background) {
            System.out.print(BoardColors.ANSI_BLUE_BACKGROUND);
        }
        if (piece == null) {
            System.out.print("-" + BoardColors.RESET);
        }
        else {
            if (piece.getColor() == ChessColor.white) {
                System.out.print(BoardColors.WHITE + piece + BoardColors.RESET);
            }
            else {
                System.out.print(BoardColors.YELLOW + piece + BoardColors.RESET);
            }
        }
        System.out.print(" ");
    }

    private static void capturedPieces(List<ChessPiece> chessPieces) {
        List<ChessPiece> white = new ArrayList<>();
        List<ChessPiece> black = new ArrayList<>();
        for(int i = 0; i < chessPieces.size(); i++){
            if(chessPieces.get(i).getColor() == ChessColor.white){
                white.add(chessPieces.get(i));
            }else{
                black.add(chessPieces.get(i));
            }
        }
       /* System.out.println("Captured pieces: ");
        System.out.print("White pieces: ");
        System.out.print(BoardColors.WHITE);
        System.out.println(Arrays.toString(white.toArray()));
        System.out.print(BoardColors.RESET);
        System.out.print("Black pieces: ");
        System.out.print(BoardColors.YELLOW);
        System.out.println(Arrays.toString(black.toArray()));

        */

    }

    }

