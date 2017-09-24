package de.roland_illig.phantomgo;

import java.io.Serializable;

public class RefereeResult implements Serializable {

    private static final long serialVersionUID = 20170924L;

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
            switch (invalidReason) {
                case OWN_STONE:
                    return "own";
                case OTHER_STONE:
                    return "other";
                case SUICIDE:
                    return "suicide";
                case KO:
                    return "ko";
            }
        }

        if (pass) {
            return "pass";
        }
        if (!atari && !selfAtari && capturedStones == 0) {
            return "ok";
        }

        String str = "";
        String sep = "";
        if (atari) {
            str = "atari";
            sep = ", ";
        }
        if (selfAtari) {
            str += sep + "selfAtari";
            sep = ", ";
        }
        if (capturedStones > 0) {
            str += sep + "captured " + capturedStones;
        }
        return str;
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
