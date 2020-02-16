package de.roland_illig.android.phantomgo.plain

import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.CountingBoard

class PlainState(
    size: Int = 9
) : java.io.Serializable {
    val board = Board(size)
    private var counting: CountingBoard? = null
    val countingBoard: CountingBoard?
        get() {
            if (counting == null && board.gameOver) {
                counting = CountingBoard(board)
            }
            return counting
        }
}
