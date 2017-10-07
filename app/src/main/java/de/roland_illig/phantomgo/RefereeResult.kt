package de.roland_illig.phantomgo

class RefereeResult private constructor(
        val invalidReason: InvalidReason?,
        val atari: Boolean,
        val selfAtari: Boolean,
        val capturedStones: Int,
        val pass: Boolean) : java.io.Serializable {

    override fun toString(): String {
        if (invalidReason != null) {
            return when (invalidReason) {
                RefereeResult.InvalidReason.OWN_STONE -> "ownStone"
                RefereeResult.InvalidReason.OTHER_STONE -> "otherStone"
                RefereeResult.InvalidReason.SUICIDE -> "suicide"
                RefereeResult.InvalidReason.KO -> "ko"
            }
        }

        if (pass) {
            return "pass"
        }
        if (!atari && !selfAtari && capturedStones == 0) {
            return "ok"
        }

        var str = ""
        var sep = ""
        if (atari) {
            str = "atari"
            sep = ", "
        }
        if (selfAtari) {
            str += sep + "selfAtari"
            sep = ", "
        }
        if (capturedStones > 0) {
            str += sep + "captured " + capturedStones
        }
        return str
    }

    enum class InvalidReason {
        OWN_STONE, OTHER_STONE, SUICIDE, KO
    }

    companion object {
        fun ok(atari: Boolean, selfAtari: Boolean, capturedStones: Int)
                = RefereeResult(null, atari, selfAtari, capturedStones, false)

        fun pass() = RefereeResult(null, false, false, 0, true)
        fun ownStone() = RefereeResult(InvalidReason.OWN_STONE, false, false, 0, false)
        fun otherStone() = RefereeResult(InvalidReason.OTHER_STONE, false, false, 0, false)
        fun suicide() = RefereeResult(InvalidReason.SUICIDE, false, false, 0, false)
        fun ko() = RefereeResult(InvalidReason.KO, false, false, 0, false)
    }
}
