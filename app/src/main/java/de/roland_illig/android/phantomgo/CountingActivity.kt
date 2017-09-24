package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.roland_illig.phantomgo.CountingBoardView

class CountingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counting)
    }

    override fun onResume() {
        super.onResume()
        findViewById<CountingBoardView>(R.id.countingBoard).configure(GameState.GLOBAL.refereeBoard)
    }

    companion object {
        fun start(ctx: Context) {
            val intent = Intent(ctx, CountingActivity::class.java)
            ctx.startActivity(intent)
        }
    }
}
