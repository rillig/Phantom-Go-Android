package de.roland_illig.android.phantomgo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.Game
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.Referee
import de.roland_illig.phantomgo.RefereeResult

class PlayerBoardView : AbstractBoardView {

    internal var mode = R.id.playButton
    private var game: Game? = null

    private val board get() = if (game!!.turn == Player.BLACK) game!!.blackBoard else game!!.whiteBoard
    private val refereeBoard get() = game!!.refereeBoard
    private val refereeHistory get() = game!!.refereeHistory

    private var refereeText: CharSequence = ""
        set(text) {
            findParentView<TextView>(R.id.referee).text = text
        }

    override val boardSize get() = refereeBoard.size

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun configure(game: Game) {
        this.game = game
        updateViews()
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
        val turn = game!!.turn
        if (turn != game!!.refereeBoard.turn) {
            refereeText = resources.getText(R.string.not_your_turn)
            return
        }

        val result = refereeBoard.play(x, y)
        refereeHistory.add(Game.RefereeHistoryEntry(turn, result))

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

        updateViews()
    }

    private fun updateViews() {
        val game = this.game!!
        val last = game.refereeHistory.lastOrNull()
        if (last != null) {
            refereeText = Referee.comment(last.result, last.player, resources)
        }
        val done = game.turn != game.refereeBoard.turn
        findParentView<View>(R.id.passButton).isEnabled = !done
        findParentView<View>(R.id.handOverButton).isEnabled = done
    }

    private fun <T : View> findParentView(resourceId: Int): T = (parent as View).findViewById(resourceId)

    fun pass() {
        val turn = game!!.turn
        if (turn != refereeBoard.turn) {
            refereeText = resources.getText(R.string.not_your_turn)
            return
        }

        val result = refereeBoard.pass()
        refereeHistory.add(Game.RefereeHistoryEntry(turn, result))
        updateViews()
    }
}
