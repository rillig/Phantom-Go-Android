package de.roland_illig.android.phantomgo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.Referee
import de.roland_illig.phantomgo.RefereeResult

class PlayerBoardView : AbstractBoardView {

    internal var mode = R.id.playButton
    private var player = Player.BLACK
    private var state: GameState? = null

    private val board get() = if (player == Player.BLACK) state!!.blackBoard else state!!.whiteBoard
    private val refereeBoard get() = state!!.refereeBoard
    private val refereeHistory get() = state!!.refereeHistory

    override val boardSize get() = refereeBoard.size

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun configure(state: GameState) {
        this.state = state
        this.player = state.refereeBoard.turn

        updateViews(null)
    }

    override fun getBoard(x: Int, y: Int) = AbstractBoardView.Cell(board[x, y], null, false)

    override fun onBoardClicked(x: Int, y: Int) {
        val board = this.board
        when (mode) {
            R.id.playButton -> onPlayModeClick(board, x, y)
            R.id.blackButton -> {
                board.turn = Player.BLACK
                if (board.play(x, y).invalidReason != null) {
                    board[x, y] = Player.BLACK
                }
            }
            R.id.whiteButton -> {
                board.turn = Player.WHITE
                if (board.play(x, y).invalidReason != null) {
                    board[x, y] = Player.WHITE
                }
            }
            R.id.eraserButton -> board[x, y] = null
        }
        invalidate()
    }

    private fun onPlayModeClick(board: Board, x: Int, y: Int) {
        if (player != refereeBoard.turn) {
            findParentView<TextView>(R.id.referee).setText(R.string.not_your_turn)
            return
        }

        val result = refereeBoard.play(x, y)
        refereeHistory.add(GameState.RefereeHistoryEntry(player, result))

        if (result.invalidReason != null) {
            when (result.invalidReason) {
                RefereeResult.InvalidReason.OTHER_STONE -> board[x, y] = player.other()
                RefereeResult.InvalidReason.OWN_STONE -> board[x, y] = player
                RefereeResult.InvalidReason.SUICIDE, RefereeResult.InvalidReason.KO -> board[x, y] = null
            }
        } else {
            val playerResult = board.copy().play(x, y)
            if (playerResult.toString() == result.toString()) {
                board.turn = player
                board.play(x, y)
            } else {
                board[x, y] = player
            }
        }

        updateViews(result)
    }

    private fun updateViews(result: RefereeResult?) {
        findParentView<TextView>(R.id.referee).text = if (result != null) Referee.comment(result, player, resources) else ""
        findParentView<View>(R.id.passButton).isEnabled = refereeBoard.turn == player
        findParentView<View>(R.id.handOverButton).isEnabled = refereeBoard.turn != player
    }

    private fun <T : View> findParentView(resourceId: Int): T = (parent as View).findViewById(resourceId)

    fun pass() {
        if (player != refereeBoard.turn) {
            findParentView<TextView>(R.id.referee).setText(R.string.not_your_turn)
            return
        }

        val result = refereeBoard.pass()
        refereeHistory.add(GameState.RefereeHistoryEntry(player, result))
        findParentView<TextView>(R.id.referee).text = Referee.comment(result, player, resources)
    }
}
