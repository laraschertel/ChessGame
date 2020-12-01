package chess;

import java.io.*;

public class ChessProtocolEngine implements Chess {
    private final OutputStream os;
    private final InputStream is;
    private final Chess gameEngine;

    private static final int METHOD_PICK = 0;
    private static final int METHOD_SET = 1;

    private static final int COLOR_WHITE = 0;
    private static final int COLOR_BLACK = 1;

    private static final int PIECE_PAWN = 0;
    private static final int PIECE_ROOK = 1;
    private static final int PIECE_KNIGHT = 2;
    private static final int PIECE_BISHOP = 3;
    private static final int PIECE_QUEEN = 4;
    private static final int PIECE_KING = 5;



    public ChessProtocolEngine(InputStream is, OutputStream os, Chess gameEngine) {
        this.is = is;
        this.os = os;
        this.gameEngine = gameEngine;
    }

    @Override
    public ChessColor pick(String userName, ChessColor wantedColor) throws GameException, StatusException {
        DataOutputStream dos = new DataOutputStream(this.os);

        try {
            // write method id
            dos.writeInt(METHOD_PICK);
            // write user name
            dos.writeUTF(userName);
            // serialize color
            switch (wantedColor) {
                case white:
                    dos.writeInt(COLOR_WHITE);
                    break;
                case black:
                    dos.writeInt(COLOR_BLACK);
                    break;
                default:
                    throw new GameException("unknown color: " + wantedColor);
            }
        } catch (IOException e) {
            throw new GameException("could not serialize command", e);
        }

        return null; // !! TODO ??
    }

    private void deserializePick() throws GameException {
        DataInputStream dis = new DataInputStream(this.is);
        ChessColor wantedColor = null;
        try {
            // read user name
            String userName = dis.readUTF();
            // read serialized color
            int colorInt = dis.readInt();
            switch (colorInt){
                case COLOR_WHITE: wantedColor = ChessColor.white; break;
                case COLOR_BLACK: wantedColor = ChessColor.black; break;
                default: throw new GameException("unknown color: " + wantedColor);
            }

            this.gameEngine.pick(userName, wantedColor);
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
            switch (color){
                case white: dos.writeInt(COLOR_WHITE); break;
                case black: dos.writeInt(COLOR_BLACK); break;
                default: throw new GameException("unknown color: " + color);
            }
            switch (piece){
                case pawn: dos.writeInt(PIECE_PAWN); break;
                case rook: dos.writeInt(PIECE_ROOK); break;
                case knight: dos.writeInt(PIECE_KNIGHT); break;
                case bishop: dos.writeInt(PIECE_BISHOP); break;
                case queen: dos.writeInt(PIECE_QUEEN); break;
                case king: dos.writeInt(PIECE_KING); break;
            }
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
       ChessColor color = null;
       ChessPieces piece = null;
        try{

            // read serialize color
            int colorInt = dis.readInt();
            switch (colorInt) {
                case COLOR_WHITE: color = ChessColor.white; break;
                case COLOR_BLACK: color = ChessColor.black; break;
                default: throw new GameException("unknown color: " + color);
            }
            int pieceInt = dis.readInt();
            switch (pieceInt){
                case PIECE_PAWN: piece = ChessPieces.pawn; break;
                case PIECE_ROOK: piece = ChessPieces.rook; break;
                case PIECE_KNIGHT: piece = ChessPieces.knight; break;
                case PIECE_BISHOP: piece = ChessPieces.bishop; break;
                case PIECE_QUEEN: piece = ChessPieces.queen; break;
                case PIECE_KING: piece = ChessPieces.king; break;
            }
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
        }catch(IOException | StatusException e){
            throw new GameException("could not serialize command", e);
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
                default:
                    throw new GameException("unknown method id: " + commandID);
            }
        } catch (IOException e) {
            throw new GameException("could not deserialize command", e);
        }
    }
}