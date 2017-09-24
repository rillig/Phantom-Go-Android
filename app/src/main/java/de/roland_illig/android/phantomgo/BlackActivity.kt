package de.roland_illig.android.phantomgo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.PlayerBoardView

class BlackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_black)
    }

    override fun onResume() {
        super.onResume()
        findViewById<PlayerBoardView>(R.id.playerBoardView)
                .configure(GameState.GLOBAL.refereeBoard, GameState.GLOBAL.blackBoard, Player.BLACK)
    }

    fun onHandOverClick(view: View) {
        val refereeResults = findViewById<PlayerBoardView>(R.id.playerBoardView).refereeResults
        HandOverActivity.start(this, Player.WHITE, refereeResults)
        finish()
    }
}
