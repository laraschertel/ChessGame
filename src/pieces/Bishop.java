package pieces;

import chess.ChessColor;
import chess.GameException;
import chessBoardGame.ChessBoard;
import chessBoardGame.ChessPiece;
import chessBoardGame.ChessPosition;

public class Bishop extends ChessPiece {

    public Bishop(ChessBoard board, ChessColor color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() throws GameException {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        ChessPosition p = new ChessPosition( 0, 0);

        //black and white does not have to be differentiated because bishops only move diagonally
        // the bishop can move diagonally until the board ends or another piece stands on the way

        // northwest
        p.setValues(position.getXCoordinate() - 1, position.getYCoordinate() - 1);
        while (getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)) {
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() - 1, p.getYCoordinate() - 1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // northeast
        p.setValues(position.getXCoordinate() - 1, position.getYCoordinate() + 1);
        while (getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)) {
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() - 1, p.getYCoordinate() + 1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // southeast
        p.setValues(position.getXCoordinate() + 1 , position.getYCoordinate() + 1);
        while (getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)) {
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() + 1, p.getYCoordinate() + 1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // down
        p.setValues(position.getXCoordinate() + 1, position.getYCoordinate() - 1 );
        while (getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)) {
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() + 1, p.getYCoordinate() - 1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        return moves;
    }
    public String toString(){
        return "B";
    }

}
