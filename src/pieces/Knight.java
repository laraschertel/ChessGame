package pieces;

import chess.ChessColor;
import chess.GameException;
import chessBoardGame.ChessBoard;
import chessBoardGame.ChessPiece;
import chessBoardGame.ChessPosition;

public class Knight extends ChessPiece {

    public Knight(ChessBoard board, ChessColor color) {
        super(board, color);
    }


    @Override
    public boolean[][] possibleMoves() throws GameException {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        ChessPosition p = new ChessPosition( 0, 0);

        // the knight can move in "L"

        // knight moving 1 up 2 left
        p.setValues(position.getXCoordinate()-2, position.getYCoordinate() +1);
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // knight moving 1 up 2 right
        p.setValues(position.getXCoordinate() +2, position.getYCoordinate() +1);
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // knight moving 1 down 2 left
        p.setValues(position.getXCoordinate() +2, position.getYCoordinate()-1);
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // knight moving 1 down 2 right
        p.setValues(position.getXCoordinate() -2, position.getYCoordinate()-1);
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // knight moving 2 up 1 right
        p.setValues(position.getXCoordinate()+1, position.getYCoordinate() +2);
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // knight moving 2 up 1 left
        p.setValues(position.getXCoordinate()-1, position.getYCoordinate() +2);
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // knight moving 2 down 1 right
        p.setValues(position.getXCoordinate() +1, position.getYCoordinate() -2);
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // knight moving 2 down 1 left
        p.setValues(position.getXCoordinate() -1, position.getYCoordinate() -2);
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        return moves;
    }
    public String toString(){
        return "H";
    }
}
