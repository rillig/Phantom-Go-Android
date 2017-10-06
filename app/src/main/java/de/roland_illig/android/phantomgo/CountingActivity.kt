package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.CountingBoard

class CountingActivity : AppCompatActivity() {

    var state: GameState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counting)
    }

    override fun onResume() {
        super.onResume()
        state = GameState.load(this)
        if (state!!.countingBoard == null) {
            state!!.countingBoard = CountingBoard(state!!.refereeBoard)
        }
        findViewById<CountingBoardView>(R.id.countingBoard).configure(
                state!!.refereeBoard,
                state!!.countingBoard!!)
    }

    override fun onPause() {
        super.onPause()
        GameState.save(this, state!!)
    }

    fun onFinishClick(view: View) {
        state = GameState()
        finish()
    }

    companion object {
        fun start(ctx: Context) {
            val intent = Intent(ctx, CountingActivity::class.java)
            ctx.startActivity(intent)
        }
    }
}
