package ui;

import chess.*;
import chessBoardGame.ChessPiece;
import network.GameSessionEstablishedListener;
import network.TCPStream;
import network.TCPStreamCreatedListener;
import view.BoardColors;
import view.ChessPrintStreamView;

import java.io.*;
import java.util.*;

public class ChessUI implements TCPStreamCreatedListener, GameSessionEstablishedListener, LocalBoardChangeListener {
    private static final String PRINT = "print";
    private static final String EXIT = "exit";
    private static final String CONNECT = "connect";
    private static final String OPEN = "open";
    private static final String SET = "set";
    private final String playerName;
    private final PrintStream os;
    private final BufferedReader inBufferedReader;
    private final ChessImpl gameEngine;
    private final ChessLocalBoard localBoard;
    private TCPStream tcpStream;
    private String partnerName;
    private ChessTCPProtocolEngine protocolEngine;
    private Status status;
    private final List<ChessPiece> captureChessPieces = new ArrayList<>();
    //TODO: captured pieces are not showing - connection breaks after a while

    public static void main(String[] args) throws IOException, GameException {
        System.out.println("Welcome to Chess version0 0.1");

        if(args.length < 1){
            System.err.println("need playerName as parameter");
            System.exit(1);
        }

        System.out.println("Welcome " + args[0]);
        System.out.println("Let's play a game");

        ChessUI userCmd = new ChessUI(args[0], System.out, System.in);

        userCmd.printUsage();
        userCmd.runCommandLoop();


    }

    public ChessUI(String playerName, PrintStream os, InputStream is) throws IOException, GameException {
        this.playerName = playerName;
        this.os = os;
        this.inBufferedReader = new BufferedReader(new InputStreamReader(is));

        this.gameEngine = new ChessImpl(playerName);
        this.localBoard = this.gameEngine;
        this.localBoard.subscribeChangeListener(this);

    }

    private void printUsage() {
        StringBuilder b = new StringBuilder();

        b.append("\n");
        b.append("\n");
        b.append("valid commands:");
        b.append("\n");
        b.append(CONNECT);
        b.append(" .. connect as tcp client");
        b.append("\n");
        b.append(OPEN);
        b.append(" .. open port become tcp server");
        b.append("\n");
        b.append(PRINT);
        b.append(" .. print board");
        b.append("\n");
        b.append(SET);
        b.append(" .. set a piece");
        b.append("\n");
        b.append(EXIT);
        b.append(" .. exit");

        this.os.println(b.toString());
    }

    private void runCommandLoop() {
        boolean again = true;

        while(again){
            boolean rememberCommand = true;
            String cmdLineString = null;

            try{
                // read user input
                cmdLineString = inBufferedReader.readLine();

                // finish that loop if less than nothing came in
                if (cmdLineString == null) break;

                // trim whitespaces on both sides
                cmdLineString = cmdLineString.trim();

                // extract command
                int spaceIndex = cmdLineString.indexOf(' ');
                spaceIndex = spaceIndex != -1 ? spaceIndex : cmdLineString.length();

                // got command string
                String commandString = cmdLineString.substring(0, spaceIndex);

                // extract parameters string - can be empty
                String parameterString = cmdLineString.substring(spaceIndex);
                parameterString = parameterString.trim();

                // start command loop
                switch (commandString) {
                    case PRINT:
                        this.doPrint();
                        break;
                    case CONNECT:
                        this.doConnect(parameterString);
                        break;
                    case OPEN:
                        this.doOpen();
                        break;
                    case SET:
                        this.doSet();
                        this.doPrint();
                        break;
                    case "q": // convenience
                    case EXIT:
                        again = false; this.doExit(); break; // end loop

                    default:
                        this.os.println("unknown command:" + cmdLineString);
                        this.printUsage();
                        rememberCommand = false;
                        break;
                }
            } catch (IOException ex) {
                this.os.println("cannot read from input stream - fatal, give up");
                try {
                    this.doExit();
                } catch (IOException e) {
                    // ignore
                }
            } catch (StatusException ex) {
                this.os.println("wrong status: " + ex.getLocalizedMessage());
            } catch (GameException ex) {
                this.os.println("game exception: " + ex.getLocalizedMessage());
            } catch (RuntimeException ex) {
                this.os.println("runtime problems: " + ex.getLocalizedMessage());
            }

        }
    }

