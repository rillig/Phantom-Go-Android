package de.roland_illig.android.phantomgo.torus

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

class ToroidalPlayActivity : AppCompatActivity() {

    private lateinit var state: ToroidalState
    private val board: Board get() = state.board

    private val boardView get() = findViewById<SimpleBoardView>(R.id.simple_board_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toroidal_play)
    }

    override fun onResume() {
        super.onResume()
        state = Persistence.loadToroidalGo(this)
        boardView.connect(board, this::boardUpdated)
        maybeSwitchToCounting()
    }

    private fun boardUpdated() {
        setTitle(if (board.turn == Player.BLACK) R.string.black_to_play else R.string.white_to_play)
        findViewById<View>(R.id.passButton).isEnabled = !board.gameOver
        findViewById<View>(R.id.resignButton).isEnabled = !board.gameOver
    }

    override fun onPause() {
        super.onPause()
        Persistence.saveToroidalGo(this, state)
    }

    fun onResignClick(view: View) {
        ResignDialogFragment().show(supportFragmentManager, "")
    }

    internal fun resign() {
        state = ToroidalState()
        boardView.connect(board, this::boardUpdated)
    }

    fun onPassClick(view: View) {
        boardView.pass()
        maybeSwitchToCounting()
    }

    private fun maybeSwitchToCounting() {
        if (!board.gameOver) return
        ToroidalCountingActivity.start(this)
        finish()
    }

    class ResignDialogFragment : DialogFragment() {

        private lateinit var ctx: ToroidalPlayActivity

        override fun onAttach(context: Context) {
            super.onAttach(context)
            ctx = context as ToroidalPlayActivity
        }

        override fun onCreateDialog(savedInstanceState: Bundle?) =
            AlertDialog.Builder(activity!!).run {
                setMessage(getString(R.string.resign_question))
                setPositiveButton(R.string.resign_button) { _, _ -> ctx.resign() }
                create()!!
            }
    }

    companion object {
        fun start(ctx: Context) {
            ctx.startActivity(Intent(ctx, ToroidalPlayActivity::class.java))
        }
    }
}
