package chess;


 class ChessBoardPosition{
     private final String sCoordinate;
     private final int iCoordinate;


     ChessBoardPosition(String sCoordinate, int iCoordinate){
         this.sCoordinate = sCoordinate;
         this.iCoordinate = iCoordinate;

     }

     String getSCoordinate() {return this.sCoordinate;}
     int getICoordinate() {return this.iCoordinate; }

}
