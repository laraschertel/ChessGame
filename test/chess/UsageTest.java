package chess;

import org.junit.Assert;
import org.junit.Test;

public class UsageTest {
    public static final String ALICE = "Alice";
    public static final String BOB = "Bob";
    public static final String CLARA = "Clara";

    private  Chess getChess() {
        return new ChessImpl();
    }

    @Test
    public void goodPickColour1() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.white);
        Assert.assertEquals(ChessColor.white, aliceColor);
    }

    @Test
    public void goodPickColour2() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.white);
        Chess bobColor = chess.pick(BOB, ChessColor.black);
        Assert.assertEquals(ChessColor.white, aliceColor);
        Assert.assertEquals(ChessColor.black, bobColor);
    }

    @Test
    public void goodPickColour3() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.white);
        Chess bobColor = chess.pick(BOB, ChessColor.white);
        Assert.assertEquals(ChessColor.white, aliceColor);
        Assert.assertEquals(ChessColor.black, bobColor);
    }

    @Test
    public void goodPickColour4() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.white);
        Chess bobColor = chess.pick(BOB, ChessColor.white);
        Assert.assertEquals(ChessColor.black, aliceColor);
        Assert.assertEquals(ChessColor.white, bobColor);
    }

    @Test
    public void goodPickColour5() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.white);
        //reconsidered
        aliceColor = chess.pick(ALICE, ChessColor.black);
        Chess bobColor = chess.pick(BOB, ChessColor.white);
        Assert.assertEquals(ChessColor.black, aliceColor);
        Assert.assertEquals(ChessColor.white, bobColor);
    }

    @Test
    public void failurePickColor3Times() throws GameException, StatusException {
        Chess chess = this.getChess();
        chess.pick(ALICE, ChessColor.white);
        chess.pick(BOB, ChessColor.white);
        chess.pick(CLARA, ChessColor.white);

    }

    @Test
    public void goodSet1() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.black);
        Chess bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition position = new ChessBoardPosition("A", 3);

        Assert.assertFalse(chess.set(ChessColor.white, ChessPieces.pawn, position));


    }

    @Test(expected = GameException.class)
    public void failureSetOutside1() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.black);
        Chess bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition position = new ChessBoardPosition("A", 10);

        Assert.assertFalse(chess.set(ChessColor.white, ChessPieces.pawn, position));

    }

    @Test(expected = StatusException.class)
    public void failureStatus1() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.black);
        Chess bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition position = new ChessBoardPosition("A", 3);

        chess.set(ChessColor.white, ChessPieces.rook, position);

        chess.pick(ALICE, ChessColor.white);

    }

    @Test(expected = GameException.class)
    public void failureWrongPosition1() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.black);
        Chess bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition position = new ChessBoardPosition("B", 2);

        chess.set(ChessColor.white, ChessPieces.bishop, position);

        chess.pick(ALICE, ChessColor.white);

    }

    @Test(expected = GameException.class)
    public void failureWrongPosition2() throws GameException, StatusException {
        Chess chess = this.getChess();
        Chess aliceColor = chess.pick(ALICE, ChessColor.black);
        Chess bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition position = new ChessBoardPosition("A", 6);

        chess.set(ChessColor.black, ChessPieces.bishop, position);

        chess.pick(ALICE, ChessColor.white);

    }



}
