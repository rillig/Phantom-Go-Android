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
                InvalidReason.OWN_STONE -> "ownStone"
                InvalidReason.OTHER_STONE -> "otherStone"
                InvalidReason.SUICIDE -> "suicide"
                InvalidReason.KO -> "ko"
            }
        }

        if (pass) {
            return "pass"
        }
        if (!atari && !selfAtari && capturedStones == 0) {
            return "ok"
        }

        val atariStr = if (atari) "atari" else ""
        val selfAtariStr = if (selfAtari) "selfAtari" else ""
        val capturedStr = if (capturedStones > 0) "captured $capturedStones" else ""
        return listOf(atariStr, selfAtariStr, capturedStr).filter { it != "" }.joinToString()
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
