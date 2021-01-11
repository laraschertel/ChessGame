package ui;

import chess.GameException;
import chess.StatusException;

import java.io.IOException;

public interface ICommand {

    String execute() throws StatusException, GameException, IOException;

    String description();

}
