package chess;

import org.junit.Assert;
import org.junit.Test;

public class UsageTest {
    public static final String ALICE = "Alice";
    public static final String BOB = "Bob";
    public static final String CLARA = "Clara";

     private  ChessLocalBoard getChess() throws GameException {
        return new ChessImpl("undistributedBoard");
    }
/*
  @Test
    public void goodCompleteGame() throws GameException, StatusException{
         ChessLocalBoard chess = this.getChess();
         ChessColor aliceColor = chess.pick(ALICE, ChessColor.white);
         ChessColor bobColor = chess.pick(BOB, ChessColor.black);

         ChessBoardPosition currentPosition = new ChessBoardPosition('f',2 );
         ChessBoardPosition desiredPosition = new ChessBoardPosition('f',3 );

         Assert.assertFalse(chess.set(currentPosition, desiredPosition));

       currentPosition = new ChessBoardPosition('e',7 );
       desiredPosition = new ChessBoardPosition('e',6 );

         Assert.assertFalse(chess.set(currentPosition, desiredPosition));

    }

 */


   /*@Test
    public void goodPickColour1() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.white);
        Assert.assertEquals(ChessColor.white, aliceColor);
    }

    @Test
    public void goodPickColour2() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.white);
        ChessColor bobColor = chess.pick(BOB, ChessColor.black);
        Assert.assertEquals(ChessColor.white, aliceColor);
        Assert.assertEquals(ChessColor.black, bobColor);
    }

    @Test
    public void goodPickColour3() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.white);
        ChessColor bobColor = chess.pick(BOB, ChessColor.white);
        Assert.assertEquals(ChessColor.white, aliceColor);
        Assert.assertEquals(ChessColor.black, bobColor);
    }

    @Test
    public void goodPickColour4() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.black);
        ChessColor bobColor = chess.pick(BOB, ChessColor.white);
        Assert.assertEquals(ChessColor.black, aliceColor);
        Assert.assertEquals(ChessColor.white, bobColor);
    }

    @Test
    public void goodPickColour5() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.white);
        //reconsidered
        aliceColor = chess.pick(ALICE, ChessColor.black);
        ChessColor bobColor = chess.pick(BOB, ChessColor.white);
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
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.black);
        ChessColor bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition currentPosition = new ChessBoardPosition("A", 7);
        ChessBoardPosition desiredPosition = new ChessBoardPosition("A", 8);

        Assert.assertFalse(chess.serializeSet(ChessColor.white, ChessPieces.pawn, currentPosition, desiredPosition));


    }

    @Test(expected = GameException.class)
    public void failureSetOutside1() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.black);
        ChessColor bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition currentPosition = new ChessBoardPosition("A", 7);
        ChessBoardPosition desiredPosition = new ChessBoardPosition("A", 8);

        Assert.assertFalse(chess.serializeSet(ChessColor.white, ChessPieces.pawn, currentPosition, desiredPosition));

    }

    @Test(expected = StatusException.class)
    public void failureStatus1() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.black);
        ChessColor bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition currentPosition = new ChessBoardPosition("A", 7);
        ChessBoardPosition desiredPosition = new ChessBoardPosition("A", 8);

        chess.serializeSet(ChessColor.white, ChessPieces.rook, currentPosition, desiredPosition);

        chess.pick(ALICE, ChessColor.white);

    }

    @Test(expected = GameException.class)
    public void failureWrongPosition1() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.black);
        ChessColor bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition currentPosition = new ChessBoardPosition("A", 7);
        ChessBoardPosition desiredPosition = new ChessBoardPosition("A", 8);

        chess.serializeSet(ChessColor.white, ChessPieces.bishop, currentPosition, desiredPosition);

        chess.pick(ALICE, ChessColor.white);

    }

    @Test(expected = GameException.class)
    public void failureWrongPosition2() throws GameException, StatusException {
        Chess chess = this.getChess();
        ChessColor aliceColor = chess.pick(ALICE, ChessColor.black);
        ChessColor bobColor = chess.pick(BOB, ChessColor.white);

        ChessBoardPosition currentPosition = new ChessBoardPosition("A", 7);
        ChessBoardPosition desiredPosition = new ChessBoardPosition("A", 6);

        chess.serializeSet(ChessColor.black, ChessPieces.bishop, currentPosition, desiredPosition);

        chess.pick(ALICE, ChessColor.white);

    }


   */


}
