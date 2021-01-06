package chess;

import network.ProtocolEngine;
import network.TCPStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class ProtocolEngineTest {
    public static final String ALICE = "Alice";
    public static final int PORTNUMBER = 5555;
    private static final String BOB = "Bob";
    private static int port = 0;
    public static final long TEST_THREAD_SLEEP_DURATION = 1000;

    private Chess getChessEngine(InputStream is, OutputStream os, Chess gameEngine) throws IOException {
        ChessProtocolEngine chessProtocolEngine = new ChessProtocolEngine(gameEngine, ALICE);
        chessProtocolEngine.handleConnection(is, os);
        return chessProtocolEngine;

    }
/*
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

 */
    private int getPortNumber() {
        if(ProtocolEngineTest.port == 0) {
            ProtocolEngineTest.port = PORTNUMBER;
        } else {
            ProtocolEngineTest.port++;
        }

        System.out.println("use portnumber " + ProtocolEngineTest.port);
        return ProtocolEngineTest.port;
    }

    @Test
    public void integrationTest1() throws GameException, StatusException, IOException, InterruptedException{
        // there are players in this test; Alice and Bob

        // create Alice's game engine
        ChessImpl aliceGameEngine = new ChessImpl(ALICE);
        // create real protocol engine on Alice's side
        ChessProtocolEngine aliceChessProtocolEngine = new ChessProtocolEngine(aliceGameEngine, ALICE);

        aliceGameEngine.setProtocolEngine(aliceChessProtocolEngine);

        // create Bob's game engine
        ChessImpl bobGameEngine = new ChessImpl(BOB);
        // create real protocol engine on Bob's side
        ChessProtocolEngine bobChessProtocolEngine = new ChessProtocolEngine(bobGameEngine, BOB);

        bobGameEngine.setProtocolEngine(bobChessProtocolEngine);
        //////////////////////////////////////// setup tcp //////////////////////////////////////////////

        int port = this.getPortNumber();
        // this stream plays TCP server role during connection establishment
        TCPStream aliceSide = new TCPStream(port, true, "aliceSide");
        // this stream plays TCP client role during connection establishment
        TCPStream bobSide = new TCPStream(port, false, "bobSide");
        // start both streams
        aliceSide.start(); bobSide.start();
        // wait until the TCP connection is established
        aliceSide.waitForConnection(); bobSide.waitForConnection();

        //////////////////////////////////////// launch protocol engine  //////////////////////////////////////////////
        // give protocol engines streams and launch
        aliceChessProtocolEngine.handleConnection(aliceSide.getInputStream(), aliceSide.getOutputStream());
        bobChessProtocolEngine.handleConnection(bobSide.getInputStream(), bobSide.getOutputStream());

        // give it a moment - important stop this test thread - two threads must be launched
        System.out.println("give threads a moment to be launched");
        Thread.sleep(TEST_THREAD_SLEEP_DURATION);

        //////////////////////////////////////// run scenario //////////////////////////////////////////////

        ChessLocalBoard playerFirst = aliceGameEngine.isActive() ? aliceGameEngine : bobGameEngine;
        ChessLocalBoard playerSecond = aliceGameEngine.isActive() ? bobGameEngine : aliceGameEngine;



        ////////////////////////////////////////// test results /////////////////////////////////////////////

        Assert.assertTrue(aliceGameEngine.getStatus() == bobGameEngine.getStatus());


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                             tidy up                                                    //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        aliceChessProtocolEngine.close();
        bobChessProtocolEngine.close();
        // stop test thread to allow operating system to close sockets
        Thread.sleep(TEST_THREAD_SLEEP_DURATION);

    }
   /* @Test
    public void pickNetworkTest() throws GameException, StatusException, IOException, InterruptedException {
        // there are players in this test; Alice and Bob

        // create Alice's game engine tester
        ChessReadTester aliceGameEngineTester = new ChessReadTester();
        // create real protocol engine on Alice's side
        ChessProtocolEngine aliceChessProtocolEngine = new ChessProtocolEngine(aliceGameEngineTester, ALICE);

        // make it clear - this is a protocol engine
        ProtocolEngine aliceProtocolEngine = aliceChessProtocolEngine;
        // make it clear - it also supports the game engine interface
        Chess aliceGameEngineSide = aliceChessProtocolEngine;

        // create Bob's game engine tester
        ChessReadTester bobGameEngineTester = new ChessReadTester();
        // create real protocol engine on Bob's side
        ProtocolEngine bobProtocolEngine = new ChessProtocolEngine(bobGameEngineTester, BOB);

        //////////////////////////////////////// setup tcp //////////////////////////////////////////////

        int port = this.getPortNumber();
        // this stream plays TCP server role during connection establishment
        TCPStream aliceSide = new TCPStream(port, true, "aliceSide");
        // this stream plays TCP client role during connection establishment
        TCPStream bobSide = new TCPStream(port, false, "bobSide");
        // start both streams
        aliceSide.start(); bobSide.start();
        // wait until the TCP connection is established
        aliceSide.waitForConnection(); bobSide.waitForConnection();

        //////////////////////////////////////// launch protocol engine  //////////////////////////////////////////////

        // give protocol engines streams and launch
        aliceProtocolEngine.handleConnection(aliceSide.getInputStream(), aliceSide.getOutputStream());
        bobProtocolEngine.handleConnection(bobSide.getInputStream(), bobSide.getOutputStream());

        // give it a moment - important stop this test thread - two threads must be launched
        System.out.println("give threads a moment to be launched");
        Thread.sleep(TEST_THREAD_SLEEP_DURATION);

        ////////////////////////////////////////// run scenario /////////////////////////////////////////////

        // connection is established here - pick thread waits for results
        ChessColor alicePickResult = aliceGameEngineSide.pick(ALICE, ChessColor.white);

        ////////////////////////////////////////// test results /////////////////////////////////////////////

        // Alice got her color
        Assert.assertEquals(ChessColor.white, alicePickResult);
        // pick( "Alice", white) arrived on Bob's side
        Assert.assertTrue(bobGameEngineTester.lastCallPick);
        Assert.assertTrue(bobGameEngineTester.userName.equalsIgnoreCase(ALICE));
        Assert.assertEquals(ChessColor.white, bobGameEngineTester.color);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                             tidy up                                                    //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        aliceProtocolEngine.close();
        bobProtocolEngine.close();
        // stop test thread to allow operating system to close sockets
        Thread.sleep(TEST_THREAD_SLEEP_DURATION);
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

    */

}