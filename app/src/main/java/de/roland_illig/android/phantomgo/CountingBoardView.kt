package de.roland_illig.android.phantomgo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import de.roland_illig.phantomgo.PhantomState

class CountingBoardView : AbstractBoardView {

    private var state = PhantomState(9) // Just for the preview during development
    private val countingBoard get() = state.countingBoard()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun configure(state: PhantomState) {
        this.state = state
        updateSummary()
    }

    override val boardSize get() = state.size

    override val highlightCross get() = false

    override fun getBoard(x: Int, y: Int): Cell {
        val stone = state.getRefereeBoard(x, y)
        val territory = countingBoard.getTerritory(x, y)
        val dead = countingBoard.isDead(x, y)
        return Cell(stone, territory, dead, null)
    }

    override fun onBoardClicked(x: Int, y: Int) {
        if (state.getRefereeBoard(x, y) != null) {
            countingBoard.toggleDead(x, y)
            updateSummary()
            invalidate()
        }
    }

    private fun updateSummary() {
        val result = countingBoard.count()
        val summary = resources.getString(
            R.string.result_summary,
            result.blackTerritory, result.blackCaptured, result.blackScore,
            result.whiteTerritory, result.whiteCaptured, result.whiteScore
        )
        ((parent as View).findViewById<View>(R.id.countingSummary) as TextView).text = summary
    }
}
