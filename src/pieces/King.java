package pieces;

import chess.ChessColor;
import chess.GameException;
import chessBoardGame.ChessBoard;
import chessBoardGame.ChessPiece;
import chessBoardGame.ChessPosition;

public class King extends ChessPiece {

    public King(ChessBoard board, ChessColor color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() throws GameException {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        ChessPosition p = new ChessPosition( 0, 0);

        // the king can move one tile up, down, left, right or diagonally  as long as the piece occupying the "destination" tile is not occupied by a piece from the same color

        // king moving up
        p.setValues(position.getXCoordinate(), position.getYCoordinate() +1);
        if(getBoard().isBoardPosition(p) && (getBoard().piece(p) == null || getBoard().piece(p).getColor() != getColor() )){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // king moving down
        p.setValues(position.getXCoordinate(), position.getYCoordinate() -1);
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // king moving right
        p.setValues(position.getXCoordinate() +1, position.getYCoordinate());
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // king moving left
        p.setValues(position.getXCoordinate() -1, position.getYCoordinate());
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // king moving up-right
        p.setValues(position.getXCoordinate()+1, position.getYCoordinate() +1);
        if(getBoard().isBoardPosition(p) && (getBoard().piece(p) == null || getBoard().piece(p).getColor() != getColor() )){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // king moving up-left
        p.setValues(position.getXCoordinate()-1, position.getYCoordinate() +1);
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // king moving down-right
        p.setValues(position.getXCoordinate() +1, position.getYCoordinate()-1);
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // king moving down-left
        p.setValues(position.getXCoordinate() -1, position.getYCoordinate()-1);
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        return moves;
    }

    public String toString(){
        return "K";
    }
}
