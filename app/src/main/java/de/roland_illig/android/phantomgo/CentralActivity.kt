package de.roland_illig.android.phantomgo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.Game

class CentralActivity : AppCompatActivity() {

    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_central)
    }

    override fun onResume() {
        super.onResume()
        game = Persistence.load(this)
    }

    fun onPlayClick(view: View) {
        when {
            game.isGameOver -> CountingActivity.start(this)
            game.isInitial -> PlayerActivity.start(this)
            else -> HandOverActivity.start(this)
        }
    }
}
