package chess;


 public class ChessBoardPosition{
     private final char sCoordinate;
     private final int iCoordinate;


     ChessBoardPosition(char sCoordinate, int iCoordinate) throws GameException {
         if(sCoordinate < 'a' || sCoordinate > 'h' || iCoordinate < 1 || iCoordinate > 8){
             throw new GameException("Only positions between a1 and h8 are valid");
         }
         this.sCoordinate = sCoordinate;
         this.iCoordinate = iCoordinate;

     }

     protected ChessBoardPosition desiredPosition() throws GameException {
         return new ChessBoardPosition((char)(8 - iCoordinate), sCoordinate - 'a' );

     }

     protected static ChessBoardPosition currentPosition(ChessBoardPosition position){
         return new ChessBoardPosition((char)('a' + position.getSCoordinate(),8 - position.getICoordinate());
     }

     public char getSCoordinate() {return this.sCoordinate;}
     public int getICoordinate() {return this.iCoordinate; }

}
