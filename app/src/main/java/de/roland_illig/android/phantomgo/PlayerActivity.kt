package de.roland_illig.android.phantomgo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import de.roland_illig.phantomgo.PhantomState
import de.roland_illig.phantomgo.Player

/**
 * It's one of the players' turn.
 * The current player can either play or take notes on the board by
 * placing black and white stones.
 */
class PlayerActivity : AppCompatActivity() {

    private lateinit var state: PhantomState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phantom_player)

        onToolClick(findViewById(R.id.playButton))
    }

    override fun onResume() {
        super.onResume()
        val state = Persistence.loadPhantomGo(this)
        this.state = state
        setTitle(if (state.turn == Player.BLACK) R.string.black_to_play else R.string.white_to_play)
        boardView().configure(state)
    }

    override fun onPause() {
        super.onPause()
        Persistence.savePhantomGo(this, state)
    }

    fun onToolClick(view: View) {
        boardView().mode = view.id

        // This is a brute-force approach at making the currently selected
        // tool button visible. For sure there is a better approach than
        // defining the colors here and looping manually over the buttons.
        val group = view.parent as RadioGroup
        for (i in 0 until group.childCount) {
            val box = group.getChildAt(i)
            val color = if (box == view) 0x55000000 else 0x33000000
            box.setBackgroundColor(color)
        }
    }

    fun onResignClick(view: View) {
        ResignDialogFragment().show(supportFragmentManager, "")
    }

    private fun resign() {
        state = PhantomState()
        finish()
    }

    fun onPassClick(view: View) {
        boardView().pass()
        onContinueClick(view)
    }

    fun onContinueClick(view: View) {
        if (state.isGameOver) {
            CountingActivity.start(this)
        } else {
            state.finishMove()
            HandOverActivity.start(this)
        }
        finish()
    }

    private fun boardView() = findViewById<PlayerBoardView>(R.id.playerBoardView)

    companion object {
        fun start(ctx: Context) {
            ctx.startActivity(Intent(ctx, PlayerActivity::class.java))
        }
    }

    class ResignDialogFragment : DialogFragment() {

        private lateinit var ctx: PlayerActivity

        override fun onAttach(context: Context) {
            super.onAttach(context)
            ctx = context as PlayerActivity
        }

        override fun onCreateDialog(savedInstanceState: Bundle?) =
            AlertDialog.Builder(activity!!).run {
                setMessage(getString(R.string.resign_question))
                setPositiveButton(R.string.resign_button) { _, _ -> ctx.resign() }
                create()!!
            }
    }
}
