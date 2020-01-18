package de.roland_illig.phantomgo

sealed class RefereeResult : java.io.Serializable {

    abstract class Invalid(private val reason: String) : RefereeResult() {
        override fun toString() = reason
    }

    object OwnStone : Invalid("ownStone")
    object OtherStone : Invalid("otherStone")
    object Suicide : Invalid("suicide")
    object Ko : Invalid("ko")

    object Pass : RefereeResult() {
        override fun toString() = "pass"
    }

    data class Ok(
        val atari: Boolean,
        val selfAtari: Boolean,
        val capturedStones: List<Intersection>
    ) : RefereeResult() {

        override fun toString() = listOf(
            if (atari) "atari" else "",
            if (selfAtari) "selfAtari" else "",
            if (capturedStones.isNotEmpty()) "captured ${capturedStones.size}" else ""
        ).filter(String::isNotEmpty).joinToString().ifEmpty { "ok" }
    }
}
