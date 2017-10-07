package de.roland_illig.phantomgo

class Game : java.io.Serializable {
    var turn = Player.BLACK
    val refereeBoard = Board(9)
    val refereeHistory = mutableListOf<RefereeHistoryEntry>()
    private val blackBoard = Board(9)
    private val whiteBoard = Board(9)
    private var countingBoard: CountingBoard? = null

    fun playerBoard() = if (turn == Player.BLACK) blackBoard else whiteBoard

    fun countingBoard(): CountingBoard {
        if (countingBoard == null) {
            countingBoard = CountingBoard(refereeBoard)
        }
        return countingBoard!!
    }

    class RefereeHistoryEntry(val player: Player, val result: RefereeResult) : java.io.Serializable
}