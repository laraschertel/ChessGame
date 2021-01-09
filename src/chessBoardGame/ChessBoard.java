package chessBoardGame;

import chess.ChessBoardPosition;
import chess.GameException;

public class ChessBoard {
    private int rows;
    private int columns;
    private ChessPiece[][] pieces;

    public ChessBoard(int rows, int columns) throws GameException {
        if (rows < 1 || columns < 1) {
            throw new GameException("The board must have at least one rows and one column");
        }
        this.rows = rows;
        this.columns = columns;
        pieces = new ChessPiece[rows][columns];
    }

    public int getRows(){
        return rows;
    }

    public int getColumns(){
        return columns;
    }

    public ChessPiece piece(int sCoordinate, int iCoordinate) throws GameException {
        if(!isBoardPosition(sCoordinate, iCoordinate)){
            throw new GameException("The position is not on the board");
        }
        return pieces[sCoordinate][iCoordinate];
    }

    public ChessPiece piece(ChessPosition position) throws GameException {
        if (!isBoardPosition(position)){
            throw new GameException("The position is not on the board");
        }
        return pieces[position.getXCoordinate()][position.getYCoordinate()];
    }

    public  void placePiece(ChessPiece piece, ChessPosition position) throws GameException {
        if(positionIsOccupied(position)){
            throw new GameException("There is already a piece at this position "+ position);
        }
        pieces[position.getXCoordinate()][position.getYCoordinate()] = piece;
        piece.position = position;
    }

    public ChessPiece removePiece (ChessPosition position) throws GameException {
        if(!isBoardPosition(position)){
            throw new GameException("The position is outside the board");
        }
        if (piece(position) == null) {
            return null;
        }
        ChessPiece aux = piece(position);
        aux.position = null;
        pieces[position.getXCoordinate()][position.getYCoordinate()] = null;
        return aux;
    }

    private boolean isBoardPosition(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public boolean isBoardPosition(ChessPosition position) {
        return isBoardPosition(position.getXCoordinate(), position.getYCoordinate());
    }

    public boolean positionIsOccupied(ChessPosition position) throws GameException {
        if (!isBoardPosition(position)) {
            throw new GameException("The position is outside the board");
        }
        return piece(position) != null;
    }


}


