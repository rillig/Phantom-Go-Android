package de.roland_illig.android.phantomgo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.CountingBoard

class CountingBoardView : AbstractBoardView {

    private var board: Board? = null
    private var countingBoard: CountingBoard? = null

    override val boardSize: Int
        get() = board!!.size

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun configure(board: Board, countingBoard: CountingBoard) {
        this.board = board
        this.countingBoard = countingBoard
        updateSummary()
    }

    override fun getBoard(x: Int, y: Int): AbstractBoardView.Cell {
        val stone = board!![x, y]
        val territory = countingBoard!!.getTerritory(x, y)
        val dead = countingBoard!!.isDead(x, y)
        return AbstractBoardView.Cell(stone, territory, dead)
    }

    override fun onBoardClicked(x: Int, y: Int) {
        if (board!![x, y] != null) {
            countingBoard!!.toggleDead(x, y)
            updateSummary()
            invalidate()
        }
    }

    private fun updateSummary() {
        val result = countingBoard!!.count()
        val summary = resources.getString(R.string.result_summary,
                result.blackTerritory, result.blackCaptured, result.blackScore,
                result.whiteTerritory, result.whiteCaptured, result.whiteScore)
        ((parent as View).findViewById<View>(R.id.countingSummary) as TextView).text = summary
    }
}
