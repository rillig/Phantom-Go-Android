package de.roland_illig.phantomgo;

public class CountResult {
    public final int blackTerritory;
    public final int whiteTerritory;
    public final int blackCaptured;
    public final int whiteCaptured;
    public final int blackScore;
    public final int whiteScore;

    public CountResult(int blackTerritory, int whiteTerritory, int blackCaptured, int whiteCaptured) {
        this.blackTerritory = blackTerritory;
        this.whiteTerritory = whiteTerritory;
        this.blackCaptured = blackCaptured;
        this.whiteCaptured = whiteCaptured;
        this.blackScore = blackTerritory + blackCaptured;
        this.whiteScore = whiteTerritory + whiteCaptured;
    }

    @Override
    public String toString() {
        return "" +
                "black=" + blackTerritory + "+" + blackCaptured + ", " +
                "white=" + whiteTerritory + "+" + whiteCaptured;
    }
}
