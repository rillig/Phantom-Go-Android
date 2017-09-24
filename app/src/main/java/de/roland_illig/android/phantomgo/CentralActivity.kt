package de.roland_illig.android.phantomgo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.Player

class CentralActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_central)
    }

    fun onPlayClick(view: View) {
        val activity = if (GameState.GLOBAL.refereeBoard.turn == Player.BLACK)
            BlackActivity::class.java
        else
            WhiteActivity::class.java
        startActivity(Intent(this, activity))
    }

    fun onSettingsClick(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}
