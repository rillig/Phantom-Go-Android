package de.roland_illig.android.phantomgo.plain

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import de.roland_illig.android.phantomgo.Persistence
import de.roland_illig.android.phantomgo.R
import de.roland_illig.android.phantomgo.SimpleBoardView
import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.Player

class PlainPlayActivity : AppCompatActivity() {

    private lateinit var state: PlainState
    private val board: Board get() = state.board

    private val boardView get() = findViewById<SimpleBoardView>(R.id.simple_board_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plain_play)
    }

    override fun onResume() {
        super.onResume()
        updateState(Persistence.loadPlainGo(this))
        maybeSwitchToCounting()
    }

    private fun boardUpdated() {
        setTitle(if (board.turn == Player.BLACK) R.string.black_to_play else R.string.white_to_play)
        findViewById<View>(R.id.passButton).isEnabled = !board.gameOver
        findViewById<View>(R.id.resignButton).isEnabled = !board.gameOver
        save()
    }

    override fun onPause() {
        super.onPause()
        save()
    }

    fun onResignClick(view: View) = ResignDialog().show(supportFragmentManager, "")

    private fun resign() {
        updateState(PlainState())
        save()
    }

    private fun updateState(state: PlainState) {
        this.state = state
        boardView.connect(board, this::boardUpdated)
    }

    private fun save() = Persistence.savePlainGo(this, state)

    fun onPassClick(view: View) {
        boardView.pass()
        maybeSwitchToCounting()
    }

    private fun maybeSwitchToCounting() {
        if (!board.gameOver) return
        PlainCountingActivity.start(this)
        finish()
    }

    class ResignDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
            val ctx = activity as PlainPlayActivity
            return AlertDialog.Builder(ctx).run {
                setMessage(getString(R.string.resign_question))
                setPositiveButton(R.string.resign_button) { _, _ -> ctx.resign() }
                create()!!
            }
        }
    }

    companion object {
        fun start(ctx: Context) {
            ctx.startActivity(Intent(ctx, PlainPlayActivity::class.java))
        }
    }
}
