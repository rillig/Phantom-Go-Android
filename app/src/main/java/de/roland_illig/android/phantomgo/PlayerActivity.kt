package de.roland_illig.android.phantomgo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.Game
import de.roland_illig.phantomgo.Player

/**
 * It's one of the players' turn.
 * The current player can either play or take notes on the board by
 * placing black and white stones.
 */
class PlayerActivity : AppCompatActivity() {

    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }

    override fun onResume() {
        super.onResume()
        val game = Persistence.load(this)
        this.game = game
        setTitle(if (game.turn == Player.BLACK) R.string.black_to_play else R.string.white_to_play)
        boardView().configure(game)
    }

    override fun onPause() {
        super.onPause()
        Persistence.save(this, game)
    }

    fun onToolClick(view: View) {
        boardView().mode = view.id
    }

    fun onResignClick(view: View) {
        ResignDialogFragment().show(supportFragmentManager, "")
    }

    internal fun resign() {
        game = Game()
        start(this)
        finish()
    }

    fun onPassClick(view: View) {
        boardView().pass()
        onContinueClick(view)
    }

    fun onContinueClick(view: View) {
        if (game.isGameOver) {
            CountingActivity.start(this)
        } else {
            game.finishMove()
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

        override fun onAttach(context: Context?) {
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
