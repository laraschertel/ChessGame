package chess;

import chessBoardGame.ChessPiece;

public class SetCommand {
   // private final ChessColor color;
    //private final ChessPiece piece;
    private final ChessBoardPosition currentPosition;
    private final ChessBoardPosition desiredPosition;

    public SetCommand(ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition ){
       // this.color = color;
        //this.piece = piece;
        this.currentPosition = currentPosition;
        this.desiredPosition = desiredPosition;
    }

   // ChessColor getColor(){ return this.color;}

   // ChessPiece getPiece(){ return this.piece;}

    ChessBoardPosition getCurrentPosition(){ return this.currentPosition;}

    ChessBoardPosition getDesiredPosition(){ return this.desiredPosition; }
}

