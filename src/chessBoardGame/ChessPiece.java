package chessBoardGame;

import chess.*;

public abstract class ChessPiece {
    private final ChessColor color;
    protected ChessPosition position;
    private ChessBoard board;


    public ChessPiece(ChessBoard board, ChessColor color){
        this.board = board;
        this.color = color;
        position = null;
    }

    protected ChessBoard getBoard() {
        return board;
    }

    public abstract boolean[][] possibleMoves() throws GameException;

    public boolean possibleMove(ChessPosition position) throws GameException {
        return possibleMoves()[position.getXCoordinate()][position.getYCoordinate()];
    }

    public boolean isThereAnyPossibleMove() throws GameException {
        boolean[][] moves = possibleMoves();
        for (int i=0; i<moves.length; i++) {
            for (int j=0; j<moves.length; j++) {
                if (moves[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    public ChessColor getColor(){
        return color;
    }


    protected boolean isThereOpponentPiece(ChessPosition position) throws GameException {
        ChessPiece piece = (getBoard().piece(position));
        return piece != null && piece.getColor() != color;
    }


    public ChessBoardPosition getChessPosition() throws GameException {
        return ChessBoardPosition.fromPosition(position);
    }
}


