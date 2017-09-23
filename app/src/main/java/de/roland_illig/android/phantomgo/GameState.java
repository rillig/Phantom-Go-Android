package de.roland_illig.android.phantomgo;

import java.io.Serializable;

public class GameState implements Serializable {

    public static GameState GLOBAL = new GameState();

    private static final long serialVersionUID = 1L;

    public String blackName;
    public String whiteName;
    public int[][] board;
    public int counter;

    public GameState() {
        counter = 5;
        blackName = "Black";
        whiteName = "White";
        board = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
    }
}
