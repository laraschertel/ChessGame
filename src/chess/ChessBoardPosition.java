package chess;


import chessBoardGame.ChessPosition;

public class ChessBoardPosition{
     private final char sCoordinate;
     private final int iCoordinate;


     public ChessBoardPosition(char sCoordinate, int iCoordinate) throws GameException {
         if(sCoordinate < 'a' || sCoordinate > 'h' || iCoordinate < 1 || iCoordinate > 8){
             throw new GameException("Only positions between a1 and h8 are valid");
         }
         this.sCoordinate = sCoordinate;
         this.iCoordinate = iCoordinate;

     }

     protected ChessPosition toPosition() throws GameException {
         return new ChessPosition((8 - iCoordinate), (sCoordinate - 'a') );

     }

     public static ChessBoardPosition fromPosition(ChessPosition position) throws GameException {
         return new ChessBoardPosition((char)('a' + position.getYCoordinate()),8 - position.getXCoordinate());
     }

     public char getSCoordinate() {return this.sCoordinate;}
     public int getICoordinate() {return this.iCoordinate; }

    public String toString (){
        return "" + sCoordinate + iCoordinate;
    }
}

