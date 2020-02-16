package de.roland_illig.phantomgo

class PhantomState(val size: Int = 9) : java.io.Serializable {
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
        val pos = Intersection(x, y)
        val board = playerBoard()
        val result = refereeBoard.play(pos)
        refereeHistory.add(RefereeHistoryEntry(turn, result))

        when (result) {
            is RefereeResult.OtherStone -> board[x, y] = turn.other()
            is RefereeResult.OwnStone -> board[x, y] = turn
            is RefereeResult.Suicide,
            is RefereeResult.Ko -> board[x, y] = null
            is RefereeResult.Ok -> {
                val playerResult = board.copy().also { it.turn = turn }.play(pos)
                if ("$playerResult" == "$result") {
                    board.turn = turn
                    board.play(pos)
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

    fun getRefereeBoard(x: Int, y: Int) = refereeBoard[Intersection(x, y)]

    class RefereeHistoryEntry(val player: Player, val result: RefereeResult) : java.io.Serializable
}
