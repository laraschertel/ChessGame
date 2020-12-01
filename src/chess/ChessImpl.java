package chess;

import java.util.HashMap;

public class ChessImpl implements Chess {
    private Status status = Status.START;
    HashMap<ChessColor, String> player = new HashMap<>();


    @Override
    public ChessColor pick(String userName, ChessColor wantedColor) throws GameException, StatusException {
       if(this.status != Status.START && this.status != Status.ONE_PICKED){
           throw new StatusException("pick call but wrong status");
       }

       ChessColor takenColor = null;
      // already chosen a color?
        takenColor = this.getTakenColor(userName, ChessColor.white);
        if(takenColor == null){
            takenColor = this.getTakenColor(userName, ChessColor.black);
       }

        // is this user number 2+?
        if(takenColor == null && this.player.values().size() == 2){
            throw new GameException("both colors taken but not from " + userName);
        }

        // user already has a color?
        if(takenColor != null){// yes - user has a color
            // wanted color?
            if(takenColor == wantedColor) return wantedColor;

            // user wants to change color - can it be changed?
            if(this.player.get(wantedColor) == null){ //yes - can change
                this.player.remove(takenColor);
                this.player.put(wantedColor, userName);
                return wantedColor;
            }else{ // no - cannot change - the other color was already chosen
                return takenColor;
            }

        }else { // no - no color chosen yet
            //wanted symbol available?
            if(this.player.get(wantedColor) == null){ // yes - symbol available
                this.player.put(wantedColor, userName);
                this.changeStatusAfterPickedColor();
                return wantedColor;
            }else { // no - wanted color was already chosen
                ChessColor otherColor = wantedColor == ChessColor.white? ChessColor.black : ChessColor.white;
                this.player.put(otherColor, userName);
                this.changeStatusAfterPickedColor();
                return otherColor;
            }

        }

    }

    private void changeStatusAfterPickedColor(){
        this.status = this.status == Status.START ? Status.ONE_PICKED : Status.ACTIVE_WHITE;
    }

    private ChessColor getTakenColor(String userName, ChessColor color){
        String name = this.player.get(color);
        if(name != null && name.equalsIgnoreCase(userName)){
            return color;
        }
        return null;
    }

    private ChessPieces[][] board = new ChessPieces[8][8]; // horizontal / vertical

    @Override
    public boolean set(ChessColor color, ChessPieces piece, ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws GameException, StatusException {
        if(this.status != Status.ACTIVE_WHITE && this.status != Status.ACTIVE_BLACK){
            throw new StatusException("set call but wrong status");
        }
        return false;
    }

}
