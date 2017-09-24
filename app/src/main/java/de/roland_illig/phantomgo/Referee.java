package de.roland_illig.phantomgo;

import android.content.res.Resources;
import de.roland_illig.android.phantomgo.R;

public enum Referee {
    ;

    public static String comment(RefereeResult result, Player color, Resources resources) {
        if (result.invalidReason != null) {
            switch (result.invalidReason) {
                case OTHER_STONE:
                    return resources.getString(R.string.referee_other_stone);
                case OWN_STONE:
                    return resources.getString(R.string.referee_own_stone);
                case SUICIDE:
                    return resources.getString(R.string.referee_suicide);
                case KO:
                    return resources.getString(R.string.referee_ko);
            }
        }

        String black = resources.getString(R.string.referee_black);
        String white = resources.getString(R.string.referee_white);
        boolean isBlack = color == Player.BLACK;
        String me = isBlack ? black : white;
        String other = isBlack ? white : black;

        if (result.pass) {
            return resources.getString(R.string.referee_passes, me);
        }

        boolean atari = result.atari;
        boolean selfAtari = result.selfAtari;
        int captured = result.capturedStones;

        if (captured > 0 && selfAtari && atari) {
            return resources.getQuantityString(R.plurals.referee_capture_atari_selfatari, captured, me, captured, other);
        }
        if (captured > 0 && selfAtari) {
            return resources.getQuantityString(R.plurals.referee_capture_selfatari, captured, me, captured);
        }
        if (captured > 0 && atari) {
            return resources.getQuantityString(R.plurals.referee_capture_atari, captured, me, captured, other);
        }
        if (captured > 0) {
            return resources.getQuantityString(R.plurals.referee_capture, captured, me, captured, other);
        }

        if (selfAtari && atari) {
            return resources.getString(R.string.referee_atari_selfatari, me, other);
        }
        if (selfAtari) {
            return resources.getString(R.string.referee_selfatari, me);
        }
        if (atari) {
            return resources.getString(R.string.referee_atari, me, other);
        }
        return resources.getString(R.string.referee_to_play, other);
    }
}
