package pieces;

import chess.ChessColor;
import chess.GameException;
import chessBoardGame.ChessBoard;
import chessBoardGame.ChessPiece;
import chessBoardGame.ChessPosition;

public class Queen extends ChessPiece {


    public Queen(ChessBoard board, ChessColor color) {
        super(board, color);
    }


    @Override
    public boolean[][] possibleMoves() throws GameException {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        ChessPosition p = new ChessPosition( 0, 0);

        //black and white does not have to be differentiated because the queen can move in any direction (diagonally or straight line)
        // the queen has the same moves of the bishop and the rook combined


        // queen moving up
        p.setValues(position.getXCoordinate(), position.getYCoordinate() +1);
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() , p.getYCoordinate()+1);
        }
        //queen can take the piece that is standing on the way and take its position
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // queen moving down
        p.setValues(position.getXCoordinate(), position.getYCoordinate() -1);
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate(), p.getYCoordinate()-1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // queen moving right
        p.setValues(position.getXCoordinate() +1, position.getYCoordinate());
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() +1, p.getYCoordinate());
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // queen moving left
        p.setValues(position.getXCoordinate() -1, position.getYCoordinate());
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() -1 , p.getYCoordinate());
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // queen moving up-right
        p.setValues(position.getXCoordinate() +1, position.getYCoordinate() +1);
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() +1, p.getYCoordinate()+1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // queen moving up-left
        p.setValues(position.getXCoordinate() -1, position.getYCoordinate() +1);
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() -1, p.getYCoordinate()+1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // queen moving down-right
        p.setValues(position.getXCoordinate() +1, position.getYCoordinate() -1);
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() +1, p.getYCoordinate()-1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }

        // queen moving down-left
        p.setValues(position.getXCoordinate() -1, position.getYCoordinate() -1);
        while(getBoard().isBoardPosition(p) && !getBoard().positionIsOccupied(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
            p.setValues(p.getXCoordinate() -1, p.getYCoordinate()-1);
        }
        if(getBoard().isBoardPosition(p) && checkMove(p)){
            moves[p.getXCoordinate()][p.getYCoordinate()] = true;
        }


        return moves;
    }
    public String toString(){
        return "Q";
    }
}
