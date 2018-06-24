package de.roland_illig.phantomgo

import android.content.res.Resources
import de.roland_illig.android.phantomgo.R

class Referee {
    companion object {

        fun comment(result: RefereeResult, color: Player, resources: Resources): String {
            when (result) {
                is RefereeResult.OtherStone -> return resources.getString(R.string.referee_other_stone)
                is RefereeResult.OwnStone -> return resources.getString(R.string.referee_own_stone)
                is RefereeResult.Suicide -> return resources.getString(R.string.referee_suicide)
                is RefereeResult.Ko -> return resources.getString(R.string.referee_ko)
            }

            val black = resources.getString(R.string.referee_black)
            val white = resources.getString(R.string.referee_white)
            val isBlack = color == Player.BLACK
            val me = if (isBlack) black else white
            val other = if (isBlack) white else black

            if (result is RefereeResult.Pass) {
                return resources.getString(R.string.referee_passes, me)
            }

            result as RefereeResult.Ok
            val atari = result.atari
            val selfAtari = result.selfAtari
            val captured = result.capturedStones

            if (captured > 0 && selfAtari && atari) {
                return resources.getQuantityString(R.plurals.referee_capture_atari_selfatari, captured, me, captured, other)
            }
            if (captured > 0 && selfAtari) {
                return resources.getQuantityString(R.plurals.referee_capture_selfatari, captured, me, captured)
            }
            if (captured > 0 && atari) {
                return resources.getQuantityString(R.plurals.referee_capture_atari, captured, me, captured, other)
            }
            if (captured > 0) {
                return resources.getQuantityString(R.plurals.referee_capture, captured, me, captured, other)
            }

            if (selfAtari && atari) {
                return resources.getString(R.string.referee_atari_selfatari, me, other)
            }
            if (selfAtari) {
                return resources.getString(R.string.referee_selfatari, me)
            }
            if (atari) {
                return resources.getString(R.string.referee_atari, me, other)
            }
            return resources.getString(R.string.referee_to_play, me, other)
        }
    }
}
