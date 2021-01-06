package chess;

public interface Chess {


    /**
     *
     * @param color of the piece
     * @param piece to be placed on board
     * @param currentPosition current position
     * @param desiredPosition desired position
     * @return true if won, false otherwise
     */
    boolean set(ChessColor color, ChessPieces piece, ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws GameException, StatusException;
}
