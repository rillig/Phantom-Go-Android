package de.roland_illig.phantomgo

class RefereeResult : java.io.Serializable {

    val invalidReason: InvalidReason?
    val atari: Boolean
    val selfAtari: Boolean
    val capturedStones: Int
    val pass: Boolean

    private constructor(invalidReason: InvalidReason) {
        this.invalidReason = invalidReason
        this.atari = false
        this.selfAtari = false
        this.capturedStones = 0
        this.pass = false
    }

    private constructor() {
        this.invalidReason = null
        this.atari = false
        this.selfAtari = false
        this.capturedStones = 0
        this.pass = true
    }

    private constructor(atari: Boolean, selfAtari: Boolean, capturedStones: Int) {
        this.invalidReason = null
        this.atari = atari
        this.selfAtari = selfAtari
        this.capturedStones = capturedStones
        this.pass = false
    }

    override fun toString(): String {
        if (invalidReason != null) {
            when (invalidReason) {
                RefereeResult.InvalidReason.OWN_STONE -> return "own"
                RefereeResult.InvalidReason.OTHER_STONE -> return "other"
                RefereeResult.InvalidReason.SUICIDE -> return "suicide"
                RefereeResult.InvalidReason.KO -> return "ko"
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
        private const val serialVersionUID = 20170924L

        fun ok(atari: Boolean, selfAtari: Boolean, capturedStones: Int)
                = RefereeResult(atari, selfAtari, capturedStones)

        fun pass() = RefereeResult()
        fun ownStone() = RefereeResult(InvalidReason.OWN_STONE)
        fun otherStone() = RefereeResult(InvalidReason.OTHER_STONE)
        fun suicide() = RefereeResult(InvalidReason.SUICIDE)
        fun ko() = RefereeResult(InvalidReason.KO)
    }
}
