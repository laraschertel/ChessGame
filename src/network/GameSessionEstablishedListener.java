package network;

import chess.ChessProtocolEngine;

/**
 * call back interface
 */
public interface GameSessionEstablishedListener {
    /**
     * is called when oracle is created
     * @param oracle
     * @param partnerName
     */
    void gameSessionEstablished(boolean oracle, String partnerName );
}
