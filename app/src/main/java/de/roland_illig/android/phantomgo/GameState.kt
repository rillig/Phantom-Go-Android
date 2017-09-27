package de.roland_illig.android.phantomgo

import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.CountingBoard
import java.io.Serializable

class GameState : Serializable {

    val refereeBoard = Board(9)
    val blackBoard = Board(9)
    val whiteBoard = Board(9)
    var countingBoard: CountingBoard? = null

    fun reset() {
        refereeBoard.reset()
        blackBoard.reset()
        whiteBoard.reset()
        countingBoard = null
    }

    companion object {
        private const val serialVersionUID = 1L

        val GLOBAL = GameState()
    }
}
