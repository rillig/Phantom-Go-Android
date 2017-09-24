package de.roland_illig.android.phantomgo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class CentralActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_central)
    }

    fun onPlayClick(view: View) {
        if (GameState.GLOBAL.refereeBoard.isGameOver) {
            CountingActivity.start(this)
        } else {
            PlayerActivity.start(this, GameState.GLOBAL.refereeBoard.turn)
        }
    }
}
