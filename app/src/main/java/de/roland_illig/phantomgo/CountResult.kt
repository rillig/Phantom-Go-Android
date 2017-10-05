package de.roland_illig.phantomgo

class CountResult(
        val blackTerritory: Int,
        val whiteTerritory: Int,
        val blackCaptured: Int,
        val whiteCaptured: Int)
    : java.io.Serializable {
    val blackScore: Int = blackTerritory + blackCaptured
    val whiteScore: Int = whiteTerritory + whiteCaptured

    override fun toString(): String {
        return "" +
                "black=" + blackTerritory + "+" + blackCaptured + ", " +
                "white=" + whiteTerritory + "+" + whiteCaptured
    }
}
