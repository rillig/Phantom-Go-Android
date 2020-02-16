package de.roland_illig.android.phantomgo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import de.roland_illig.phantomgo.PhantomState
import de.roland_illig.phantomgo.Intersection
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.Referee

class PlayerBoardView : AbstractBoardView {

    internal var mode = R.id.playButton
    private lateinit var state: PhantomState

    private fun getBoard() = state.playerBoard()

    private fun setRefereeText(text: CharSequence) {
        findParentView<TextView>(R.id.referee).text = text
    }

    override val boardSize get() = state.size

    override val highlightCross get() = !state.isReadyToHandOver()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun configure(state: PhantomState) {
        this.state = state
        updateViews()
    }

    override fun getBoard(x: Int, y: Int) = Cell(getBoard()[Intersection(x, y)], null, false)

    override fun onBoardClicked(x: Int, y: Int) {
        val board = this.getBoard()
        when (mode) {
            R.id.playButton -> onPlayModeClick(x, y)
            R.id.blackButton -> board.edit(Intersection(x, y), Player.BLACK)
            R.id.whiteButton -> board.edit(Intersection(x, y), Player.WHITE)
            R.id.eraserButton -> board[x, y] = null
        }
        invalidate()
    }

    private fun onPlayModeClick(x: Int, y: Int) {
        if (state.isReadyToHandOver()) {
            setRefereeText(resources.getText(R.string.not_your_turn))
            return
        }

        state.play(x, y)
        updateViews()
    }

    fun pass() {
        if (state.isReadyToHandOver()) {
            setRefereeText(resources.getText(R.string.not_your_turn))
            return
        }

        state.pass()
        updateViews()
    }

    private fun updateViews() {
        val last = state.refereeHistory.lastOrNull()
        if (last != null) {
            setRefereeText(Referee.comment(last.result, last.player, resources))
        }
        val done = state.isReadyToHandOver()
        findParentView<View>(R.id.passButton).isEnabled = !done
        findParentView<View>(R.id.handOverButton).isEnabled = done
    }

    private fun <T : View> findParentView(resourceId: Int): T = (parent as View).findViewById(resourceId)
}
