package de.roland_illig.android.phantomgo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
        onPlayClick()
        finish()
    }

    fun onPlayClick(view: View) = onPlayClick()

    private fun onPlayClick() {
        when {
            game.isGameOver -> CountingActivity.start(this)
            game.isInitial -> PlayerActivity.start(this)
            else -> HandOverActivity.start(this)
        }
    }
}
