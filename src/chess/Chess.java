package chess;


import chessBoardGame.ChessPiece;

import java.io.OutputStream;

public interface Chess {


    int DEFAULT_PORT = 6907;

    /**
     *
     * @param currentPosition current position
     * @param desiredPosition desired position
     * @return true if won, false otherwise
     */
    boolean set(ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws GameException, StatusException;
}
