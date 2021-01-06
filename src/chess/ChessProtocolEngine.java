package chess;

import network.GameSessionEstablishedListener;
import network.ProtocolEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChessProtocolEngine implements Chess, Runnable, ProtocolEngine {
    private OutputStream os;
    private InputStream is;
    private final Chess gameEngine;

    private static final int METHOD_PICK = 0;
    private static final int METHOD_SET = 1;
    private static final int RESULT_PICK = 2;

    private static final int COLOR_WHITE = 0;
    private static final int COLOR_BLACK = 1;

    private static final int PIECE_PAWN = 0;
    private static final int PIECE_ROOK = 1;
    private static final int PIECE_KNIGHT = 2;
    private static final int PIECE_BISHOP = 3;
    private static final int PIECE_QUEEN = 4;
    private static final int PIECE_KING = 5;

    private Thread protocolThread = null;
    private Thread pickWaitThread = null;
    private ChessColor pickResult;

    private boolean oracle;
    private String name;
    private String partnerName;


    public ChessProtocolEngine(Chess gameEngine, String username) {
        this.gameEngine = gameEngine;
    }



    @Override
    public ChessColor pick(String userName, ChessColor wantedColor) throws GameException, StatusException {
        System.out.println("send a pick message to the other player");
        DataOutputStream dos = new DataOutputStream(this.os);

        try {
            // write method id
            dos.writeInt(METHOD_PICK);
            // write user name
            dos.writeUTF(userName);
            // serialize color
            dos.writeInt(this.getIntValue4Color(wantedColor));

            // read result
            try{
                this.pickWaitThread = Thread.currentThread();
                Thread.sleep(Long.MAX_VALUE);
            }catch(InterruptedException e){
                // interrupted
                System.out.println("pick thread back - results arrived");
            }

            // remember - we are not waiting any longer
            this.pickWaitThread = null;

            return this.pickResult;
        } catch (IOException e) {
            throw new GameException("could not serialize command", e);
        }
    }



    private void deserializeResultPick() throws GameException {
        System.out.println("deserialize received pick result message");
        DataInputStream dis = new DataInputStream(this.is);
        ChessColor wantedColor = null;
        try{
            // read serialized color
            int colorInt = dis.readInt();
            // convert to color
            this.pickResult = this.getColorFromIntValue(colorInt);

            // wake up thread
            this.pickWaitThread.interrupt();
        }catch(IOException e){
            throw new GameException("could not deserialize command", e);
        }
    }

    private void deserializePick() throws GameException {
        DataInputStream dis = new DataInputStream(this.is);
        try {
            // read user name
            String userName = dis.readUTF();
            // read serialized color
            ChessColor wantedColor = this.getColorFromIntValue(dis.readInt());
            // call method
            ChessColor color = this.gameEngine.pick(userName, wantedColor);

            // write result
            System.out.println("going to send return value");
            DataOutputStream dos = new DataOutputStream(this.os);
            dos.writeInt(RESULT_PICK);
            dos.writeInt(this.getIntValue4Color(color));
        }catch (IOException | StatusException e){
            throw new GameException("could not deserialize command", e);
        }
    }


    @Override
    public boolean set(ChessColor color, ChessPieces piece, ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws GameException, StatusException {
        DataOutputStream dos = new DataOutputStream(this.os);

        try{
            //write method id
            dos.writeInt(METHOD_SET);
            // serialize color
            dos.writeInt(this.getIntValue4Color(color));
            // serialize piece
            dos.writeInt(this.getIntValue4Piece(piece));
            // serialize position coordinates
            dos.writeUTF(currentPosition.getSCoordinate());
            dos.writeInt(currentPosition.getICoordinate());

            dos.writeUTF(desiredPosition.getSCoordinate());
            dos.writeInt(desiredPosition.getICoordinate());

        }catch(IOException e){
            throw new GameException("could not serialize command", e);
        }
        return false;
    }

    private void deserializeSet() throws GameException{
       DataInputStream dis = new DataInputStream(this.is);
        try{

            // read serialized color
            ChessColor color = this.getColorFromIntValue(dis.readInt());
            // read serialized piece
            ChessPieces piece = this.getPieceFromIntValue(dis.readInt());
            // read S current position
            String currentSCoordinate = dis.readUTF();
            // read I current position
            int currentICoordinate = dis.readInt();

            ChessBoardPosition currentPosition = new ChessBoardPosition(currentSCoordinate, currentICoordinate);

            // read S desired position
            String desiredSCoordinate = dis.readUTF();
            // read I desired position
            int desiredICoordinate = dis.readInt();

            ChessBoardPosition desiredPosition = new ChessBoardPosition(desiredSCoordinate, desiredICoordinate);

            // call method
            this.gameEngine.set(color, piece, currentPosition, desiredPosition);

            // write result
            System.out.println("going to send return value");
            DataOutputStream dos = new DataOutputStream(this.os);
            dos.writeInt(RESULT_PICK);
            dos.writeInt(this.getIntValue4Color(color));
        }catch(IOException | StatusException e){
            throw new GameException("could not serialize command", e);
        }

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

    private ChessPieces getPieceFromIntValue(int pieceInt) throws GameException{
        switch (pieceInt){
            case PIECE_PAWN: return ChessPieces.pawn;
            case PIECE_ROOK: return ChessPieces.rook;
            case PIECE_KNIGHT: return ChessPieces.knight;
            case PIECE_BISHOP: return ChessPieces.bishop;
            case PIECE_QUEEN: return ChessPieces.queen;
            case PIECE_KING: return ChessPieces.king;
            default: throw new GameException("unknown piece " + pieceInt);
        }
    }

    private int getIntValue4Piece(ChessPieces piece)throws GameException {
        switch (piece) {
            case pawn:
                return PIECE_PAWN;
            case rook:
                return PIECE_ROOK;
            case knight:
                return PIECE_KNIGHT;
            case bishop:
                return PIECE_BISHOP;
            case queen:
                return PIECE_QUEEN;
            case king:
                return PIECE_KING;
            default: throw new GameException("unknown piece " + piece);
        }
    }

    public void read() throws GameException {
        DataInputStream dis = new DataInputStream(this.is);

        // read method id
        try {
            int commandID = dis.readInt();
            switch (commandID) {
                case METHOD_PICK:
                    this.deserializePick();
                    break;
                case METHOD_SET:
                    this.deserializeSet();
                    break;
                case RESULT_PICK: this.deserializeResultPick(); break;
                default:
                    throw new GameException("unknown method id: " + commandID);
            }
        } catch (IOException e) {
            throw new GameException("could not deserialize command", e);
        }
    }

    @Override
    public void run() {
        System.out.println("Protocol Engine started - flip a coin");
        long seed = this.hashCode() * System.currentTimeMillis();
        Random random = new Random(seed);

        int localInt = 0, remoteInt =0;
        try {
            DataOutputStream dos = new DataOutputStream(this.os);
            DataInputStream dis = new DataInputStream(this.is);
            do {
                localInt = random.nextInt();
                this.log("flip and take numer " + localInt);
                dos.writeInt(localInt);
                remoteInt = dis.readInt();
            }while(localInt == remoteInt);

            this.oracle = localInt < remoteInt;
            this.log("Flipped a coin and got an oracle == " + this.oracle);
            // this.oracleSet = true;

            // exchange names
            dos.writeUTF(this.name);
            this.partnerName = dis.readUTF();
        }catch(IOException e){
            e.printStackTrace();
        }

        // call listener
        if(this.sessionCreatedListenerList != null && !this.sessionCreatedListenerList.isEmpty()){
            for(GameSessionEstablishedListener oclistener : this.sessionCreatedListenerList){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1); // block a moment to let read thread start - just in case
                        } catch (InterruptedException e) {
                            // will not happen
                        }
                        oclistener.gameSessionEstablished(
                                ChessProtocolEngine.this.oracle,
                                ChessProtocolEngine.this.partnerName);
                    }

                }).start();
                    }
            }

        try{
            boolean again = true;
            while(again){
                again = this.read();
            }
        }catch(GameException e){
            this.logError("exeption called in protocol engine thread - fatal and stop");

    }

}


    @Override
    public void handleConnection(InputStream is, OutputStream os) throws IOException {
        this.is = is;
        this.os = os;

        this.protocolThread = new Thread(this);
        this.protocolThread.start();

    }

    @Override
    public void close() throws IOException {
        if(this.os != null){
            this.os.close();
        }
        if(this.is != null){
            this.is.close();
        }
    }

    private List<GameSessionEstablishedListener> sessionCreatedListenerList = new ArrayList<>();

    //////////////////////////////////// oracle creation listener ///////////////////////////////////////////

    @Override
    public void subscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener){
        this.sessionCreatedListenerList.add(ocListener);
    }

    @Override
    public void unsubscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener){
        this.sessionCreatedListenerList.remove(ocListener);
    }

}