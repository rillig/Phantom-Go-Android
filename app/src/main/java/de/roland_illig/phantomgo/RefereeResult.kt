package de.roland_illig.phantomgo

sealed class RefereeResult : java.io.Serializable {

    abstract class Invalid(val reason: String) : RefereeResult() {
        override fun toString() = reason
    }

    object OwnStone : Invalid("ownStone")
    object OtherStone : Invalid("otherStone")
    object Suicide: Invalid("suicide")
    object Ko : Invalid("ko")

    object Pass : RefereeResult() {
        override fun toString() = "pass"
    }

    data class Ok(
            val atari: Boolean,
            val selfAtari: Boolean,
            val capturedStones: Int) : RefereeResult() {

        override fun toString(): String {
            if (!atari && !selfAtari && capturedStones == 0) {
                return "ok"
            }
            val atariStr = if (atari) "atari" else ""
            val selfAtariStr = if (selfAtari) "selfAtari" else ""
            val capturedStr = if (capturedStones > 0) "captured $capturedStones" else ""
            return listOf(atariStr, selfAtariStr, capturedStr).filter { it != "" }.joinToString()
        }
    }
}
