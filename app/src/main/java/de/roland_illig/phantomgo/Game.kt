package de.roland_illig.phantomgo

class Game : java.io.Serializable {
    val size = 9
    var turn = Player.BLACK; private set
    private val refereeBoard = Board(size)
    val refereeHistory = mutableListOf<RefereeHistoryEntry>()
    private val blackBoard = Board(size)
    private val whiteBoard = Board(size)
    private var countingBoard: CountingBoard? = null

    fun playerBoard() = if (turn == Player.BLACK) blackBoard else whiteBoard

    fun countingBoard(): CountingBoard {
        if (countingBoard == null) {
            countingBoard = CountingBoard(refereeBoard)
        }
        return countingBoard!!
    }

    fun isMoveDone() = turn != refereeBoard.turn

    fun finishMove() {
        turn = refereeBoard.turn
    }

    fun play(x: Int, y: Int): RefereeResult {
        return refereeBoard.play(x, y).also {
            refereeHistory.add(RefereeHistoryEntry(turn, it))
        }
    }

    fun pass() {
        val result = refereeBoard.pass()
        refereeHistory.add(RefereeHistoryEntry(turn, result))
    }

    val isGameOver get() = refereeBoard.gameOver
    val isInitial get() = refereeBoard.empty

    fun getRefereeBoard(x: Int, y: Int) = refereeBoard[x, y]

    class RefereeHistoryEntry(val player: Player, val result: RefereeResult) : java.io.Serializable
}
