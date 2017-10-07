package de.roland_illig.android.phantomgo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import de.roland_illig.phantomgo.Game

class CountingBoardView : AbstractBoardView {

    private var game: Game? = null
    private val countingBoard get() = game!!.countingBoard()

    override val boardSize get() = game!!.size

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun configure(game: Game) {
        this.game = game
        updateSummary()
    }

    override fun getBoard(x: Int, y: Int): AbstractBoardView.Cell {
        val stone = game!!.getRefereeBoard(x, y)
        val territory = countingBoard.getTerritory(x, y)
        val dead = countingBoard.isDead(x, y)
        return AbstractBoardView.Cell(stone, territory, dead)
    }

    override fun onBoardClicked(x: Int, y: Int) {
        if (game!!.getRefereeBoard(x, y) != null) {
            countingBoard.toggleDead(x, y)
            updateSummary()
            invalidate()
        }
    }

    private fun updateSummary() {
        val result = countingBoard.count()
        val summary = resources.getString(R.string.result_summary,
                result.blackTerritory, result.blackCaptured, result.blackScore,
                result.whiteTerritory, result.whiteCaptured, result.whiteScore)
        ((parent as View).findViewById<View>(R.id.countingSummary) as TextView).text = summary
    }
}
