package chess;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ProtocolEngineTest {
    public static final String ALICE = "Alice";

    private Chess getChessEngine(InputStream is, OutputStream os, Chess gameEngine) {
        return new ChessProtocolEngine(is, os, gameEngine);

    }

    @Test
    public void pickTest1() throws GameException, StatusException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Chess chessProtocolSender = this.getChessEngine(null, baos, null);
        chessProtocolSender.pick(ALICE, ChessColor.white);

        // simulated network
        byte[] serializedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);

        ChessReadTester chessReceiver = new ChessReadTester();
        Chess chessProtocolReceiver = this.getChessEngine(bais, null, chessReceiver);

        ChessProtocolEngine chessEngine = (ChessProtocolEngine) chessProtocolReceiver;
        chessEngine.read();

        Assert.assertTrue(chessReceiver.lastCallPick);
        Assert.assertTrue(chessReceiver.userName.equalsIgnoreCase(ALICE));
        Assert.assertEquals(ChessColor.white, chessReceiver.color);

    }

    @Test
    public void setTest1() throws GameException, StatusException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Chess chessProtocolSender = this.getChessEngine(null, baos, null);

        ChessBoardPosition currentPosition = new ChessBoardPosition("A", 2);
        ChessBoardPosition desiredPosition = new ChessBoardPosition("A", 3);
        chessProtocolSender.set(ChessColor.white, ChessPieces.pawn, currentPosition, desiredPosition);

        // simulated network
        byte[] serializedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);

        ChessReadTester chessReceiver = new ChessReadTester();
        Chess chessProtocolReceiver = this.getChessEngine(bais, null, chessReceiver);


        ChessProtocolEngine chessEngine = (ChessProtocolEngine) chessProtocolReceiver;
        chessEngine.read();

        Assert.assertTrue(chessReceiver.lastCallSet);
        Assert.assertEquals(ChessColor.white, chessReceiver.color);
        Assert.assertEquals(ChessColor.white, chessReceiver.color);
        Assert.assertEquals(ChessPieces.pawn, chessReceiver.piece);
        Assert.assertTrue(chessReceiver.currentPosition.getSCoordinate().equalsIgnoreCase("A"));
        Assert.assertEquals(2, chessReceiver.currentPosition.getICoordinate());
        Assert.assertTrue(chessReceiver.desiredPosition.getSCoordinate().equalsIgnoreCase("A"));
        Assert.assertEquals(3, chessReceiver.desiredPosition.getICoordinate());


    }


    private class ChessReadTester implements Chess {
        private boolean lastCallPick = false;
        private boolean lastCallSet = false;

        private String userName = null;
        private ChessColor color;
        private ChessPieces piece;
        private ChessBoardPosition currentPosition;
        private ChessBoardPosition desiredPosition;

        @Override
        public ChessColor pick(String userName, ChessColor wantedColor) throws GameException, StatusException {
           this.lastCallPick = true;
           this.lastCallSet = false;
           this.userName = userName;
           this.color = wantedColor;

           return wantedColor;
        }

        @Override
        public boolean set(ChessColor color, ChessPieces piece, ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws GameException, StatusException {
            this.lastCallPick = false;
            this.lastCallSet = true;
            this.color = color;
            this.piece = piece;
            this.currentPosition = currentPosition;
            this.desiredPosition = desiredPosition;

            return false;
        }
    }

}