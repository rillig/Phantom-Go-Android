package de.roland_illig.phantomgo;

import java.util.Locale;
import java.util.StringJoiner;

public class RefereeResult {

    public final InvalidReason invalidReason;
    public final boolean atari;
    public final boolean selfAtari;
    public final int capturedStones;
    public final boolean pass;

    private RefereeResult(InvalidReason invalidReason) {
        this.invalidReason = invalidReason;
        this.atari = false;
        this.selfAtari = false;
        this.capturedStones = 0;
        this.pass = false;
    }

    private RefereeResult() {
        this.invalidReason = null;
        this.atari = false;
        this.selfAtari = false;
        this.capturedStones = 0;
        this.pass = true;
    }

    private RefereeResult(boolean atari, boolean selfAtari, int capturedStones) {
        this.invalidReason = null;
        this.atari = atari;
        this.selfAtari = selfAtari;
        this.capturedStones = capturedStones;
        this.pass = false;
    }

    public static RefereeResult ok(boolean atari, boolean selfAtari, int capturedStones) {
        return new RefereeResult(atari, selfAtari, capturedStones);
    }

    @Override
    public String toString() {
        if (invalidReason != null) {
            return invalidReason.toString().toLowerCase(Locale.ROOT);
        }
        if (pass) {
            return "pass";
        }
        if (!atari && !selfAtari && capturedStones == 0) {
            return "ok";
        }
        StringJoiner joiner = new StringJoiner(", ");
        if (atari) {
            joiner.add("atari");
        }
        if (selfAtari) {
            joiner.add("selfAtari");
        }
        if (capturedStones > 0) {
            joiner.add("captured " + capturedStones);
        }
        return joiner.toString();
    }

    public static RefereeResult pass() {
        return new RefereeResult();
    }

    public static RefereeResult ownStone() {
        return new RefereeResult(InvalidReason.OWN_STONE);
    }

    public static RefereeResult otherStone() {
        return new RefereeResult(InvalidReason.OTHER_STONE);
    }

    public static RefereeResult suicide() {
        return new RefereeResult(InvalidReason.SUICIDE);
    }

    public static RefereeResult ko() {
        return new RefereeResult(InvalidReason.KO);
    }

    public enum InvalidReason {
        OWN_STONE, OTHER_STONE, SUICIDE, KO
    }
}
