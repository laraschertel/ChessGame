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

public class ChessUICommand implements TCPStreamCreatedListener, GameSessionEstablishedListener, LocalBoardChangeListener {
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

    public ChessUICommand(String playerName, PrintStream os, InputStream is) throws IOException, GameException {
        this.playerName = playerName;
        this.os = os;
        this.inBufferedReader = new BufferedReader(new InputStreamReader(is));

        this.gameEngine = new ChessImpl(playerName);
        this.localBoard = this.gameEngine;
        this.localBoard.subscribeChangeListener(this);

    }

    public LinkedList<ICommand> returnsCommandList() {
        LinkedList<ICommand> cmds = new LinkedList<ICommand>();
        cmds.add(exit());
        cmds.add(set());
        cmds.add(open());
        cmds.add(connect());
       // cmds.add(print());
        cmds.add(printBoard());

        return cmds;
    }

    private ICommand set() {
        return new ICommand() {

            @Override
            public String execute() throws StatusException, GameException {

                try{
                    doSet();
                }catch(StatusException | GameException e){
                    System.out.println(e.getLocalizedMessage());
                }
                return " ";
            }
            @Override
            public String description() {
                return "set";
            }
        };
    }
    private  ICommand open() {
        return new ICommand() {

            @Override
            public String execute() throws StatusException, GameException {

                doOpen();
                return "waiting for other player to connect";
            }
            @Override
            public String description() {
                return "open";
            }
        };
    }
    private ICommand connect() {
        return new ICommand() {

            @Override
            public String execute() throws StatusException, GameException {

                doConnect();
                return "you are now connected";
            }
            @Override
            public String description() {
                return "connect";
            }
        };
    }

    private ICommand print() {
        return new ICommand() {

            @Override
            public String execute() throws StatusException, GameException, IOException {

                doPrint();
                return " ";
            }
            @Override
            public String description() {
                return "print";
            }
        };
    }
    private ICommand printBoard() {
        return new ICommand() {

            @Override
            public String execute() throws StatusException, GameException, IOException {

                doPrintBoard();
                return " ";
            }
            @Override
            public String description() {
                return "print";
            }
        };
    }

    private ICommand exit() {
        return new ICommand() {

            @Override
            public String execute() throws StatusException, GameException, IOException {

                doExit();
                return " exit (: bye bye";
            }
            @Override
            public String description() {
                return "exit";
            }
        };
    }

    public void doSet() throws StatusException, GameException {
        // call guards
        this.checkConnectionStatus();


        System.out.println("Please enter the current position of the piece that you want to move");
        char currentRow = Console.readCharFromStdin("row coordinate (letter): ");
        int currentColumn = Console.readIntegerFromStdIn("column coordinate(number): ");

        ChessBoardPosition currentPosition = new ChessBoardPosition(currentRow, currentColumn);

        boolean[][] possibleMoves = this.gameEngine.possibleMoves(currentPosition);
        ChessPrintStreamView.printBoard(this.gameEngine.getPieces(), possibleMoves);

        System.out.println("Please enter the desired position of the piece that you want to move");
        char desiredRow = Console.readCharFromStdin("row coordinate (letter): ");
        int desiredColumn = Console.readIntegerFromStdIn("column coordinate(number): ");



        ChessBoardPosition desiredPosition = new ChessBoardPosition(desiredRow, desiredColumn);


        this.gameEngine.set(currentPosition, desiredPosition);

        ChessPrintStreamView.printBoard(this.gameEngine.getPieces());
        System.out.println();


    }

    public void doExit() throws IOException {
        // shutdown engines which needs to be
        this.protocolEngine.close();
    }

    public void doOpen() {
        if (this.alreadyConnected()) return;

        this.tcpStream = new TCPStream(Chess.DEFAULT_PORT, true, this.playerName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();
    }

    public void doConnect() throws NoSuchElementException, GameException {
        if (this.alreadyConnected()) return;

        String hostname = null;


        this.tcpStream = new TCPStream(Chess.DEFAULT_PORT, false, this.playerName);
        this.tcpStream.setRemoteEngine(hostname);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();


    }

    public void doPrintBoard() throws NoSuchElementException, GameException {

        ChessPrintStreamView.printBoard(this.gameEngine.getPieces());

    }

    public void doPrint() throws IOException, GameException {

        //List<ChessPiece> captureChessPieces = new ArrayList<>();

       /* try {
            System.out.print(BoardColors.ANSI_RESET);
            ChessPrintStreamView.printChess(this.gameEngine, captureChessPieces);
            System.out.println();

        }catch (GameException | InputMismatchException e) {
            System.out.println(e.getMessage());

        }

        */

        if(this.gameEngine.getStatus() == Status.ENDED) {
            if(this.gameEngine.hasWon()) {
                System.out.println("you won");
            } else {
                System.out.println("you lost");
            }
        } else if(this.gameEngine.isActive()) {
            System.out.println("your turn");
        } else {
            System.out.println("please wait");
        }
    }

    ////////////////////////////////////////////////// guards //////////////////////////////////////////////

    /**
     * Guard method - checks if already connected
     *
     * @throws StatusException
     */
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

    public Status getStatus(){
        return this.localBoard.getStatus();
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




