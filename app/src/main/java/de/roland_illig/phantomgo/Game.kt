package de.roland_illig.phantomgo

class Game(val size: Int = 9) : java.io.Serializable {
    var turn = Player.BLACK; private set
    private val refereeBoard = Board(size)
    val refereeHistory = mutableListOf<RefereeHistoryEntry>()
    val blackBoard = Board(size)
    val whiteBoard = Board(size)
    private var countingBoard: CountingBoard? = null

    fun playerBoard() = if (turn == Player.BLACK) blackBoard else whiteBoard

    fun countingBoard(): CountingBoard {
        if (countingBoard == null) {
            countingBoard = CountingBoard(refereeBoard)
        }
        return countingBoard!!
    }

    fun isReadyToHandOver() = turn != refereeBoard.turn

    fun finishMove() {
        turn = refereeBoard.turn
    }

    fun play(x: Int, y: Int): RefereeResult {
        val board = playerBoard()
        val result = refereeBoard.play(x, y)
        refereeHistory.add(RefereeHistoryEntry(turn, result))

        when (result.invalidReason) {
            RefereeResult.InvalidReason.OTHER_STONE -> board[x, y] = turn.other()
            RefereeResult.InvalidReason.OWN_STONE -> board[x, y] = turn
            RefereeResult.InvalidReason.SUICIDE,
            RefereeResult.InvalidReason.KO -> board[x, y] = null
            null -> {
                val playerResult = board.copy().also { it.turn = turn }.play(x, y)
                if (playerResult.toString() == result.toString()) {
                    board.turn = turn
                    board.play(x, y)
                } else {
                    board[x, y] = turn
                }
            }
        }

        return result
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
