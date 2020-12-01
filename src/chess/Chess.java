package chess;

public interface Chess {
    /**
     *
     * @param userName user name
     * @param wantedColor user asks for this color
     * @return selected color
     * @throws GameException both colors are already taken - it is at least the third attempt in a two players game
     * @throws StatusException can only be called if game hasn't started yet
     */
    ChessColor pick(String userName, ChessColor wantedColor) throws  GameException, StatusException;

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
