package chess;

import chessBoardGame.ChessBoard;
import chessBoardGame.ChessPosition;
import network.GameSessionEstablishedListener;
import chessBoardGame.ChessPiece;
import pieces.*;
import view.ChessPrintStreamView;
import view.PrintStreamView;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ChessImpl implements Chess, GameSessionEstablishedListener, ChessLocalBoard {
   private static final String DEFAULT_PLAYERNAME = "anonPlayer";
   private String localPlayerName;
   private String remotePlayerName;
    private Status status = Status.START;
    HashMap<ChessColor, String> player = new HashMap<>();
    private ChessProtocolEngine protocolEngine;
    private ChessColor localColor;
    private ChessColor remoteColor;
    private boolean localWon;
    private boolean check;
    private boolean checkMate;
    private ChessPiece promoted;
    private ChessBoard board;


    private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
    private List<ChessPiece> capturedPieces = new ArrayList<>();

    public ChessImpl(String localPlayerName) throws GameException {

        this.localPlayerName = localPlayerName;
        this.board = new ChessBoard(8,8);
        initialSetup();
    }


    public PrintStreamView getPrintStreamView(){
        return new ChessPrintStreamView(this.board);
    }

    private ChessColor getTakenColor(String userName, ChessColor color){
        String name = this.player.get(color);
        if(name != null && name.equalsIgnoreCase(userName)){
            return color;
        }
        return null;
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

    public ChessPiece[][] getPieces() throws GameException {
        ChessPiece[][] moves = new ChessPiece[board.getColumns()][board.getRows()];
        for(int i = 0; i < board.getRows(); i++){
            for(int j = 0; j < board.getColumns(); j++){
                moves[i][j] = board.piece(i, j);
            }
        }
        return moves;
    }


    public boolean set(ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws StatusException, GameException {
        if(this.status != Status.ACTIVE_WHITE && this.status != Status.ACTIVE_BLACK){
            throw new StatusException("set called but wrong status");
        }

        ChessPosition current = currentPosition.toPosition();
        validateCurrentPosition(current);

        ChessPosition desired = desiredPosition.toPosition();
        validateDesiredPosition(current, desired);

        ChessPiece capturedPiece = makeMove(current, desired);

        
        if(testCheck(currentPlayerColor())){
            undoMove(current, desired, capturedPiece);
            throw new GameException("your can't put yourself under check");
        }

        ChessPiece movedPiece = board.piece(desired);


        // promotion - pawn reached the other end of the board
        promoted = null;
        if(movedPiece instanceof Pawn){
            if((movedPiece.getColor() == ChessColor.white && desired.getXCoordinate() == 0)
                    || (movedPiece.getColor() == ChessColor.black && desired.getXCoordinate() == 7)){
                promoted = board.piece(desired);
                promoted = replacePromotedPiece("A");
                
            }
        }
        
      boolean hasWon = this.hasWon(movedPiece);

        if(testCheck(opponentColor()) && !testCheckMate(opponentColor())){
            System.out.println("CHECK!");
        }

        if(testCheckMate(opponentColor())){
            System.out.println(this.localPlayerName + ": set " + movedPiece  + " - has won");
            this.status = Status.ENDED;
            if(this.localColor == movedPiece.getColor()) this.localWon = true;
        }else {
            this.status = this.status == Status.ACTIVE_WHITE ? Status.ACTIVE_BLACK : Status.ACTIVE_WHITE;
            System.out.println(this.localPlayerName + ": set " + movedPiece.getColor()  + " - not won, new status " + this.status);
        }


        // tell other side
        if(this.localColor == movedPiece.getColor() && this.protocolEngine != null){
            this.protocolEngine.set(currentPosition, desiredPosition);
        } else {
            // remote call
            this.notifyBoardChanged();
        }


        return hasWon;
    }

    public ChessPiece replacePromotedPiece(String type) throws GameException {
        if(promoted == null){
            throw new IllegalStateException("There is no piece to be promoted");
        }
        if(!type.equals("T") && !type.equals("A") && !type.equals("C") && !type.equals("B")){
            throw new InvalidParameterException("Invalid type of promotion");
        }
        
        ChessPosition position = promoted.getChessPosition().toPosition();
        ChessPiece piece = board.removePiece(position);
        piecesOnTheBoard.remove(piece);
        
        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, position);
        piecesOnTheBoard.add(newPiece);
        
        return newPiece;
        
    }

    private ChessPiece newPiece(String type, ChessColor color) {
        if (type.equals("B")) return new Bishop(board, color);
        if (type.equals("C")) return new Knight(board, color);
        if (type.equals("A")) return new Queen(board, color);
        return new Rook(board, color);
    }

    public ChessColor currentPlayerColor(){
        if(this.status == Status.ACTIVE_WHITE){
            return ChessColor.white;
        } else if(this.status == Status.ACTIVE_BLACK){
            return ChessColor.black;
        }else {
            return null;
        }
    }
    

    private void validateCurrentPosition(ChessPosition position) throws GameException {
        if (!board.positionIsOccupied(position)) {
            throw new GameException("There is no piece at this position");
        }
        if ((isActive() && this.localColor != (board.piece(position)).getColor())){
            throw new GameException("The chosen piece is not yours");
        }
        if(!board.piece(position).isThereAnyPossibleMove()){
            throw new GameException("There are no possible moves for the chosen piece");
        }
    }

    private void validateDesiredPosition(ChessPosition current, ChessPosition desired) throws GameException {
        if(!board.piece(current).possibleMove(desired)){
            throw new GameException("The chosen piece cannot be moved to the desired position");
        }
    }
    public boolean[][] possibleMoves(ChessBoardPosition currentPosition) throws GameException {
        ChessPosition position = currentPosition.toPosition();
        validateCurrentPosition(position);
        return board.piece(position).possibleMoves();
    }

    private boolean hasWon(ChessPiece piece) throws GameException {
        return testCheckMate(currentPlayerColor());

    }

    @Override
    public boolean hasWon() {
        return this.status == Status.ENDED && this.localWon;
    }

    @Override
    public boolean hasLost() {
        return this.status == Status.ENDED && !this.localWon;
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

    private  ChessColor otherColor(ChessColor color){
        if(color == ChessColor.white) {
            return ChessColor.black;
        }else{
            return ChessColor.white;
        }
    }

    private boolean testCheck(ChessColor color) throws GameException {
        ChessPosition kingPosition = king(color).getChessPosition().toPosition();
        List<ChessPiece> opponentPieces = listColorPieces(otherColor(color));

        for(int i= 0; i < opponentPieces.size(); i++ ){
            boolean[][] moves = opponentPieces.get(i).possibleMoves();
            if (moves[kingPosition.getXCoordinate()][kingPosition.getYCoordinate()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(ChessColor color) throws GameException {
        if(!testCheck(color)){
            return false;
        }
        List<ChessPiece> list = listColorPieces(color);
        for(int i= 0; i < list.size(); i++ ){
            boolean[][] moves = list.get(i).possibleMoves();
            for(int x=0; x < board.getRows(); x++){
                for(int y=0; y< board.getColumns(); y++){
                    if(moves[x][y]){
                        ChessPosition current = (list.get(i)).getChessPosition().toPosition();
                        ChessPosition desired = new ChessPosition(x, y);
                        ChessPiece capturedPiece = makeMove(current, desired);
                        boolean testCheck = testCheck(color);
                        undoMove(current, desired, capturedPiece);
                        if(!testCheck){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    private ChessPiece makeMove(ChessPosition current, ChessPosition desired) throws GameException {
        ChessPiece piece =  board.removePiece(current);
        //piece.increaseMoveCount();
        ChessPiece capturedPiece = board.removePiece(desired);
        board.placePiece(piece, desired);
        if(capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }
        return capturedPiece;
    }

    private void undoMove(ChessPosition current, ChessPosition desired, ChessPiece capturedPiece) throws GameException {
        ChessPiece piece =  board.removePiece(desired);
       // piece.decreaseMoveCount();
        board.placePiece(piece, current);

        if(capturedPiece != null){
            board.placePiece(capturedPiece, desired);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
    }

    private ChessColor opponentColor() {
        if(this.status == Status.ACTIVE_WHITE){
            return ChessColor.black;
        } else if(this.status == Status.ACTIVE_BLACK){
            return ChessColor.white;
        }else{
            return null;
        }
    }

    private ChessPiece king(ChessColor color) throws GameException {
        List<ChessPiece> list = listColorPieces(color);

        for(int i=0; i < list.size(); i++){
            if(list.get(i) instanceof King){
                return list.get(i);
            }
        }
        throw new IllegalStateException("There is no king with the color " + color);
    }


    private List<ChessPiece> listColorPieces(ChessColor color) throws GameException {
        int j =0;
        List<ChessPiece> listColorPiece = new ArrayList<>();
        for(int i= 0; i < piecesOnTheBoard.size(); i++){
            if(piecesOnTheBoard.get(i).getColor() == color){
                listColorPiece.add(piecesOnTheBoard.get(i));
            }
        }
        return listColorPiece;
        }


    ////////////////////////////////// constructor helper ////////////////////////////////////////////

    public void setProtocolEngine(ChessProtocolEngine protocolEngine){
        this.protocolEngine = protocolEngine;
        this.protocolEngine.subscribeGameSessionEstablishedListener(this);
    }


    ////////////////////////////////////////////// observed /////////////////////////////////////////////////

    private List<LocalBoardChangeListener> boardChangeListenerList = new ArrayList<>();

    @Override
    public void subscribeChangeListener(LocalBoardChangeListener changeListener) {
    }

    private void notifyBoardChanged(){
        // are there any listeners?
        if(this.boardChangeListenerList == null || this.boardChangeListenerList.isEmpty()) return;

        // yes - there are - create a thread and inform them
        (new Thread(new Runnable(){
                @Override
                public void run(){
                    for(LocalBoardChangeListener listener : ChessImpl.this.boardChangeListenerList){
                        listener.changed();
                    }
        }
        })).start();
    }

    // TODO
    @Override
    public boolean isCheck() {
        return this.check;
    }

    // TODO
    @Override
    public boolean isCheckMate() {
        return this.checkMate;
    }


    public ChessPiece getPromoted() {
        return promoted;
    }

    private void placeNewPiece(char sCoordinate, int iCoordinate, ChessPiece piece) throws GameException {
        board.placePiece(piece, new ChessBoardPosition(sCoordinate, iCoordinate).toPosition());
        piecesOnTheBoard.add(piece);
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

    private void initialSetup() throws GameException {
        placeNewPiece('a', 1, new Rook(board, ChessColor.white));
        placeNewPiece('b', 1, new Knight(board, ChessColor.white));
        placeNewPiece('c', 1, new Bishop(board,ChessColor.white));
        placeNewPiece('d', 1, new Queen(board, ChessColor.white));
        placeNewPiece('e', 1, new King(board, ChessColor.white));
        placeNewPiece('f', 1, new Bishop(board, ChessColor.white));
        placeNewPiece('g', 1, new Knight(board, ChessColor.white));
        placeNewPiece('h', 1, new Rook(board, ChessColor.white));
        placeNewPiece('a', 2, new Pawn(board, ChessColor.white));
        placeNewPiece('b', 2, new Pawn(board, ChessColor.white));
        placeNewPiece('c', 2, new Pawn(board, ChessColor.white));
        placeNewPiece('d', 2, new Pawn(board, ChessColor.white));
        placeNewPiece('e', 2, new Pawn(board, ChessColor.white));
        placeNewPiece('f', 2, new Pawn(board,ChessColor.white));
        placeNewPiece('g', 2, new Pawn(board, ChessColor.white));
        placeNewPiece('h', 2, new Pawn(board, ChessColor.white));

        placeNewPiece('a', 8, new Rook(board, ChessColor.black));
        placeNewPiece('b', 8, new Knight(board, ChessColor.black));
        placeNewPiece('c', 8, new Bishop(board, ChessColor.black));
        placeNewPiece('d', 8, new Queen(board,ChessColor.black));
        placeNewPiece('e', 8, new King(board, ChessColor.black));
        placeNewPiece('f', 8, new Bishop(board, ChessColor.black));
        placeNewPiece('g', 8, new Knight(board, ChessColor.black));
        placeNewPiece('h', 8, new Rook(board, ChessColor.black));
        placeNewPiece('a', 7, new Pawn(board, ChessColor.black));
        placeNewPiece('b', 7, new Pawn(board, ChessColor.black));
        placeNewPiece('c', 7, new Pawn(board, ChessColor.black));
        placeNewPiece('d', 7, new Pawn(board, ChessColor.black));
        placeNewPiece('e', 7, new Pawn(board, ChessColor.black));
        placeNewPiece('f', 7, new Pawn(board, ChessColor.black));
        placeNewPiece('g', 7, new Pawn(board, ChessColor.black));
        placeNewPiece('h', 7, new Pawn(board, ChessColor.black));
    }

}
