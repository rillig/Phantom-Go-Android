package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.Player

class PlayerActivity : AppCompatActivity() {

    private var state: GameState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }

    override fun onResume() {
        super.onResume()
        val state = GameState.load(this)
        this.state = state
        setTitle(if (state.turn == Player.BLACK) R.string.black_to_play else R.string.white_to_play)
        boardView().configure(state)
    }

    override fun onPause() {
        super.onPause()
        GameState.save(this, state!!)
    }

    fun onToolClick(view: View) {
        boardView().mode = view.id
    }

    fun onPassClick(view: View) {
        boardView().pass()
        onContinueClick(view)
    }

    fun onContinueClick(view: View) {
        if (state!!.refereeBoard.gameOver) {
            CountingActivity.start(this)
        } else {
            state!!.turn = state!!.refereeBoard.turn
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
}
