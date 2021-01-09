package chess;

import chessBoardGame.ChessPiece;
import network.GameSessionEstablishedListener;
import pieces.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static chess.ChessTCPProtocolEngine.METHOD_SET;


public abstract class ChessProtocolEngine implements Chess {
    private static final int COLOR_WHITE = 0;
    private static final int COLOR_BLACK = 1;

    private static final int PIECE_PAWN = 0;
    private static final int PIECE_ROOK = 1;
    private static final int PIECE_KNIGHT = 2;
    private static final int PIECE_BISHOP = 3;
    private static final int PIECE_QUEEN = 4;
    private static final int PIECE_KING = 5;
    private Pawn pawn;
    private Bishop bishop;
    private Rook rook;
    private Knight knight;
    private Queen queen;
    private King king;



    void serializeSet(ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition, OutputStream os) throws GameException, StatusException {
        DataOutputStream dos = new DataOutputStream(os);

        try{
            //write method id
            dos.writeInt(METHOD_SET);
            // serialize color
           // dos.writeInt(this.getIntValue4Color(color));
            // serialize piece
            //dos.writeInt(this.getIntValue4Piece(piece));
            // serialize position coordinates
            dos.writeChar(currentPosition.getSCoordinate());
            dos.writeInt(currentPosition.getICoordinate());

            dos.writeChar(desiredPosition.getSCoordinate());
            dos.writeInt(desiredPosition.getICoordinate());

        }catch(IOException e){
            throw new GameException("could not serialize command", e);
        }
    }

    SetCommand deserializeSet(InputStream is) throws GameException, IOException {
        DataInputStream dis = new DataInputStream(is);
            // read serialized color
           // ChessColor color = this.getColorFromIntValue(dis.readInt());
            // read serialized piece
            //ChessPiece piece = this.getPieceFromIntValue(dis.readInt());
            // read S current position
            char currentSCoordinate = dis.readChar();
            // read I current position
            int currentICoordinate = dis.readInt();

            ChessBoardPosition currentPosition = new ChessBoardPosition(currentSCoordinate, currentICoordinate);

            // read S desired position
            char desiredSCoordinate = dis.readChar();
            // read I desired position
            int desiredICoordinate = dis.readInt();

            ChessBoardPosition desiredPosition = new ChessBoardPosition(desiredSCoordinate, desiredICoordinate);

            // call method
            return new SetCommand(currentPosition, desiredPosition);
    }

    private ChessColor getColorFromIntValue (int colorInt) throws GameException{
        switch (colorInt){
            case COLOR_WHITE: return ChessColor.white;
            case COLOR_BLACK: return  ChessColor.black;
            default: throw new GameException("unknown color " + colorInt);
        }
    }

    private int getIntValue4Color(ChessColor color) throws GameException{
        switch (color){
            case white: return COLOR_WHITE;
            case black: return COLOR_BLACK;
            default: throw new GameException("unknown color " + color);

        }
    }

    private ChessPiece getPieceFromIntValue(int pieceInt) throws GameException{
        switch (pieceInt){
            case PIECE_PAWN: return pawn;
            case PIECE_ROOK: return rook;
            case PIECE_KNIGHT: return knight;
            case PIECE_BISHOP: return bishop;
            case PIECE_QUEEN: return queen;
            case PIECE_KING: return king;
            default: throw new GameException("unknown piece " + pieceInt);
        }
    }

    private int getIntValue4Piece(ChessPiece piece)throws GameException {
            if(piece == pawn) {
                return PIECE_PAWN;
            }
            if(piece == rook) {
                return PIECE_ROOK;
            }
            if(piece == knight) {
                return PIECE_KNIGHT;
            }
            if(piece == bishop) {
                return PIECE_BISHOP;
            }
            if(piece == queen) {
                return PIECE_QUEEN;
            }
            if(piece == king) {
                return PIECE_KING;
            } else{
            throw new GameException("unknown piece " + piece);
        }
    }


    private List<GameSessionEstablishedListener> sessionCreatedListenerList = new ArrayList<>();

    //////////////////////////////////// oracle creation listener ///////////////////////////////////////////

    void notifyGamesSessionEstablished(boolean oracle, String partnerName) {
        // call listener
        if (this.sessionCreatedListenerList != null && !this.sessionCreatedListenerList.isEmpty()) {
            for (GameSessionEstablishedListener oclistener : this.sessionCreatedListenerList) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1); // block a moment to let read thread start - just in case
                        } catch (InterruptedException e) {
                            // will not happen
                        }
                        oclistener.gameSessionEstablished(oracle, partnerName);
                    }
                }).start();
            }
        }
    }

    public void subscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener){
        this.sessionCreatedListenerList.add(ocListener);
    }


    public void unsubscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener){
        this.sessionCreatedListenerList.remove(ocListener);
    }

}