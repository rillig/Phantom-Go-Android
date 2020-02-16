package de.roland_illig.android.phantomgo

import android.content.Context
import android.util.AttributeSet
import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.Intersection
import de.roland_illig.phantomgo.RefereeResult

class SimpleBoardView : AbstractBoardView {

    private lateinit var board: Board
    private lateinit var boardUpdated: () -> Unit

    override val boardSize get() = board.size

    override val highlightCross get() = true

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun connect(board: Board, boardUpdated: () -> Unit) {
        this.board = board
        this.boardUpdated = boardUpdated
        boardUpdated()
        invalidate()
    }

    override fun getBoard(x: Int, y: Int): Cell {
        val marker = board.lastMove.let {
            if (it == Intersection(x, y)) Marker.CIRCLE else null
        }
        return Cell(board[Intersection(x, y)], null, false, marker)
    }

    override fun onBoardClicked(x: Int, y: Int) {
        if (board.gameOver) return
        if (board.play(Intersection(x, y)) is RefereeResult.Invalid) return
        boardUpdated()
        invalidate()
    }

    fun pass() {
        board.pass()
        boardUpdated()
        invalidate()
    }
}
