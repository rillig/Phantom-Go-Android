package de.roland_illig.android.phantomgo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.Player
import de.roland_illig.phantomgo.PlayerBoardView

class WhiteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_white)
    }

    override fun onResume() {
        super.onResume()
        findViewById<PlayerBoardView>(R.id.playerBoardView)
                .configure(GameState.GLOBAL.refereeBoard, GameState.GLOBAL.whiteBoard, Player.WHITE)
    }

    fun onHandOverClick(view: View) {
        val intent = Intent(this, HandOverActivity::class.java)
        intent.putExtra("phantomGo.handOverTo", Player.BLACK)
        startActivity(intent)
        finish()
    }
}
