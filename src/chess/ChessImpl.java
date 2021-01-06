package chess;

import network.GameSessionEstablishedListener;

import java.util.HashMap;

public class ChessImpl implements Chess, GameSessionEstablishedListener, ChessLocalBoard {
   private static final String DEFAULT_PLAYERNAME = "anonPlayer";
   private final String localPlayerName;
   private String remotePlayerName;
    private Status status = Status.START;
    HashMap<ChessColor, String> player = new HashMap<>();
    private ChessProtocolEngine protocolEngine;
    private ChessColor localColor;
    private ChessColor remoteColor;
    private boolean localWon;

    public ChessImpl(String localPlayerName) {
        this.localPlayerName = localPlayerName;
    }


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

    ////////////////////////////////// constructor helper ////////////////////////////////////////////

    public void setProtocolEngine(ChessProtocolEngine protocolEngine){
        this.protocolEngine = protocolEngine;
        this.protocolEngine.subscribeGameSessionEstablishedListener(this);
    }

    @Override
    public Status getStatus(){ return this.status;}

    @Override
    public boolean isActive(){
        if(this.localColor == null) return false;

        return(
                (this.getStatus() == Status.ACTIVE_WHITE && this.localColor == ChessColor.white)
                ||
                (this.getStatus() == Status.ACTIVE_BLACK && this.localColor == ChessColor.black));
    }

    @Override
    public boolean hasWon() {
        return false;
    }

    @Override
    public boolean hasLost() {
        return false;
    }

    @Override
    public void subscribeChangeListener(LocalBoardChangeListener changeListener) {

    }


    @Override
    public ChessColor pick(String userName, ChessColor wantedColor) throws GameException, StatusException {
        return null;
    }

    @Override
    public boolean set(ChessColor color, ChessPieces piece, ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws GameException, StatusException {
        if(this.status != Status.ACTIVE_WHITE && this.status != Status.ACTIVE_BLACK){
            throw new StatusException("set call but wrong status");
        }
        return false;
    }


   /////////////////////////////////////////////// listener /////////////////////////////////////////////////////

    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {
        System.out.println(this.localPlayerName + ": gameSessionEstablished with " + partnerName + " | " + oracle);

        this.localColor = oracle ? ChessColor.white : ChessColor.black;
        this.remoteColor = this.localColor == ChessColor.white ? ChessColor.black : ChessColor.white;
        this.remotePlayerName = partnerName;

        // white always starts
        this.status = Status.ACTIVE_WHITE;

    }
}
