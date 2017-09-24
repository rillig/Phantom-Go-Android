package de.roland_illig.android.phantomgo;

import de.roland_illig.phantomgo.Board;
import de.roland_illig.phantomgo.CountingBoard;
import java.io.Serializable;

public class GameState implements Serializable {

    public static GameState GLOBAL = new GameState();

    private static final long serialVersionUID = 1L;

    public final Board refereeBoard = new Board(9);
    public final Board blackBoard = new Board(9);
    public final Board whiteBoard = new Board(9);
    public CountingBoard countingBoard;

    public void reset() {
        refereeBoard.reset();
        blackBoard.reset();
        whiteBoard.reset();
        countingBoard = null;
    }
}
