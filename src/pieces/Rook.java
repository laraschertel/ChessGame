package pieces;

import chess.ChessColor;
import chess.GameException;
import chessBoardGame.ChessBoard;
import chessBoardGame.ChessPiece;
import chessBoardGame.ChessPosition;

public class Rook extends ChessPiece {


    public Rook(ChessBoard board, ChessColor color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() throws GameException {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        ChessPosition p = new ChessPosition( 0, 0);

        //black and white does not have to be differentiated because rooks can move in any direction in a straight line
        // the rook can move in a line until the board ends or another piece stands on the way

        // rook moving up
        p.setValues(position.getXCoordinate(), position.getYCoordinate() +1);
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() , p.getYCoordinate()+1);
        }
        //rook can take the piece that is standing on the way and take its position
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // rook moving down
        p.setValues(position.getXCoordinate(), position.getYCoordinate() -1);
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate(), p.getYCoordinate()-1);
        }
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // rook moving right
        p.setValues(position.getXCoordinate() +1, position.getYCoordinate());
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() +1, p.getYCoordinate());
        }
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // rook moving left
        p.setValues(position.getXCoordinate() -1, position.getYCoordinate());
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() -1 , p.getYCoordinate());
        }
        if(getBoard().isBoardPosition(p) && isThereOpponentPiece(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }


        return moves;
    }
    public String toString(){
        return "R";
    }
}
