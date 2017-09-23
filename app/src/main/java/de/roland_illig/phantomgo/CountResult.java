package de.roland_illig.phantomgo;

public class CountResult {
    public final int blackTerritory;
    public final int whiteTerritory;
    public final int blackDead;
    public final int whiteDead;

    public CountResult(int blackTerritory, int whiteTerritory, int blackDead, int whiteDead) {
        this.blackTerritory = blackTerritory;
        this.whiteTerritory = whiteTerritory;
        this.blackDead = blackDead;
        this.whiteDead = whiteDead;
    }

    @Override
    public String toString() {
        return "black=" + blackTerritory + "+" + whiteDead + ", white=" + whiteTerritory + "+" + blackDead;
    }
}
