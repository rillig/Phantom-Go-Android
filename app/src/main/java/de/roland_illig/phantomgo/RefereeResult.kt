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
            val capturedStones: List<Intersection>) : RefereeResult() {

        override fun toString(): String {
            if (!atari && !selfAtari && capturedStones.isEmpty()) {
                return "ok"
            }
            val atariStr = if (atari) "atari" else ""
            val selfAtariStr = if (selfAtari) "selfAtari" else ""
            val capturedStr = if (capturedStones.isNotEmpty()) "captured ${capturedStones.size}" else ""
            return listOf(atariStr, selfAtariStr, capturedStr).filter { it != "" }.joinToString()
        }
    }
}
