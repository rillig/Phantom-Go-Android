package de.roland_illig.android.phantomgo.torus

import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.CountingBoard
import de.roland_illig.phantomgo.Rules

class ToroidalState(
    size: Int = 9
) : java.io.Serializable {
    val board = Board(size).apply { rules = Rules.Toroidal }
    private var counting: CountingBoard? = null
    val countingBoard: CountingBoard?
        get() {
            if (counting == null && board.gameOver) {
                counting = CountingBoard(board)
            }
            return counting
        }
}
