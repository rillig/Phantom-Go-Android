package de.roland_illig.phantomgo;

public enum GermanReferee {
    ;

    static String comment(RefereeResult res, Player color) {
        if (res.invalidReason != null) {
            switch (res.invalidReason) {
                case OTHER_STONE:
                    return "Da steht schon ein gegnerischer Stein.";
                case OWN_STONE:
                    return "Da steht schon ein eigener Stein.";
                case SUICIDE:
                    return "Selbstmord.";
                case KO:
                    return "Das Ko darf nicht sofort zurückgeschlagen werden.";
            }
        }

        boolean atari = res.atari;
        boolean selfAtari = res.selfAtari;
        int capturedStones = res.capturedStones;

        String playerName = color == Player.BLACK ? "Schwarz" : "Weiß";
        String opponentName = color == Player.BLACK ? "Weiß" : "Schwarz";
        String opponentColor = color == Player.BLACK ? "weiß" : "schwarz";

        String captureText;
        if (capturedStones > 1) {
            captureText = "schlägt " + capturedStones + " " + opponentColor + "e Steine";
        } else if (capturedStones > 0) {
            captureText = "schlägt einen " + opponentColor + "en Stein";
        } else {
            captureText = null;
        }

        String atariText;
        if (atari && selfAtari) {
            atariText = "setzt " + opponentName + " und sich selbst ins Atari";
        } else if (atari) {
            atariText = "setzt " + opponentName + " ins Atari";
        } else if (selfAtari) {
            atariText = "setzt sich selbst ins Atari";
        } else {
            atariText = null;
        }

        if (captureText != null && atariText != null) {
            return playerName + " " + captureText + " und " + atariText + ".";
        }
        if (captureText != null) {
            return playerName + " " + captureText + ".";
        }
        if (atariText != null) {
            return playerName + " " + atariText + ".";
        }
        return opponentName + " ist dran.";
    }
}
