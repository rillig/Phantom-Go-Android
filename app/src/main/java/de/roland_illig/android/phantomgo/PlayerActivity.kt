package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.Board
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.PlayerBoardView

class PlayerActivity : AppCompatActivity() {

    var state: GameState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setTitle(if (player == Player.BLACK) R.string.black_to_play else R.string.white_to_play)
    }

    override fun onResume() {
        super.onResume()
        state = GameState.load(this)
        findViewById<PlayerBoardView>(R.id.playerBoardView)
                .configure(state!!.refereeBoard, board, player)
    }

    override fun onPause() {
        super.onPause()
        GameState.save(this, state!!)
    }

    fun onPassClick(view: View) {
        (findViewById<PlayerBoardView>(R.id.playerBoardView)).pass()
        onHandOverClick(view)
    }

    fun onHandOverClick(view: View) {
        val refereeResults = findViewById<PlayerBoardView>(R.id.playerBoardView).refereeResults
        if (state!!.refereeBoard.gameOver) {
            CountingActivity.start(this)
        } else {
            HandOverActivity.start(this, player.other(), refereeResults)
        }
        finish()
    }

    private val board: Board
        get() = if (player == Player.BLACK) state!!.blackBoard else state!!.whiteBoard
    private val player: Player
        get() = intent.getSerializableExtra("phantomGo.player") as Player

    companion object {
        fun start(ctx: Context, player: Player) {
            val intent = Intent(ctx, PlayerActivity::class.java)
            intent.putExtra("phantomGo.player", player)
            ctx.startActivity(intent)
        }
    }
}
