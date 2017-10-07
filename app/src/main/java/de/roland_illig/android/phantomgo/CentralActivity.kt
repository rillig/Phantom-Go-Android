package de.roland_illig.android.phantomgo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class CentralActivity : AppCompatActivity() {

    private var state: GameState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_central)
    }

    override fun onResume() {
        super.onResume()
        state = GameState.load(this)
    }

    fun onPlayClick(view: View) {
        val refereeBoard = state!!.refereeBoard
        if (refereeBoard.gameOver) {
            CountingActivity.start(this)
        } else if (refereeBoard.empty) {
            PlayerActivity.start(this)
        } else {
            HandOverActivity.start(this)
        }
    }
}
