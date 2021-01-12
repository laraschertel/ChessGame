package chess;

public interface ChessLocalBoard extends Chess{

    /**
     * pick is now only a local call
     * @param userName
     * @param wantedColor
     * @return
     * @throws GameException
     * @throws StatusException
     */
    ChessColor pick(String userName, ChessColor wantedColor) throws GameException, StatusException;

    boolean set(ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws GameException, StatusException;

    /**
     *
     * @return game status
     */
    Status getStatus();

    /**
     *
     * @return true if active - can set a piece, false otherwise
     */
    boolean isActive();

    /**
     *
     * @return true if won, false otherwise
     */
    boolean hasWon();

    /**
     *
     * @return true if lost, false otherwise
     */
    boolean hasLost();

    /**
     * Subscribe for changes
     * @param changeListener
     */
    void subscribeChangeListener(LocalBoardChangeListener changeListener);



}
