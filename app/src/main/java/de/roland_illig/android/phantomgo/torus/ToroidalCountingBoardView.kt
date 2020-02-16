package de.roland_illig.android.phantomgo.torus

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import de.roland_illig.android.phantomgo.AbstractBoardView
import de.roland_illig.android.phantomgo.R
import de.roland_illig.phantomgo.Intersection

class ToroidalCountingBoardView : AbstractBoardView {

    private var state = ToroidalState() // Just for the preview during development

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun configure(state: ToroidalState) {
        this.state = state
        updateSummary()
        invalidate()
    }

    override val boardSize get() = state.board.size

    override val highlightCross get() = false

    override fun getBoard(x: Int, y: Int): Cell {
        val countingBoard = state.countingBoard ?: return Cell(null, null, false, null)
        val stone = state.board[Intersection(x, y)]
        val territory = countingBoard.getTerritory(x, y)
        val dead = countingBoard.isDead(x, y)
        return Cell(stone, territory, dead, null)
    }

    override fun onBoardClicked(x: Int, y: Int) {
        if (state.board[Intersection(x, y)] == null) return
        state.countingBoard!!.toggleDead(x, y)
        updateSummary()
        invalidate()
    }

    private fun updateSummary() {
        val countingBoard = state.countingBoard ?: return
        val result = countingBoard.count()
        val summary = resources.getString(
            R.string.result_summary,
            result.blackTerritory, result.blackCaptured, result.blackScore,
            result.whiteTerritory, result.whiteCaptured, result.whiteScore
        )
        ((parent as View).findViewById<View>(R.id.countingSummary) as TextView).text = summary
    }
}
