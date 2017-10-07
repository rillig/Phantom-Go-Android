package de.roland_illig.android.phantomgo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import de.roland_illig.phantomgo.Game

class CountingActivity : AppCompatActivity() {

    private var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counting)
    }

    override fun onResume() {
        super.onResume()
        val game = Persistence.load(this)
        this.game = game
        findViewById<CountingBoardView>(R.id.countingBoard).configure(game)
    }

    override fun onPause() {
        super.onPause()
        Persistence.save(this, game!!)
    }

    fun onFinishClick(view: View) {
        game = Game()
        finish()
    }

    companion object {
        fun start(ctx: Context) {
            val intent = Intent(ctx, CountingActivity::class.java)
            ctx.startActivity(intent)
        }
    }
}
