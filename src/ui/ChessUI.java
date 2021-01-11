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

public class ChessUI  {
    public static void main(String[] args) throws IOException, GameException, StatusException {

        System.out.println("Welcome to Chess version0 0.1");

        if(args.length < 1){
            System.err.println("need playerName as parameter");
            System.exit(1);
        }

        System.out.println("Welcome " + args[0]);
        System.out.println("Let's play a game");

        ChessUICommand userCmd = new ChessUICommand(args[0], System.out, System.in);


        LinkedList<ICommand> commands = userCmd.returnsCommandList();
        do {
            System.out.println(buildCLIMenu(commands, "Chess Game 0.1 - select a command"));
            ICommand selectedCMD = selectCommand(commands);
            System.out.println(selectedCMD.execute());
        } while (true);
    }

    static public String buildCLIMenu(LinkedList<ICommand> commands, String headline) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator());
        builder.append( headline + System.lineSeparator());
        for (int i = 1; i < commands.size(); i++) {
            ICommand cmd = commands.get(i);
            builder.append(" " + i + "." + cmd.description() + System.lineSeparator());
        }
        builder.append(" " + 0 + "." + commands.get(0).description() + System.lineSeparator());
        return builder.toString();
    }

    static public ICommand selectCommand(LinkedList<ICommand> commands) {
        do {
            int select = Console.readIntegerFromStdIn("Please select an option:");
            if (select >= 0 && select < commands.size()) {
                return commands.get(select);
            }   System.out.println("\tWarning -> Please select a valid option between 0 and " + (commands.size() - 1) + ".");
        }
        while (true);
    }
}