    ////////////////////////////////////// ui method implementations ////////////////////////////////////
    private void doSet() throws StatusException, GameException {
        // call guards
        this.checkConnectionStatus();

        System.out.println("Please enter the current position of the piece that you want to move");
        Scanner scanner = new Scanner(System.in);
        String cp = scanner.nextLine();
        ChessBoardPosition currentPosition = new ChessBoardPosition(cp.charAt(0), Integer.parseInt(cp.substring(1)));

        boolean[][] possibleMoves = this.gameEngine.possibleMoves(currentPosition);
        ChessPrintStreamView.printBoard(this.gameEngine.getPieces(), possibleMoves);

        System.out.println("Please enter the desired position of the piece that you want to move");
        String dp = scanner.nextLine();


        ChessBoardPosition desiredPosition = new ChessBoardPosition(dp.charAt(0), Integer.parseInt(dp.substring(1)));


        this.gameEngine.set(currentPosition, desiredPosition);


        ChessPrintStreamView.printBoard(this.gameEngine.getPieces());
        System.out.println();

    }

    private void doExit() throws IOException {
        // shutdown engines which needs to be
        this.protocolEngine.close();
    }

    private void doOpen() {
        if (this.alreadyConnected()) return;

        this.tcpStream = new TCPStream(Chess.DEFAULT_PORT, true, this.playerName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();
    }

    private void doConnect(String parameterString) throws NoSuchElementException, GameException {
        if (this.alreadyConnected()) return;

        String hostname = null;

        try {
            StringTokenizer st = new StringTokenizer(parameterString);
            hostname = st.nextToken();
        }catch(NoSuchElementException e){
            System.out.println("no hostname provided - take localhost");
            hostname = "localhost";
        }

        this.tcpStream = new TCPStream(Chess.DEFAULT_PORT, false, this.playerName);
        this.tcpStream.setRemoteEngine(hostname);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();

    }

    private void doPrint() throws IOException, GameException {


            try {
                System.out.print(BoardColors.RESET);
                ChessPrintStreamView.printChess(this.gameEngine, this.captureChessPieces);
                System.out.println();

            }catch (GameException | InputMismatchException e) {
                System.out.println(e.getMessage());

            }

        if(this.gameEngine.getStatus() == Status.ENDED) {
            if(this.gameEngine.hasWon()) {
                System.out.println("you won");
            } else {
                System.out.println("you lost");
            }
        } else if(this.gameEngine.isActive()) {
            System.out.println("your turn");
        } else {
            System.out.println("please wait for the other player to make a move");
        }
    }

    ////////////////////////////////////////////////// guards //////////////////////////////////////////////

    /**
     * Guard method - checks if already connected
     *
     * @throws StatusException

         **/

    private void checkConnectionStatus() throws StatusException {
        if (this.protocolEngine == null) {
            throw new StatusException("not yet connected - call connect or open before");
        }
    }

    //////////////////////////////////////  helper : dont repeat yourself //////////////////////////////////////////////

    private boolean alreadyConnected() {
        if (this.tcpStream != null) {
            System.err.println("connection already established or connection attempt in progress");
            return true;
        }

        return false;
    }


    ///////////////////////////////////////// listener //////////////////////////////////////////////////////

    @Override
    public void streamCreated(TCPStream stream) {
        // connection established - setup protocol engine
        System.out.println("stream created - setup engine - we can play quite soon.");
        this.protocolEngine = new ChessTCPProtocolEngine(this.gameEngine, this.playerName);
        this.gameEngine.setProtocolEngine(protocolEngine);

        this.protocolEngine.subscribeGameSessionEstablishedListener(this);

        try {
            protocolEngine.handleConnection(stream.getInputStream(), stream.getOutputStream());
        } catch (IOException e) {
            System.err.println("cannot get streams from tcpStream - fatal, give up: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    @Override
    public void changed() {
        try{
            this.doPrint();
        }catch(IOException | GameException e){
            System.err.println("very very unexpected: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {
        System.out.println("game session created");
        this.partnerName = partnerName;

        if(oracle){
            System.out.println("your turn");
        }else{
            System.out.println("wait for other player to set a piece");
        }

    }

}


