package chessBoardGame;

import chess.ChessBoardPosition;
import chess.GameException;

public class ChessPosition {
    private int xCoordinate;
    private int yCoordinate;


    public ChessPosition(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;

    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setValues(int xCoordinate, int yCoordinate) {
        this.yCoordinate = yCoordinate;
        this.xCoordinate = xCoordinate;
    }

    @Override
    public String toString() {
        return xCoordinate + ", " + yCoordinate;
    }
}
