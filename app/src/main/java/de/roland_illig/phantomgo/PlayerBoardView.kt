package de.roland_illig.phantomgo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import de.roland_illig.android.phantomgo.R
import java.util.*

class PlayerBoardView : AbstractBoardView {

    private var refereeBoard = Board(9)
    private var board = Board(9)
    private var player = Player.BLACK
    internal val refereeResults = ArrayList<RefereeResult>()

    override val boardSize: Int
        get() = board.size

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun configure(refereeBoard: Board, board: Board, player: Player) {
        this.refereeBoard = refereeBoard
        this.board = board
        this.player = player

        updateViews(null)
    }

    override fun getBoard(x: Int, y: Int) = AbstractBoardView.Cell(board[x, y], null, false)

    override fun boardMouseClicked(x: Int, y: Int) {
        if (isChecked(R.id.playButton)) {
            onPlayModeClick(x, y)
        } else if (isChecked(R.id.blackButton)) {
            board.turn = Player.BLACK
            if (board.play(x, y).invalidReason != null) {
                board[x, y] = Player.BLACK
            }
        } else if (isChecked(R.id.whiteButton)) {
            board.turn = Player.WHITE
            if (board.play(x, y).invalidReason != null) {
                board[x, y] = Player.WHITE
            }
        } else if (isChecked(R.id.eraserButton)) {
            board[x, y] = null
        }
        invalidate()
    }

    private fun isChecked(resourceId: Int) = findParentView<RadioButton>(resourceId).isChecked

    private fun onPlayModeClick(x: Int, y: Int) {
        if (player != refereeBoard.turn) {
            findParentView<TextView>(R.id.referee).setText(R.string.not_your_turn)
            return
        }

        val result = refereeBoard.play(x, y)
        refereeResults.add(result)

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
        refereeResults.add(result)
        findParentView<TextView>(R.id.referee).text = Referee.comment(result, player, resources)
    }
}
