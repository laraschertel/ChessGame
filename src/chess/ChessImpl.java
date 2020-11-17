package chess;

public class ChessImpl implements Chess {
    private Status status = Status.START;


    @Override
    public Chess pick(String userName, ChessColor wantedColor) throws GameException, StatusException {
       if(this.status != Status.START && this.status != Status.ONE_PICKED){
           throw new StatusException("pick call but wrong status");
       }
        return null;
    }

    @Override
    public boolean set(ChessColor color, ChessPieces piece, ChessBoardPosition position) throws GameException, StatusException {
        if(this.status != Status.ACTIVE_WHITE && this.status != Status.ACTIVE_BLACK){
            throw new StatusException("set call but wrong status");
        }
        return false;
    }

}
