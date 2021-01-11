package pieces;

import chess.*;
import chessBoardGame.ChessBoard;
import chessBoardGame.ChessPiece;
import chessBoardGame.ChessPosition;

public class Pawn extends ChessPiece {

    private ChessImpl gameEngine;

    public Pawn(ChessBoard board, ChessColor color)
    {
        super(board, color);

    }

    @Override
    public boolean[][] possibleMoves() throws GameException {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        ChessPosition p = new ChessPosition( 0, 0);

        // black and white must be differentiated because pawn can only move forward
        if (getColor() == ChessColor.white) {
            // pawn can move one tile ahead if the position is not occupied
            p.setValues(position.getXCoordinate()-1, position.getYCoordinate());
            if (getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)) {
                moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            }
            // pawn can move two tiles ahead if non of these two tiles are occupied
            /*p.setValues(position.getXCoordinate()-2, position.getYCoordinate());
            ChessPosition p2 = new ChessPosition(position.getXCoordinate()-1, position.getYCoordinate() );
            if (getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p) && getBoard().isBoardPosition(p2) && !getBoard().positionIsOccupied(p2) && getMoveCount() ==0) {
                moves[p.getYCoordinate()][p.getXCoordinate()] = true;
            }

             */
            // pawn can move one tile ahead and one to the side if tile is occupied (taking the opponents piece)
            p.setValues(position.getXCoordinate() -1, position.getYCoordinate() - 1);
            if (getBoard().isBoardPosition(p) && isThereOpponentPiece(p)) {
                moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            }
            // pawn can move one tile ahead and one to the side if tile is occupied (taking the opponents piece)
            p.setValues(position.getYCoordinate() - 1, position.getXCoordinate() + 1);
            if (getBoard().isBoardPosition(p) && isThereOpponentPiece(p)) {
                moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            }

        }else {
            // pawn can move one tile ahead if the position is not occupied
            p.setValues(position.getXCoordinate() + 1, position.getYCoordinate());
            if (getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)) {
                moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            }
            // pawn can move two tiles ahead if non of these two tiles are occupied
          /*  p.setValues(position.getXCoordinate()+2, position.getYCoordinate());
            ChessPosition p2 = new ChessPosition(position.getXCoordinate(), position.getYCoordinate() -1);
            if (getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p) && getBoard().isBoardPosition(p2) && !getBoard().positionIsOccupied(p2) && getMoveCount() ==0) {
                moves[p.getYCoordinate()][p.getXCoordinate()] = true;
            }

           */
            // pawn can move one tile ahead and one to the side if tile is occupied (taking the opponents piece)
            p.setValues(position.getXCoordinate() + 1, position.getYCoordinate() + 1);
            if (getBoard().isBoardPosition(p) && isThereOpponentPiece(p)) {
                moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            }
            // pawn can move one tile ahead and one to the side if tile is occupied (taking the opponents piece)
            p.setValues(position.getYCoordinate() + 1, position.getXCoordinate() - 1);
            if (getBoard().isBoardPosition(p) && isThereOpponentPiece(p)) {
                moves[p.getYCoordinate()][p.getXCoordinate()] = true;
            }
        }
        return moves;
    }
    public String toString(){
        return "P";
    }
}
