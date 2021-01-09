package chess;

import chessBoardGame.ChessPiece;
import network.ProtocolEngine;

import java.io.*;
import java.util.Random;

public class ChessTCPProtocolEngine extends ChessProtocolEngine implements Runnable, ProtocolEngine {
    private static final String DEFAULT_NAME = "anonymousProtocolEngine";
    private String name;
    private OutputStream os;
    private InputStream is;
    private final Chess gameEngine;

    public static final int METHOD_SET = 1;

    private Thread protocolThread = null;
    private boolean oracle;
    private String partnerName;

    public ChessTCPProtocolEngine(Chess gameEngine, String name) {
        this.gameEngine = gameEngine;
        this.name = name;
    }

    public ChessTCPProtocolEngine(Chess gameEngine) {
        this(gameEngine, DEFAULT_NAME);
    }



    public boolean set(ChessBoardPosition currentPosition, ChessBoardPosition desiredPosition) throws GameException, StatusException {
        this.log("send set message to other side");
        this.serializeSet(currentPosition , desiredPosition , this.os);
        return false;
    }

    private void deserializeSet() throws GameException {
        this.log("deserialize received set message");

        try {
            SetCommand setCommand = this.deserializeSet(this.is);
            // call method - but no need to keep result - it isn't sent back.
            this.gameEngine.set(setCommand.getCurrentPosition(), setCommand.getDesiredPosition());
        } catch (StatusException | IOException e) {
            throw new GameException("could not deserialize command", e);
        }
    }

    boolean read() throws GameException {
        this.log("Protocol Engine: read from input stream");
        DataInputStream dis = new DataInputStream(this.is);

        // read method id
        try {
            int commandID = dis.readInt();
            switch (commandID) {
                case METHOD_SET: this.deserializeSet(); return true;
                default: this.log("unknown method, throw exception id == " + commandID); return false;

            }
        } catch (IOException e) {
            this.log("IOException caught - most probably connection close - stop thread / stop engine");
            try {
                this.close();
            } catch (IOException ioException) {
                // ignore
            }
            return false;
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
                this.log("flip and take number " + localInt);
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

      this.notifyGamesSessionEstablished(ChessTCPProtocolEngine.this.oracle, (ChessTCPProtocolEngine.this.partnerName));
        try{
            boolean again = true;
            while(again){
                again = this.read();
            }
        }catch(GameException e){
            this.logError("exeption called in protocol engine thread - fatal and stop");
            e.printStackTrace();
            // leave while - end thread
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
        if(this.os != null) { this.os.close();}
        if(this.is != null) { this.is.close();}
    }

    private String produceLogString(String message) {
        StringBuilder sb = new StringBuilder();
        if(this.name != null) {
            sb.append(this.name);
            sb.append(": ");
        }

        sb.append(message);

        return sb.toString();
    }

    private void log(String message) {
        System.out.println(this.produceLogString(message));
    }

    private void logError(String message) {
        System.err.println(this.produceLogString(message));
    }

}

