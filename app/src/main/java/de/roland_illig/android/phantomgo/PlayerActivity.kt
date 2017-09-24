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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setTitle(if (player == Player.BLACK) R.string.black_to_play else R.string.white_to_play)
    }

    override fun onResume() {
        super.onResume()
        findViewById<PlayerBoardView>(R.id.playerBoardView)
                .configure(GameState.GLOBAL.refereeBoard, board, player)
    }

    fun onPassClick(view: View) {
        (findViewById<PlayerBoardView>(R.id.playerBoardView)).pass()
        onHandOverClick(view)
    }

    fun onHandOverClick(view: View) {
        val refereeResults = findViewById<PlayerBoardView>(R.id.playerBoardView).refereeResults
        HandOverActivity.start(this, player.other(), refereeResults)
        finish()
    }

    private val board: Board
        get() = if (player == Player.BLACK) GameState.GLOBAL.blackBoard else GameState.GLOBAL.whiteBoard
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
